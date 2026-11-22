HTSM: Host Transversal Surface Map Protocol Part A

Boundary: The only Host AI ingress to the OS/10 (Operating System 10) Agentic Micro Kernel System
Protocol version: HTSM/1
Spec version: 1.0.1-draft (corrected from 1.0.0-draft; see Appendix B)
Companions: os10-lifecycle.md (all state machines), amk-defaults-manifest.md (every default value, referenced here as DV-... IDs)
Authority: This guide is authoritative for the HTSM protocol. Where an older document (including controller/spec/amk-map.md) disagrees, this guide wins. Reconciling the older documents is pending and is not part of this guide.
Normative language: MUST, MUST NOT, SHOULD and MAY are used as in RFC 2119. No default value is stated in this guide; each is a DV- ID defined only in the defaults manifest.
Self-containment: A conforming deployment MUST publish the resolved values of every DV- ID it references alongside this spec, in a machine-readable form, so the deployment is auditable without the companion manifest. This guide remains the normative reference for which defaults are required; the companion manifest remains the source of what they are.

---

1. Purpose and scope

HTSM is the baseline method for any Host AI to communicate with the controller. It represents the whole operating system: the kernel and every runtime attached to a session. It is built for the weakest plausible AI host, which can only issue HTTPS GET requests, read JSON, and copy short opaque tokens.

In scope: the Host AI to controller channel, its addressing, parameters, authentication, console turns as seen by the AI, the surface structure, and errors.

Out of scope: console to controller communication (signed envelopes, defined in amk-contracts §6 and amk-console), runtime to controller registration, capability token use by adapters, the content of runtime subtrees, and the dispatch and completion of effects (§12).

Non-goals: proving that the AI is who it claims to be (no such proof exists), confidentiality beyond TLS, and authorizing effects. An HTSM request is a proposal. Effects require operator authorization and a capability token.

Layer separation: This protocol defines the control plane only. The effect path (§12) is deliberately external to HTSM and may be hosted independently of the ingress. A deployment may run the ingress on one platform and the effect dispatch on another; HTSM makes no claim that a single platform hosts both.

2. Terms

Term Meaning
Surface The tree of nodes one session sees. One surface per session generation.
Node One addressable unit: content, links, or a request template.
Handle Opaque, session-scoped, controller-minted identifier of a node. A credential.
Subtree A branch of the surface owned by one party: the kernel, or one runtime.
Projection The surface assembled for one session from the kernel subtree plus the subtrees the operator authorized for it.
Session One operator-created context for one Host AI conversation. Its states are CREATED, ACTIVE, PENDING_CONSOLE, DEGRADED, PAUSED, HALTED, and the terminal CLOSED, EXPIRED, REVOKED and SUPERSEDED, all defined in os10-lifecycle.md §6.
Generation A numbered incarnation of a session. A re-handoff starts a new generation.
Chain The ordered sequence of authenticated steps in one generation.
Continuation The single-use token that authorizes exactly the next step.
Handoff The line the operator pastes into the AI conversation to start or resume a chain.
Console turn An operator action, over the console channel, required before the chain may continue.
Proposal A recorded request. Never an effect.
Effect A state change outside HTSM, dispatched to a runtime or adapter under a capability token (§12).
Level The operational level the operator granted the session (§14).

3. Protocol rules

ID Rule Contracts invariant
HTSM-01 HTSM is the baseline Host AI ingress to the controller. Other methods can be used at the runtime level based on the host AI capabilities. However, no other Host AI method must ever exist at the kernel level. INV-29
HTSM-02 The AI holds no secret and computes nothing. A request carries only a handle, a step, a continuation and declared parameters. INV-30
HTSM-03 A GET never executes. A request node records a proposal. A GET on a request node is effect-safe: it changes only the recorded proposal state, never any state outside the session's own chain and proposal log. INV-31
HTSM-04 Operations reserved to the console have no node. They are unrepresentable, not rejected. INV-32
HTSM-05 Parameter values are untrusted data, validated against the node's declared schema. Anything outside it is rejected. INV-33
HTSM-06 The kernel subtree is controller-owned. No runtime may define or shadow a kernel node. INV-34
HTSM-07 Handles and continuations are credentials: controller-minted, session-bound, short-lived, and (for continuations) single-use. INV-26
HTSM-08 Errors and responses reveal nothing beyond the session's own projection. n/a
HTSM-09 The AI changes session state only by advancing the chain. Every other state change comes from the console or the controller. n/a
HTSM-10 Any verification failure produces an error and no side effect. INV-18
HTSM-11 While the deployment is not READY, every request receives only unavailable. n/a
HTSM-12 The operational level is set by the operator. The AI cannot claim or raise it. n/a

