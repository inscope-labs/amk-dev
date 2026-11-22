# HTSM: Host Transversal Surface Map Protocol (Part B)

## 11. Console turns

### 11.1 Triggers
A console turn is required when any of these occurs: `DV-HTSM-CHECKPOINT-STEPS` steps since the last turn; a request whose risk exceeds policy; a runtime subtree attachment, revision, suspension, or removal; `DV-HTSM-BAD-REQUEST-ATTEMPTS` rejections on one token; token expiry; or a controller anomaly that policy marks as needing review.

### 11.2 Flow
1. The controller moves the session to PENDING_CONSOLE and raises a console alert.
2. The AI's next request receives `console_turn_pending` (except polls). The AI is told the reason code in generic terms.
3. The operator acts in the console. For a checkpoint the console submits a signed receipt over the console channel:
   `sig_t = Sign(console_key, H(read_set_hash || current_continuation || session_id || nonce_t))`.
4. The controller verifies the receipt, applies the operator's decision, and issues a fresh continuation, returned through the next poll.

### 11.3 Polling
During a pending turn only `kernel/turn` (type `poll`) is served. A poll does not advance the step. The continuation issued with a pending turn is poll-only until the turn completes. The token clock is paused. When the turn completes, the poll response carries the normal continuation.

### 11.4 Missed windows
If the console does not answer within `DV-HTSM-TURN-WINDOW-S`, the session moves to DEGRADED. A degraded session may take at most `DV-HTSM-DEGRADED-EXTENSION-STEPS` more steps, all read-only, then the session moves to PAUSED.

### 11.5 Expired tokens
If a token expires with no turn pending, the session moves to PENDING_CONSOLE with a reason of `token_expired`. The AI holds no valid token and cannot poll. The operator resumes the session in the console, which issues a **resume handoff**. The generation does not change.

### 11.6 Operator authorization
Approving a `needs_console` proposal is a console turn. The turn's decision, not the AI's request, is what authorizes any effect (§12).

## 12. From proposal to effect (outside HTSM)

HTSM carries no effect. After the operator accepts a proposal, the controller may issue a capability token with a lifetime of `DV-CAP-TOKEN-TTL-S` (`DV-CAP-TOKEN-DESTRUCTIVE-TTL-S`, single use, for destructive or irreversible operations) directly to the runtime or adapter. The Host AI never sees the token. The result node tells the AI only the outcome.

## 13. Subtree attachment and revision

What HTSM requires; the full lifecycle is in `os10-lifecycle.md`.

- A runtime provider submits a subtree: runtime id, provider identity, nodes, declared operations with risk classes, parameter schemas, and size caps, signed by the provider.
- The controller validates it against kernel policy and the limits (`DV-SUBTREE-MAX-NODES`, `DV-SUBTREE-MAX-BYTES`).
- The operator authorizes attachment per session in a console turn whose receipt covers the subtree hash.
- Attachment and later revisions are **subtree revisions**. They do not change the surface version, so the chain continues without a re-handoff. A session allows at most `DV-SUBTREE-REVISIONS-MAX` revisions per generation.
- Only a kernel-level change or a recovery bumps the surface version and forces a re-handoff.

## 14. Operational levels

The operator sets the level at session creation (`DV-HTSM-LEVEL-DEFAULT`) and may raise or lower it with a console turn.

| Level | Name | The AI may |
|---|---|---|
| 1 | Traverse | Follow handles and read `info`, `index`, `result` and `poll` nodes |
| 2 | Request | Everything in Level 1, plus fill parameter templates on `request` nodes |

Each node declares its `min_level`. A request above the session's level returns `level_insufficient`. The AI cannot claim a level; it learns it from `kernel/identity`.

## 15. Responses and errors

### 15.1 Success
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

### 15.2 Errors
```json
{ "htsm": "1", "ok": false, "error": { "code": "fork_detected", "message": "generic text" } }
```
The message is generic and fixed per code. Detail goes to the console alert and the audit log, never to the AI.

| Code | HTTP | Meaning | Session effect |
|---|---|---|---|
| `bad_request` | 400 | Syntax, method or parameter violation | Counts toward attempt cap |
| `unsupported_version` | 400 | Unknown protocol version | None |
| `not_found` | 404 | Unknown or unauthorized handle (same response for both) | Counts toward probe limit |
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

## 16. Audit and alerts
Audited per request: session, generation, step, node, parameter hash, outcome, error code, fingerprint, and chain head. Audited per event: genesis, handoff use, console turns and receipts, proposals and their decisions, subtree revisions, forks, and every anomaly. Console alerts accompany every anomaly and every pending turn.

