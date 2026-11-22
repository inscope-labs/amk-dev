# HTSM — Host Transversal Surface Map

**The only Host AI ingress to the OS/10 Agentic Micro Kernel.**

| | |
|---|---|
| **Protocol version** | `HTSM/1` |
| **Specification version** | `1.0.1-draft` (corrected from `1.0.0-draft`; see Part A Appendix B) |
| **Status** | **Draft — incomplete.** Two of 39 files carry normative text; the rest are placeholders. See [Current state](#current-state). |
| **Normative text** | [`specification/HTSM-PART-A.md`](specification/HTSM-PART-A.md), [`specification/HTSM-PART-B.md`](specification/HTSM-PART-B.md) |
| **Completion plan** | [`HTSM-COMPLETION-PLAN.md`](HTSM-COMPLETION-PLAN.md) — 8 phases, 48 stages, 8 gates |
| **Layer** | Control plane only. The effect path is deliberately outside HTSM. |
| **Contents** | Specification only. No implementation. |
| **Language / platform** | None required. Language- and platform-agnostic by construction. |

---

## What HTSM is

HTSM is the baseline method for **any** Host AI to communicate with the controller. It represents the whole operating
system — the kernel and every runtime attached to a session.

It is built for the **weakest plausible AI host**: one that can issue HTTPS GET requests, read JSON, and copy short
opaque tokens. Nothing more is assumed of it.

The design rests on one substitution. Instead of asking the AI to prove *what it is*, HTSM asks it to prove **continuity**:
every step must present the continuation issued by the previous one. The AI holds no secret and computes nothing. It
follows links and fills in parameters. The controller decides everything else.

> **An HTSM request is a proposal. Never an effect.**
> Effects require operator authorisation and a capability token, and they leave HTSM entirely.

### The three properties that define it

| Property | What it means | Where |
|---|---|---|
| **A GET never executes** | A GET on a request node records a proposal and returns a result node. It cannot cause any state change outside the session's own chain and proposal log. | Part A §3 (HTSM-03), §9.4 |
| **Console-only operations are unrepresentable** | Authorize, deny, revoke, activate, enroll, recover and every session, surface and subtree lifecycle action have **no node**. They cannot be called, because there is nothing to call. | Part A §3 (HTSM-04), §8.2 |
| **The AI cannot raise its own level** | The operational level is set by the operator. The AI learns it from `kernel/identity` and cannot claim or raise it. | Part A §3 (HTSM-12), Part B §14 |

### What HTSM is not

- **Not an identity protocol.** Nothing in HTSM proves the AI is who it claims to be. No such proof exists.
- **Not a confidentiality mechanism.** TLS is assumed; confidentiality beyond TLS is a non-goal.
- **Not an authorisation mechanism.** HTSM does not authorise effects.
- **Not the whole system.** Console↔controller envelopes, runtime registration, capability-token use by adapters, runtime
  subtree contents, and effect dispatch are all out of scope.

---

## Scope

**In scope:** the Host AI → controller channel; addressing; parameters; authentication by traversal chain; console turns
as seen by the AI; the surface structure; errors.

**Out of scope:** console → controller communication (signed envelopes); runtime → controller registration; capability
token use by adapters; the content of runtime subtrees; the dispatch and completion of effects.

**Layer separation.** The ingress defined here may be hosted on a different platform from the effect dispatch. HTSM makes
no claim that one platform hosts both. A deployment MAY split them.

---

## The model in one page

```
                        operator
                           │  (signed envelopes — out of HTSM scope)
                           ▼
                    ┌─────────────┐
                    │  Controller │  owns the kernel subtree, mints handles,
                    └─────────────┘  enforces the chain, issues continuations
                           ▲
                           │  HTTPS GET only
                           │  handle + step + continuation + parameters
                    ┌─────────────┐
                    │   Host AI   │  holds no secret, computes nothing,
                    └─────────────┘
```

A **surface** is the tree of nodes one session sees — the kernel subtree plus the runtime subtrees the operator authorised
for that session. A **node** is one addressable unit: content, a link, or a request template. A **handle** is the opaque,
session-scoped, controller-minted identifier of a node — and it is a credential.

### The lifecycle of a step

1. The operator starts a session in the console, choosing the operational level.
2. The console signs a **genesis anchor** and the controller registers the session.
3. The console shows a **handoff**; the operator pastes it into the AI conversation.
4. The AI GETs the root node, presenting the genesis continuation.
5. The controller verifies sixteen checks in order (Part A §10.5). The first failure wins.
6. The controller records a proposal, advances the chain, issues a fresh continuation, and returns a result node.
7. If policy requires it, the session moves to `PENDING_CONSOLE`. The AI polls `kernel/turn` until the operator decides.
8. On acceptance, the controller issues a capability token **directly to the runtime or adapter**. The Host AI never sees
   the token. It learns only the outcome.

---

## Addressing

```
GET {base}/htsm/1/n/{handle}?step={n}&continuation={token}[&{declared params}]
```

- `htsm/1` is the protocol version path. The base URL is given in the handoff.
- `step` and `continuation` are **reserved** and always required. A declared parameter MUST NOT use a reserved name.
- Strict percent-encoding. The controller rejects duplicate keys, unknown keys, missing required keys, malformed encoding
  and any URL over the limit.
- The handoff and every response give the AI the **exact URL template to fill**, so the AI never invents a URL.

Every link the AI may follow appears in `next` with its template and its parameter declarations:

```json
{ "rel": "ask", "type": "request", "title": "...", "handle": "H1",
  "template": "/htsm/1/n/H1?step={step}&continuation={continuation}&mode={mode}",
  "params": [ { "name": "mode", "type": "enum", "values": ["a","b"], "required": true } ] }
```

---

## The node model

Every node carries seven fields:

| Field | Meaning |
|---|---|
| `handle` | Opaque, session-unique, at least `DV-HTSM-HANDLE-ENTROPY-BITS` of entropy |
| `type` | One of the registry types (§7.1) |
| `title` | Short human-readable label |
| `owner` | `kernel` or `runtime:{id}` |
| `released` | Whether the operator released the node to this session |
| `min_level` | Lowest operational level that may use the node |
| `hash` | SHA-256 of the node content, so controller and console can compare what the AI saw |
| `content` | Bounded by `DV-HTSM-NODE-CONTENT-MAX-BYTES` |

### Node types (registry v1)

| Type | Purpose |
|---|---|
| `info` | Read-only content |
| `index` | Lists child nodes, at most `DV-HTSM-INDEX-MAX-CHILDREN` |
| `request` | Declares parameters; a GET records a proposal and returns a result node |
| `result` | The outcome of a proposal |
| `poll` | Read-only status that does not advance the chain |

A new node type requires a registry version bump.

### Parameter types

`enum`, `int`, `bool`, `slug`, `hex`, `b64url`, `text`, `handle-ref` — each with a declared constraint. **There is no
free-form pattern type**, so a node cannot ask the AI for arbitrary text beyond a bounded charset. Parameters are
untrusted data, validated against the node's declared schema; anything outside it is rejected.

### The kernel subtree

Controller-owned. Nine kernel commands exist only as nodes, plus two auxiliary nodes that carry no command.

| Node | Command | Type |
|---|---|---|
| `kernel/status` | status | info |
| `kernel/identity` | identity | info |
| `kernel/state` | state | info |
| `kernel/policy` | policy | index |
| `kernel/capabilities` | capabilities | index |
| `kernel/runtimes` | runtime | index |
| `kernel/query` | query | request |
| `kernel/verify` | verify | request |
| `kernel/help` | help | info |
| `kernel/proposals` | *(none)* | index |
| `kernel/turn` | *(none)* | poll |

Runtime subtrees live under `runtime:{id}`. A runtime subtree MUST NOT contain kernel-owned node paths, claim
console-only operations, or declare a `min_level` lower than its risk class allows.

---

## Protocol rules

The twelve rules are the contract. Full text in Part A §3.

| ID | Rule |
|---|---|
| HTSM-01 | HTSM is the baseline Host AI ingress to the controller. No other Host AI method may exist at the kernel level. |
| HTSM-02 | The AI holds no secret and computes nothing. |
| HTSM-03 | A GET never executes. |
| HTSM-04 | Operations reserved to the console have no node — they are unrepresentable, not rejected. |
| HTSM-05 | Parameter values are untrusted data, validated against the node's declared schema. |
| HTSM-06 | The kernel subtree is controller-owned. No runtime may define or shadow a kernel node. |
| HTSM-07 | Handles and continuations are credentials: controller-minted, session-bound, short-lived, single-use for continuations. |
| HTSM-08 | Errors and responses reveal nothing beyond the session's own projection. |
| HTSM-09 | The AI changes session state only by advancing the chain. |
| HTSM-10 | Any verification failure produces an error and no side effect. |
| HTSM-11 | While the deployment is not READY, every request receives only `unavailable`. |
| HTSM-12 | The operational level is set by the operator. The AI cannot claim or raise it. |

> **A note on "safe".** HTSM's GET *does* record a proposal, which changes the session's proposal log. HTSM's claim is
> therefore **effect-safety**, not RFC 9110 safety. Implementations MUST NOT describe an HTSM GET as RFC-9110 "safe"
> without that qualifier.

---

## Authentication: the traversal chain

A request is authenticated only as the **valid next step of its session's chain**. The chain proves continuity.

- The console signs a genesis anchor binding the session id, surface version, release reference, intent hash, console key
  id, nonce and timestamp.
- The controller registers the session and issues the first continuation, returned in the handoff.
- Each response carries a fresh continuation in the JSON body and the `X-HTSM-Continuation` header. The controller stores
  only token hashes.
- A **continuation** is opaque, single-use, bound to the session, generation, handle and step, and valid for
  `DV-HTSM-TOKEN-TTL-S`. The clock pauses while a console turn is pending.

### Verification order

Sixteen checks, in order. The first failure wins.

| # | Check | Failure |
|---|---|---|
| 1 | Deployment is READY | `unavailable` |
| 2 | Protocol version supported | `unsupported_version` |
| 3 | Source rate limit | `rate_limited` |
| 4 | Request syntax: method, URL length, reserved parameters | `bad_request` |
| 5 | Resolve the handle to a session | `not_found` |
| 6 | Session rate limit | `rate_limited` |
| 7 | Session state allows requests | `session_ended`, `session_paused`, `chain_broken`, `console_turn_pending`, `degraded_read_only` |
| 8 | Surface version, generation and release reference current | `stale_continuation` |
| 9 | Continuation matches the chain head, or is an identical non-poll replay | `stale_continuation` / `fork_detected` |
| 10 | Step is exactly the expected next step | `step_out_of_order` |
| 11 | Token not expired | `expired_token` |
| 12 | Node released and level sufficient | `not_released` / `level_insufficient` |
| 13 | Step and byte budgets allow | `budget_exhausted` |
| 14 | Parameters validate | `bad_request` |
| 15 | Node risk within policy | records a proposal in state `needs_console` |
| 16 | Bind the token, advance the chain, issue the next continuation | success |

### Retries, replays and forks

| Situation | Outcome |
|---|---|
| Identical repeat (non-poll), same continuation + step + parameter hash | The stored response, marked `X-HTSM-Replay: 1` |
| Identical repeat (poll) | Served fresh from current chain state; polls are excluded from the replay cache |
| Same continuation, different step or parameters | `fork_detected` — the session **HALTs** and the operator decides |
| Fresh continuation, wrong step | `step_out_of_order` — console alert, session does not halt |
| Internally inconsistent chain | `chain_broken` — session HALTs |
| Malformed request | `bad_request`; the token is **not** consumed. After `DV-HTSM-BAD-REQUEST-ATTEMPTS` rejections on one token, the token is consumed and a console turn is required |

---

## Console turns

A console turn is required when: `DV-HTSM-CHECKPOINT-STEPS` steps have passed since the last turn; a request's risk
exceeds policy; a runtime subtree is attached, revised, suspended or removed; the bad-request attempt cap is hit; a token
expires; or a controller anomaly is one policy marks for review.

The controller moves the session to `PENDING_CONSOLE` and raises a console alert. The AI's next request receives
`console_turn_pending` — except polls. During a pending turn, **only `kernel/turn` is served**; a poll does not advance the
step, and the token clock is paused. When the turn completes, the next poll carries the normal continuation.

For a checkpoint the console submits a signed receipt over the console channel:

```
sig_t = Sign(console_key, H(read_set_hash || current_continuation || session_id || nonce_t))
```

**Missed window.** If the console does not answer within `DV-HTSM-TURN-WINDOW-S`, the session moves to `DEGRADED`. A
degraded session may take at most `DV-HTSM-DEGRADED-EXTENSION-STEPS` further steps, all read-only, and then pauses.

**Expired token.** If a token expires with no turn pending, the session moves to `PENDING_CONSOLE` with reason
`token_expired`. The AI holds no valid token and cannot poll. The operator resumes the session, which issues a **resume
handoff**. The generation does not change.

---

## Operational levels

| Level | Name | The AI may |
|---|---|---|
| 1 | Traverse | Follow handles and read `info`, `index`, `result` and `poll` nodes |
| 2 | Request | Everything in Level 1, plus fill parameter templates on `request` nodes |

Each node declares its `min_level`. A request above the session's level returns `level_insufficient`.

---

## Responses and errors

```json
{ "htsm": "1", "ok": true, "step": 4, "ttl": "seconds left on the continuation",
  "node": { "handle": "...", "type": "info", "title": "...", "owner": "kernel", "hash": "...", "min_level": 1 },
  "content": { },
  "next": [ ],
  "continuation": "...",
  "budget": { "steps_left": 0, "bytes_left": 0 },
  "turn": null,
  "result": null }
```

`turn` is non-null while a turn is pending, with a generic reason code and `poll_only: true`.

An error body is `{"htsm": "1", "ok": false, "error": {"code": "...", "message": "generic text"}}`. **The message is
generic and fixed per code.** Detail goes to the console alert and the audit log, never to the AI.

| Code | HTTP | Meaning | Session effect |
|---|---|---|---|
| `bad_request` | 400 | Syntax, method or parameter violation | Counts toward attempt cap |
| `unsupported_version` | 400 | Unknown protocol version | None |
| `not_found` | 404 | Unknown or unauthorised handle (identical response for both) | Counts toward probe limit |
| `not_released` | 403 | Node not released to this session | None |
| `level_insufficient` | 403 | Session level below the node's `min_level` | None |
| `expired_token` | 401 | Continuation expired | PENDING_CONSOLE (`token_expired`) |
| `stale_continuation` | 409 | Not the chain head, or surface version outdated | Alert |
| `step_out_of_order` | 409 | Wrong step number | Alert |
| `fork_detected` | 409 | Continuation reused with a different request | HALTED |
| `chain_broken` | 409 | Chain state inconsistent | HALTED |
| `console_turn_pending` | 423 | A console turn is required | None |
| `session_paused` | 423 | The operator paused the session | None |
| `session_ended` | 410 | Closed, expired, revoked or superseded | Terminal |
| `budget_exhausted` | 429 | Step or byte budget used | None |
| `rate_limited` | 429 | Rate limit, with `Retry-After` | None |
| `unavailable` | 503 | Deployment not READY, with `Retry-After` | None |
| `internal_error` | 500 | Unexpected failure; no side effect | Audited |

### Result states

`pending` · `needs_console` · `accepted` · `denied` · `expired` · `failed` · `revoked`

The AI reads results through `kernel/proposals` for `DV-PROPOSAL-RESULT-RETENTION-S`. A session holds at most
`DV-PROPOSAL-MAX-PENDING` pending proposals.

---

## Audit

**Audited per request:** session, generation, step, node, parameter hash, outcome, error code, fingerprint, chain head.

**Audited per event:** genesis, handoff use, console turns and receipts, proposals and their decisions, subtree
revisions, forks, and every anomaly.

Console alerts accompany every anomaly and every pending turn.

---

## Security considerations

- **Injection steering.** Injected text can push the AI to submit chosen parameter values to any node it holds a handle
  for. *Mitigations:* strict schemas with no free-form types, risk classing, console turns for risky proposals, and the
  rule that a GET never executes.
- **Token exposure.** AI host tooling may log or cache URLs. *Mitigations:* single use, bounded lifetime, `no-store`, and
  chain-continuity detection of reuse.
- **Intermediaries.** Prefetching and retries are handled by the replay rule.
- **Console key.** Compromise of the console signing key defeats genesis and receipts. Hardware-backed keys are required
  by `amk-console`.
- **Carrier race.** Between a handoff being shown and pasted, a second party could use it. Detectable (first use wins,
  second is a fork) but not preventable.
- **Residual.** Side channels from response size and timing are not analysed in this version.

Transport requirements: HTTPS GET only, TLS required, `application/json; charset=utf-8`, `Cache-Control: no-store`,
`Referrer-Policy: no-referrer`, no cookies, no redirects, bounded response and URL sizes.

---

## Conformance

Twenty-four tests are named in Part B §19. An implementation claims conformance by declaring its operational profile and
passing the applicable tests with retained evidence.

| Test | Subject |
|---|---|
| T01 | No Host AI route other than HTSM GETs |
| T02 | Any non-GET on an HTSM path is `bad_request` |
| T03 | A request with no valid continuation is rejected with no side effect |
| T04 | A GET on a request node records a proposal and executes nothing |
| T05 | Enumerating every subtree finds no node for any console-only operation |
| T06 | Unknown, duplicate, missing, over-length and mis-encoded parameters are `bad_request` |
| T07 | A runtime subtree cannot define or shadow a kernel node |
| T08 | A handle from another session returns `not_found`, identical to an unknown handle |
| T09 | The chain advances and the old continuation is rejected |
| T10 | An identical repeat replays; a different repeat is `fork_detected` and HALTs the session |
| T11 | A rejected `bad_request` does not consume the token; the attempt cap forces a console turn |
| T12 | `expired_token` moves the session to PENDING_CONSOLE; a resume handoff restores it |
| T13 | During a pending turn only polls are served, and the token clock is paused |
| T14 | A missed turn window degrades the session and caps further steps |
| T15 | A receipt not signed by the registered console key is refused |
| T16 | Subtree attachment is a revision: the chain continues with no re-handoff |
| T17 | A kernel-subtree change requires re-handoff and voids old tokens and handles |
| T18 | Level 1 cannot call a Level 2 node |
| T19 | Budgets and rate limits produce `budget_exhausted` and `rate_limited` |
| T20 | Errors leak no structure beyond the session's projection |
| T21 | While the deployment is not READY every request receives only `unavailable` |
| T22 | Handles and tokens have the required entropy and are never reused |
| T23 | Responses never exceed size limits; `next` templates validate against their own schemas |
| T24 | Concurrent requests on one chain are serialized: exactly one succeeds |

> The test **titles** above are normative in intent but are not yet written as executable specifications. The conformance
> document, the security profile, and eight additional tests (T25–T32) covering currently untested behaviours — including
> the Release Manifest change rule of §10.8 — are specified in Phase 5 of the
> [completion plan](HTSM-COMPLETION-PLAN.md).

---

## Defaults

Every tunable in HTSM is a `DV-` identifier. **No default value is stated in the normative text**; each is defined only
in the defaults manifest, which does not yet exist.

Twenty-six identifiers are referenced today:

`DV-HTSM-RESPONSE-MAX-BYTES` · `DV-HTSM-URL-MAX-BYTES` · `DV-HTSM-HANDLE-ENTROPY-BITS` ·
`DV-HTSM-INDEX-MAX-CHILDREN` · `DV-HTSM-NODE-CONTENT-MAX-BYTES` · `DV-HTSM-DEPTH-MAX` ·
`DV-HTSM-PARAMS-MAX-COUNT` · `DV-HTSM-PARAM-TEXT-MAX-LEN` · `DV-PROPOSAL-PENDING-TTL-S` ·
`DV-PROPOSAL-RESULT-RETENTION-S` · `DV-PROPOSAL-MAX-PENDING` · `DV-HTSM-TOKEN-ENTROPY-BITS` ·
`DV-HTSM-TOKEN-TTL-S` · `DV-HTSM-PROBE-RATE-PER-MIN` · `DV-HTSM-FP-ALERT-PER-HOUR` ·
`DV-HTSM-BAD-REQUEST-ATTEMPTS` · `DV-SESSION-HANDOFF-TTL-S` · `DV-HTSM-CHECKPOINT-STEPS` ·
`DV-HTSM-TURN-WINDOW-S` · `DV-HTSM-DEGRADED-EXTENSION-STEPS` · `DV-HTSM-LEVEL-DEFAULT` ·
`DV-SUBTREE-MAX-NODES` · `DV-SUBTREE-MAX-BYTES` · `DV-SUBTREE-REVISIONS-MAX` ·
`DV-CAP-TOKEN-TTL-S` · `DV-CAP-TOKEN-DESTRUCTIVE-TTL-S`

A conforming deployment MUST publish the resolved values of every `DV-` ID it references, in a machine-readable form,
alongside the specification — so the deployment is auditable without the companion manifest.

---

## Directory map

```
specifications/protocols/htsm/
├── README.md                       This file
├── CHANGELOG.md                    Version history
├── LICENSE                         Licence terms
├── HTSM-COMPLETION-PLAN.md         The phase / stage completion plan
├── specification/                  Normative text
│   ├── HTSM-PART-A.md              §1–§10 — foundations through chain authentication
│   ├── HTSM-PART-B.md              §11–§19 — console turns through conformance
│   ├── HTSM-CONFORMANCE.md         Test specifications (planned)
│   ├── HTSM-REGISTRY-V1.md         Node types, parameter types, error codes (planned)
│   └── HTSM-SECURITY-PROFILE.md    Threat model and profile obligations (planned)
├── contracts/                      Boundary documents HTSM points into
│   ├── capability/                 Capability token issuance and lifetimes
│   ├── console/                    Console obligations towards HTSM
│   ├── cryptography/               Hash and signature constructions
│   ├── defaults/                   Every DV- value
│   ├── effect-dispatch/            The proposal → effect boundary
│   ├── lifecycle/                  The session state machine
│   ├── policy/                     Risk classes and level gating
│   ├── release-manifest/           release_ref, epoch, kernel subtree hash
│   └── runtime-registration/       Subtree submission and validation
├── schemas/                        JSON Schema (draft 2020-12)
│   ├── node.schema.json            The seven node fields
│   ├── handoff.schema.json         The handoff form
│   ├── request.schema.json         Request nodes and parameter declarations
│   ├── response.schema.json        The success body
│   ├── proposal.schema.json        The proposal record
│   └── error.schema.json           The error body
├── fixtures/                       Test vectors
│   ├── valid/  invalid/  replay/  fork-detection/  authorization/
├── mocks/                          Mock role contracts
│   ├── controller/  operator-console/  runtime-provider/
│   └── capability-issuer/  effect-dispatcher/
└── tests/                          Conformance suites
    ├── protocol/  authentication/  lifecycle/
    └── security/  conformance/  interoperability/
```

---

## Reading order

**To implement HTSM:**

1. This README, for the model.
2. [`HTSM-PART-A.md`](specification/HTSM-PART-A.md) §§1–10 — scope, rules, transport, addressing, node model,
   parameters, chain authentication.
3. [`HTSM-PART-B.md`](specification/HTSM-PART-B.md) §§11–19 — console turns, effects, subtrees, levels, responses,
   errors, audit, security, versioning, conformance.
4. The `contracts/` documents for the boundaries HTSM depends on.
5. The `schemas/` for the machine-readable shapes.

**To complete HTSM:** start with the [completion plan](HTSM-COMPLETION-PLAN.md).

---

## Current state

**This protocol is not complete.** At the baseline commit `1463d89` the tree contained 39 files: **37 were placeholders** and
2 carried normative text. It remains that way today, apart from this README and the completion plan.

| Area | State |
|---|---|
| Normative text (Part A, Part B) | **Substantive** — §1–§19, but §11 is duplicated across the two files and Part A lacks Markdown headings |
| 12 protocol rules | Present, without stable per-rule IDs |
| 16 verification checks | Present and ordered |
| 17 error codes, 7 result states, 9 kernel commands, 5 node types, 8 parameter types | Present |
| 24 conformance tests | Named only — not specified |
| 26 `DV-` default values | **Referenced, none defined** |
| 8 `INV-` invariant citations | **Referenced, none defined anywhere in the repository** |
| 9 contracts | Placeholders |
| 6 JSON Schemas | Placeholders |
| 10 fixture and mock directories | Placeholders |
| 6 test suites | Placeholders |
| `CHANGELOG.md`, `LICENSE` | Placeholders |

### Known conflicts with the rest of the AMK

These are recorded, with file-and-line evidence, in [§5 of the completion plan](HTSM-COMPLETION-PLAN.md). The four
blocking ones:

1. **Vocabulary.** HTSM says *proposal*; the prompt, console and controller say *work order*, and the prompt requires a
   `work_order_required` error code that is absent from the HTSM error table.
2. **`kernel/verify` vs `kernel/validate`.** HTSM defines `kernel/verify` as a request node; the prompt defines
   `kernel/validate` as an index node of an optional session-validation layer.
3. **No session state machine.** Ten session states are named and delegated to `os10-lifecycle.md §6`, which does not
   exist.
4. **Dangling invariant citations.** `INV-18`, `INV-26`, `INV-29`–`INV-34` are cited with no definitions anywhere.

### Open decisions

Ten operator decisions gate the completion plan — vocabulary, the validation layer, the state machine home, the
invariant namespace, defaults placement, the handoff artefact shape, session identity without a fingerprint, the registry
version, the file structure, and the licence. They are specified in
[§6 of the completion plan](HTSM-COMPLETION-PLAN.md).

---

## Relationship to the rest of the AMK

| Document | Relationship |
|---|---|
| `amk-controller` | Owns the surface, mints handles and continuations, applies the verification order (`CTR-5:11`–`CTR-5:12`) |
| `amk-console` | Signs the genesis anchor and the turn receipts; renders the handoff |
| `amk-prompt` | Tells the Host AI how to behave on this channel (`PMT-7:1`–`PMT-7:15`) |
| `amk-runtime` | Owns package provenance; supplies runtime subtrees |
| `amk-authority` | Issues and verifies role attestations; does not authorise effects |
| `session-router` | A blind ingress/egress relay companion to the controller; not part of HTSM |

HTSM is the **baseline** channel, not the only one. Runtimes may introduce other channels at the runtime level; no other
Host AI method may exist at the kernel level.

---

## Change control

Protocol changes are governed by the framework's version rules. A new node type, parameter type or error code requires a
**registry or protocol version bump**. A change to the kernel subtree definition, or to the protocol's major version,
supersedes every live surface and requires re-handoff.

The Release Manifest MUST identify the HTSM protocol version and a hash of the kernel subtree definition, so a deployment
can tell whether a manifest change is breaking.

> **Note.** The repository's `CHANGELOG.md`, `GOVERNANCE.md`, `CONTRIBUTING.md` and `SECURITY.md` are placeholders, pending
> framework decisions D-04 and D-05. The change-control rules above are taken from the protocol text itself and are not
> yet backed by a published governance policy.

---

## Licence

Pending framework decision D-04. See [`LICENSE`](LICENSE).

---

*Protocol version `HTSM/1` · specification version `1.0.1-draft` · draft for review*