Note on "safe" (HTSM-03). RFC 9110 §9.2.1 defines a GET as safe when it does not change origin-server state. HTSM's GET does record a proposal, which is a change to the session's proposal log. HTSM's claim is therefore effect-safety, not RFC-safety: the GET cannot cause any state change outside the session's own chain and proposal log, and it cannot cause any effect. Implementations MUST NOT expose a GET that can cause an effect, and MUST NOT describe an HTSM GET as RFC-9110 "safe" without the effect-safety qualifier.

A host-side wrapper (for example a tool that performs the GET on the AI's behalf) is permitted only as a pass-through of HTSM requests. It MUST NOT add a second protocol, a second endpoint, or any semantics. (This resolves the single-ingress question as a default; it is open to the operator's override.)

4. Actors and channels

Actor Talks to the controller by Authority
Host AI HTSM only None. Untrusted.
Console (operator) Signed console envelopes Sole source of human authorization
Runtime Registration protocol (separate spec) Untrusted until authenticated and authorized
Adapter Capability tokens Acts only within a token's scope

5. Transport

· HTTPS GET only, TLS required. Any other method on an HTSM path returns bad_request. Plain HTTP is never accepted.
· Responses are application/json; charset=utf-8.
· Every response carries Cache-Control: no-store and Referrer-Policy: no-referrer. The controller never sets cookies and never issues redirects.
· A response never exceeds DV-HTSM-RESPONSE-MAX-BYTES. A request URL never exceeds DV-HTSM-URL-MAX-BYTES.
· The ingress defined by this section is separable from the effect dispatch defined in §12. A deployment MAY host them on different platforms, and the ingress MAY be hosted on a platform that does not expose transport fingerprints (see §10.5).

6. Addressing

```
GET {base}/htsm/1/n/{handle}?step={n}&continuation={token}[&{declared params}]
```

· base is given in the handoff. htsm/1 is the protocol version path.
· step and continuation are reserved and always required. A declared parameter MUST NOT use a reserved name.
· Parameter names and values follow strict percent-encoding. The controller rejects duplicate keys, unknown keys, missing required keys, malformed encoding, and any URL longer than the limit.
· The handoff and every response give the AI the exact URL template to fill (§9.3), so the AI never has to invent a URL.

7. Node model

Field Meaning
handle Opaque, at least DV-HTSM-HANDLE-ENTROPY-BITS of entropy, unique within the session
type One of the registry types in §7.1
title Short human-readable label
owner kernel or runtime:{id}
released Whether the operator has released the node to the session
min_level Lowest operational level that may use the node
hash SHA-256 of the node content, so the controller and console can compare what the AI saw
content Bounded by DV-HTSM-NODE-CONTENT-MAX-BYTES

7.1 Node types (registry v1)

Type Purpose
info Read-only content
index Lists child nodes, at most DV-HTSM-INDEX-MAX-CHILDREN
request Declares parameters; a GET records a proposal and returns a result node
result The outcome of a proposal (§9.5)
poll Read-only status that does not advance the chain (§11.3)

A new node type needs a registry version bump (§18). Surface depth never exceeds DV-HTSM-DEPTH-MAX.

8. Surface structure

8.1 Root and subtrees

The root is minted per session. It links to the kernel subtree and to each attached runtime subtree. Runtime subtrees live under the namespace runtime:{id}. A subtree never exceeds DV-SUBTREE-MAX-NODES nodes or DV-SUBTREE-MAX-BYTES of content.

8.2 Kernel subtree

The kernel subtree is controller-owned (HTSM-06). The nine kernel commands exist only as nodes (HTSM-01); two auxiliary nodes (kernel/proposals, kernel/turn) carry no command:

Node Command Type Notes
kernel/status status info Deployment and session state
kernel/identity identity info Kernel name and version, release epoch and hash, HTSM version, session level
kernel/state state info Compact summary: state, pending turn, pending proposals, blocking conditions
kernel/policy policy index Policy references and versions
kernel/capabilities capabilities index Each capability as declared, verified, unavailable, unknown, expired or revoked
kernel/runtimes runtime index Attached runtimes and their state (the runtime command lists runtimes)
kernel/query query request Parameters: capability (handle-ref), op (enum), arg (text)
kernel/verify verify request Parameter: target (enum of what the session may verify)
kernel/help help info Protocol and node documentation
kernel/proposals (none) index The session's proposals and their results
kernel/turn (none) poll Console turn status (§11.3)