## 17. Security considerations

- **Injection steering.** Injected text can push the AI to submit chosen parameter values to any node it holds a handle for. Mitigations: strict schemas with no free-form types, risk classing, console turns for risky proposals, and the rule that a GET never executes.
- **Token exposure.** AI host tooling may log or cache URLs. Mitigations: single use, bounded lifetime, `no-store`, and chain-continuity detection of reuse.
- **Intermediaries.** Prefetching and retries are handled by the replay rule (§9.6).
- **Console key.** Compromise of the console signing key defeats genesis and receipts. Hardware-backed keys are required by `amk-console`.
- **Carrier race.** Between a handoff being shown and pasted, a second party could use it. This is detectable (the first use wins, a second is a fork) but not preventable.
- **Prerequisites.** TLS is assumed. Clock skew affects only token lifetimes, which are measured by the controller's own clock.
- **Residual.** Side channels from response size and timing are not analyzed in this version.

## 18. Versioning and extensibility
HTSM carries a protocol version in the path. The node-type registry has its own version, reported in `kernel/identity`. A new node type, parameter type or error code needs a registry or protocol version bump. A change to the kernel subtree definition or to the protocol's major version supersedes every live surface and requires re-handoff. The Release Manifest MUST identify the HTSM protocol version and a hash of the kernel subtree definition, so a deployment can tell whether a manifest change is breaking.

## 19. Conformance tests

| ID | Test |
|---|---|
| HTSM-T01 | The controller exposes no Host AI route other than HTSM GETs (HTSM-01) |
| HTSM-T02 | Any non-GET on an HTSM path is `bad_request` |
| HTSM-T03 | A request with no valid continuation is rejected and has no side effect |
| HTSM-T04 | A GET on a request node records a proposal and executes nothing (HTSM-03) |
| HTSM-T05 | Enumerating every subtree finds no node for any console-only operation (HTSM-04) |
| HTSM-T06 | Every unknown, duplicate, missing, over-length and mis-encoded parameter is `bad_request` (HTSM-05) |
| HTSM-T07 | A runtime subtree cannot define or shadow a kernel node (HTSM-06) |
| HTSM-T08 | A handle from another session returns `not_found`, identical to an unknown handle |
| HTSM-T09 | The chain advances and the old continuation is rejected |
| HTSM-T10 | An identical repeat replays; a different repeat is `fork_detected` and HALTS the session |
| HTSM-T11 | A rejected `bad_request` does not consume the token; the attempt cap forces a console turn |
| HTSM-T12 | `expired_token` moves the session to PENDING_CONSOLE and a resume handoff restores it |
| HTSM-T13 | During a pending turn only polls are served, and the token clock is paused |
| HTSM-T14 | A missed turn window degrades the session and caps further steps |
| HTSM-T15 | A receipt not signed by the registered console key is refused |
| HTSM-T16 | Subtree attachment is a revision: the chain continues with no re-handoff |
| HTSM-T17 | A kernel-subtree change requires re-handoff and voids old tokens and handles |
| HTSM-T18 | Level 1 cannot call a Level 2 node (`level_insufficient`) |
| HTSM-T19 | Budgets and rate limits produce `budget_exhausted` and `rate_limited` |
| HTSM-T20 | Errors leak no structure beyond the session's projection |
| HTSM-T21 | While the deployment is not READY every request receives only `unavailable` (HTSM-11) |
| HTSM-T22 | Handles and tokens have the required entropy and are never reused |
| HTSM-T23 | Responses never exceed size limits, and `next` templates always validate against their own schemas |
| HTSM-T24 | Concurrent requests on one chain are serialized: exactly one succeeds |

## Appendix A: Worked example

1. The operator starts a session at Level 2. The console shows:
   `htsm=1 base=https://controller.example surface=3 root=H0 continuation=G.k3q...`
2. The AI GETs `/htsm/1/n/H0?step=0&continuation=G.k3q...`. The response lists `kernel/identity` and `kernel/query` in `next`, with templates, and a fresh continuation `T1`.
3. The AI follows `kernel/query` with `?step=1&continuation=T1&capability=H7&op=read&arg=status`. The controller verifies all fifteen checks, records a proposal, and returns a `result` of `pending` with continuation `T2`.
4. The proposal's risk exceeds policy, so the result is `needs_console` and `turn.pending` is true. The AI's next non-poll request gets `console_turn_pending`.
5. The AI polls `kernel/turn`. The operator approves in the console and the signed receipt is verified. The next poll returns a normal continuation `T3`, and `kernel/proposals` now shows the result `accepted`. Effects proceed outside HTSM.