Console-only operations (authorize, deny, revoke, activate, enroll, recover, and every session, surface and subtree lifecycle action) have no node (HTSM-04).

8.3 Runtime subtrees

Attached under §13. A runtime subtree MAY contain info, index, request and result nodes. It MUST NOT contain kernel-owned node paths, claim console-only operations, or declare a min_level lower than its risk class allows.

8.4 Projection

Each session sees only the kernel subtree plus the subtrees the operator authorized for it. Handles are minted per session and are not valid in any other session. Release state (released) is per session.

9. Parameters and requests

9.1 Parameter schema

A request node declares at most DV-HTSM-PARAMS-MAX-COUNT parameters. Each has name, type, required, and constraints. There is no free-form pattern type, so a node cannot ask the AI for arbitrary text beyond a bounded charset.

Type Constraint
enum The value MUST be one of the listed values
int Within declared min and max
bool true or false
slug Lowercase letters, digits, hyphen; declared max_len
hex Hexadecimal; declared max_len
b64url Base64url characters; declared max_len
text Letters, digits, space and - _ . , :; declared max_len, never above DV-HTSM-PARAM-TEXT-MAX-LEN
handle-ref A handle that exists in this session's projection

9.2 Strictness

The controller validates before it acts. A violation returns bad_request and has no side effect other than counting toward the attempt cap (§9.6). Parameters sent to a node that declares none are also bad_request.

9.3 Templates

Every link the AI may follow appears in next with an exact template:

```json
{ "rel": "ask", "type": "request", "title": "...", "handle": "H1",
  "template": "/htsm/1/n/H1?step={step}&continuation={continuation}&mode={mode}",
  "params": [ { "name": "mode", "type": "enum", "values": ["a","b"], "required": true } ] }
```

9.4 Semantics

A GET on a request node, once it passes verification (§10.5), records a proposal and returns a result node. It does not execute anything (HTSM-03). The proposal is bound to the session, the step, the node, a hash of the parameters, and the time.

9.5 Result states

pending (recorded, awaiting a decision), needs_console (a console turn is required), accepted (the operator authorized it; effects proceed outside HTSM), denied, expired (not decided within DV-PROPOSAL-PENDING-TTL-S), failed (the effect failed before completion, §12), revoked (the operator revoked an accepted proposal before the effect completed, §12). The AI reads results through kernel/proposals for DV-PROPOSAL-RESULT-RETENTION-S. A session holds at most DV-PROPOSAL-MAX-PENDING pending proposals.

9.6 Retries

· Identical repeat (non-poll). The same continuation, step and parameter hash within the token lifetime receives the stored response, marked X-HTSM-Replay: 1. This covers network retries and prefetching.
· Identical repeat (poll). Polls are idempotent reads and are excluded from the replay cache; each poll is served fresh from current chain state. The poll-only continuation is reused across polls until the turn completes, at which point it is voided and replaced by the normal continuation (§11.3).
· Different repeat. The same continuation (same value presented again) with a different step or parameters is fork_detected.
· Fresh continuation, wrong step. A continuation never seen before, presented with a step other than the expected next step, is step_out_of_order.
· Malformed request. A request rejected as bad_request does not consume the token. After DV-HTSM-BAD-REQUEST-ATTEMPTS rejections on one token, the token is consumed and a console turn is required (§11). Source rate limiting (check 3, §10.5) bounds the rate at which an attacker holding a captured token can force this outcome.

9.7 Large payloads

HTSM/1 has no staging. A request carries only values within the limits above. Larger content is passed by handle-ref to content the controller already holds.

10. Authentication: Traversal Chain Authentication

10.1 Principle

A request is authenticated only as the valid next step of its session's chain. The AI proves nothing about itself. The chain proves continuity: every step must present the continuation issued by the previous one.

10.2 Genesis and handoff

1. The operator starts a session in the console, choosing the level and, optionally, an intent.
2. The console derives session_id and a nonce and signs the genesis anchor:
   trail_0 = H(session_id || surface_version || release_ref || intent_hash || console_key_id || nonce || ts); sig_0 = Sign(console_key, trail_0).
   release_ref is the Release Manifest hash and epoch the session starts under.
3. The controller registers the session: id, generation, genesis, console public key, level, budgets, checkpoint policy, and the release_ref in force.
4. The console shows the handoff, and the operator pastes it into the AI conversation after the kernel prompt. An unused handoff expires after DV-SESSION-HANDOFF-TTL-S.

Handoff form: htsm=1 base={base} surface={surface_version} root={handle} continuation=G.{token}. A resume handoff (§11.5) has the same form with prefix R..

10.3 Continuations

Opaque, at least DV-HTSM-TOKEN-ENTROPY-BITS of entropy, single-use, bound to the session, generation, handle and step, and valid for DV-HTSM-TOKEN-TTL-S (the clock is paused while a console turn is pending). Returned in the JSON body and the X-HTSM-Continuation header. The controller stores only token hashes.

10.4 Chain state

Per session generation, held by the single-writer session object: chain head, step, surface version and revision, release reference, fingerprint log, turn state, replay cache, and attempt counters. The controller never lets two requests mutate one chain concurrently.

10.5 Verification order

Checks run in this order and the first failure wins. No step has a side effect unless noted.

# Check Failure
1 Deployment is READY unavailable
2 Protocol version supported unsupported_version
3 Source rate limit rate_limited
4 Request syntax: method, URL length, reserved parameters present bad_request
5 Resolve the handle to a session not_found (also counts against DV-HTSM-PROBE-RATE-PER-MIN)
6 Session rate limit rate_limited
7 Session state allows requests session_ended, session_paused, chain_broken, console_turn_pending, or degraded_read_only (except poll nodes, §11.3)
8 Surface version, generation and release_ref current stale_continuation
9 Continuation matches the chain head, or is an identical non-poll replay (§9.6) stale_continuation or fork_detected
10 Step is exactly the expected next step (fresh continuations only; reused continuations are handled at check 9) step_out_of_order
11 Token not expired expired_token
12 Node released and level sufficient not_released or level_insufficient
13 Step and byte budgets allow budget_exhausted
14 Parameters validate (§9) bad_request
15 Node risk within policy, else record a proposal that needs a console turn result of needs_console
16 Bind the token to the handle, advance the chain, issue the next continuation, respond success

Transport fingerprint is captured at check 3, or as early as the platform allows. On platforms that do not expose a transport fingerprint, the signal is absent and the absence is logged; it is never treated as a fingerprint change. When present, a change is logged, and more than DV-HTSM-FP-ALERT-PER-HOUR changes in an hour raises a console alert. It never blocks a request.

10.6 Forks and anomalies

· A reused continuation (the same value presented again) with a different step or parameters is fork_detected. The controller does not decide which branch is real. The session moves to HALTED and the operator decides.
· A fresh continuation presented with a step other than the expected next step is step_out_of_order and raises a console alert. It does not HALT the session.
· An internally inconsistent chain is chain_broken and also HALTED.
· stale_continuation and repeated not_found raise console alerts.
· Every anomaly is audited with the request, the chain head and the fingerprint (if available).

10.7 Generations

A re-handoff (an operator action) increases the generation, bumps the surface version if the kernel subtree changed, signs a fresh genesis, and issues a new handoff. The old generation's tokens and handles are void. Recovery is only ever by re-handoff or resume; the AI cannot recover itself.

10.8 Release-manifest change during a session

If the Release Manifest changes such that release_ref no longer matches the session's registered value, check 8 fails for every subsequent request and the session moves to HALTED with a console alert. The operator must re-handoff the session under the new release. A release-manifest change that does not alter release_ref (for example, a change to a runtime subtree definition only) does not affect live sessions.

11. Console turns

11.1 Triggers

A console turn is required when any of these occurs: DV-HTSM-CHECKPOINT-STEPS steps since the last turn; a request whose risk exceeds policy; a runtime subtree attachment, revision, suspension, or removal; DV-HTSM-BAD-REQUEST-ATTEMPTS rejections on one token; token expiry; or a controller anomaly that policy marks as needing review.

11.2 Flow

1. The controller moves the session to PENDING_CONSOLE and raises a console alert.
2. The AI's next request receives console_turn_pending (except polls). The AI is told the reason code in generic terms.
3. The operator acts in the console. For a checkpoint the console submits a signed receipt over the console channel:
   sig_t = Sign(console_key, H(read_set_hash || current_continuation || session_id || nonce_t)).
4. The controller verifies the receipt, applies the operator's decision, and issues a fresh continuation, returned through the next poll.
