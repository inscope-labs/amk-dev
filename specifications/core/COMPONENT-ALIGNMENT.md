# Core Component Alignment Report

| Field | Value |
|---|---|
| Area | `specifications/core/` — cross-component alignment across the five core specifications |
| Components | `amk-authority` (ATA) · `amk-console` (CNS) · `amk-controller` (CTR) · `amk-prompt` (PMT) · `amk-runtime` (RTM) |
| Baseline | `inscope-labs/amk-dev` @ `1463d89` |
| Status | **Draft.** Reports the alignment state, applies the unambiguous corrections, and lists the corrections that need an owner decision. |
| Companion | [`kernel/invariants/README.md`](kernel/invariants/README.md) — the shared invariant register this report's fixes point at |

---

## 1. Purpose and method

This report answers one question: **do the five core specifications agree with each other, and where they do not, what
exactly differs?**

It does not rewrite the specifications. The components are careful, drafted normative text with their own review
findings (`E-ATA-nn`, `E-CNS-nn`, `E-CTR-nn`, `E-PMT-nn`, `E-RTM-nn`) and open items (`O-ATA-nn`, `CNS-O-nn`,
`CTR-O-nn`, `RTM-O-nn`). Rewriting them from outside would discard that review work.

Instead, this report does three things:

1. **Measures** the alignment, dimension by dimension, with `file:line` evidence.
2. **Applies** the corrections that are mechanical or forced — where one component already states the answer and another
   simply has not caught up. These are listed in §7 and are in the working tree.
3. **Registers** the corrections that need an owner decision — where the components genuinely disagree and no evidence
   picks a winner. These are listed in §8.

### 1.1 The central finding

> **The five components are not five independent specifications that happen to interact. They are five partial views of
> one contract, and the contract itself was never written.**

Four independent signals show this:

| Signal | Evidence |
|---|---|
| Each component cites a suite-level document that does not exist | `amk-ssot.md` (normative source of `contract_version`, `CNS-3:16` / `CTR-3:10`), `specs/core/OPEN-ITEMS.md` (cited by three components), `amk-test-matrix` (cited by three), `amk-defaults-manifest` (cited live by the runtime, declared discontinued by the authority) |
| Cross-component questions are recorded **twice**, in two components, with no owner | Envelope schemas (`CNS-O-01` = `CTR-O-01`); freshness and sequencing (`CNS-O-04` = `CTR-O-08`); recovery protocol (`CNS-O-03` = `CTR-O-07`); runtime intake envelope name (`CNS-O-10` = `CTR-O-13`); `SessionPolicy` / `CheckIn` / `WorkOrderProposal` (`CNS-O-11` = `CTR-O-14`); behavioural rules for status envelopes (`CNS-O-12` = `CTR-O-15`) |
| The same obligation is phrased three different ways | "Soft isolation is acceptable" in `CNS-7:7`, `CTR-8:7`, `RTM-8:7` — one term, three specifications, no definition anywhere |
| One component's invariant register is explicitly delegated to a document that does not exist | `PMT-1:14`: the prompt's invariants are "as registered in the contracts" |

The `abstract-kernel/` area — the fourteen sibling areas listed in §2 — is precisely the missing contract. Every gap
this report names maps onto one of those areas. The **kernel invariant register** delivered with this report is the
first of them to be written.

---

## 2. The target structure

The attached `abstract-kernel/` scaffold defines fourteen areas. Their mapping onto the suite's cross-cutting gaps:

| Area | Holds | Gaps it closes |
|---|---|---|
| `scope/` | What the kernel is and is not; the suite's own registers | the missing `OPEN-ITEMS.md`; suite versioning (`RTM-O-19`, `CORE-O-03`) |
| `principles/` | The design principles the invariants realise | — |
| `trust-model/` | Who trusts whom, for what | trust-anchor mismatch (§6.8) |
| `authority-model/` | How authority flows; who may not widen it | the console-only operation list (§4.1) |
| `trust-bootstrap/` | How trust anchors are established | key distribution and pinning (`O-ATA-11`, `CNS-O-13`, `CTR-O-16`) |
| `cryptographic-lifecycle/` | Key lifecycle, rotation, recovery | recovery protocol (`CNS-O-03`, `CTR-O-07`); root-key rotation |
| `time-and-freshness/` | Skew, nonce lifetime, TTL, sequence storage | `CNS-O-04` = `CTR-O-08` |
| `operating-levels/` | Basic/Standard/High and L0/L1/L2 | "soft isolation"; profile exclusions; peer rules |
| `canonical-data-model/` | Envelopes, states, errors, audit records | `CNS-O-01` = `CTR-O-01`; error taxonomy (`E-CTR-14`, `E-PMT-06`); audit format (`E-CTR-10`, `E-CNS-10`, `E-RTM-08`); defaults location |
| `security-requirements/` | Suite-wide security obligations | "soft isolation"; remote attestation; side channels |
| `threat-model/` | Threats and the invariants answering them | — |
| `architecture/` | How the components fit | the loader's absence from the controller (`E-RTM-09`) |
| `backend-extension-model/` | How third parties extend without weakening | runtime boundary (`RTM-1:16`) |
| **`invariants/`** | **The shared invariant register** | **delivered with this report** |

**Placement.** The scaffold's root directory is `abstract-kernel/`. The repository's pre-restructure layout used
`amk/abstract-kernel/`; the current tree is `specifications/{core,contracts,protocols}/` and already carries a
placeholder at `specifications/core/kernel/README.md`. The scaffold is therefore installed at
**`specifications/core/kernel/`**, and the invariants register at
**`specifications/core/kernel/invariants/README.md`**.

---

## 3. The five components at a glance

| | `amk-authority` | `amk-console` | `amk-controller` | `amk-prompt` | `amk-runtime` |
|---|---|---|---|---|---|
| Version | 1.2.0-draft | 2.1.0-draft | 2.0.0-draft | 2.0.0-draft | 0.1.0-draft |
| Role | Sole issuer of role attestations | Sole seat of operator authority | Sole enforcement point | Advisory contract | Package authority & loader |
| Trusted for | What it signs | Authenticated operator intent | Enforcement decisions | **Nothing** | Package provenance |
| ID scheme | `ATA-<file>:<rule>` (conversion pending, `CORE-O-05`) | `CNS-<file>:<rule>` | `CTR-<file>:<rule>` | `PMT-<file>:<rule>` | `RTM-<file>:<rule>` |
| Own invariants | `INV-ATA-01`…`07` (7) | `CNS-1:9`…`16` (8) | `CTR-1:14`…`20` (7) | `PMT-1:14`–`15` (2) | `RTM-1:12`…`20` (9) |
| Profiles | Basic/Std/High | Basic/Std/High | Basic/Std/High | L0/L1/L2 assurance | Basic/Std/High |
| Non-conformance clause | **absent** | `CNS-1:16` | `CTR-1:20` | `PMT-1:15` | `RTM-1:20` |

---

## 4. Alignment matrix

Each row is a dimension on which the five specifications must agree. **Status**: `aligned` · `cosmetic` (wording or
metadata only) · `divergent` (substantive disagreement) · `gap` (a contract nobody owns).

### 4.1 Authority, enforcement and the console-only operation list

**Divergence — the most consequential one in the suite.** Three specifications list the operations the Host AI may
never perform. They are not the same list.

| Source | List | Verbatim |
|---|---|---|
| `amk-controller/03-console-identity.md:20` (`CTR-4:7`) | A | session start, surface approval, authorization, denial, revocation, **runtime registration support**, pre-authorization management, **level changes**, recovery |
| `amk-prompt/03-command-set.md:14` (`PMT-4:4`) | B | session start, surface approval, authorize, deny, revoke, **activate**, pre-authorization, runtime registration, **enroll**, recover |
| `HTSM-PART-A.md:136` (§8.2) | C | authorize, deny, revoke, **activate**, **enroll**, recover, and every session, surface and subtree lifecycle action |

| Operation | A (CTR) | B (PMT) | C (HTSM) | Union |
|---|:--:|:--:|:--:|:--:|
| session start | ✓ | ✓ | — | ✓ |
| surface approval | ✓ | ✓ | — | ✓ |
| authorize / deny / revoke | ✓ | ✓ | ✓ | ✓ |
| **activate** | — | ✓ | ✓ | ✓ |
| **enroll** | — | ✓ | ✓ | ✓ |
| **level changes** | ✓ | — | — | ✓ |
| pre-authorization management | ✓ | ✓ | — | ✓ |
| runtime registration | ✓ | ✓ | — | ✓ |
| recovery | ✓ | ✓ | — | ✓ |
| every session/surface/subtree lifecycle action | — | — | ✓ | ✓ |

**Resolution (recommended).** The canonical list is the **union**, stated once in `abstract-kernel/authority-model/` and
cited by all five. The prompt and HTSM agree with each other on `activate` and `enroll`; the controller alone lists
`level changes`; HTSM alone carries the lifecycle-action catch-all. Nothing in the union is wrong in any specification —
each list is simply incomplete relative to the others. This maps directly onto the suite invariant **INV-32**
(console-only operations are unrepresentable), which all three are already asserting.

**Why it matters.** `PMT-4:4` is what the Host AI is told; `CTR-4:7` is what the controller enforces. A Host AI that
follows the prompt may attempt `activate`, and the controller — which does not list it — has no node for it. Under
`INV-32` the request is unrepresentable either way, so there is no security hole; but the two specifications disagree
about what the set contains, which is exactly the kind of drift that produces an implementer's invention.

### 4.2 Enforcement authority

| Dimension | Finding | Status |
|---|---|---|
| Sole enforcement point | `CTR-1:1` (controller) is corroborated by `CNS-1:11` (enforcement lies outside the console), `PMT-1:6`, `RTM-1:5`, `ATA-3` | **aligned** |
| Authority, not authentication | `CTR-1:10`; `CNS-1:3`, `CNS-1:5`; `RTM-1:4`; `INV-ATA-*` | **aligned** |
| The controller never widens a grant | `CTR-1:18`; `RTM-1:4`; `CNS-5:6` | **aligned** |
| The controller has no notion of an operator | `CTR-1:4`; `CNS-1:2`; `ATR` Core Intent | **aligned** |
| The prompt cannot enforce | `PMT-1:5`–`PMT-1:7`; `CTR-1:7` | **aligned** |
| The Authority does not decide | `ATA-3`, `RTM-1:6` | **aligned** |

### 4.3 Invariants and non-conformance

| Dimension | Finding | Status |
|---|---|---|
| Component invariant lists | Five private lists, no shared index | **gap** → **closed by the kernel invariant register** |
| The prompt delegates its invariants | `PMT-1:14` — "as registered in the contracts" — no such register existed | **gap** → **closed** |
| HTSM cites eight undefined suite invariants | `INV-18`, `INV-26`, `INV-29`…`INV-34` | **gap** → **closed**, at the cited numbering |
| Non-conformance clause | Present in four of five; the Authority lacks one | **cosmetic** → **fixed** (§7, F9) |
| "Invariants apply at every profile" | `CNS-7:5`, `CTR-8:5`, `RTM-8:5`; **absent from the Authority** | **cosmetic** → **fixed** (§7, F10) |
| No profile may relax an invariant | Not stated suite-wide; three components flag it | **gap** → **closed** by `INV-0` in the register |

### 4.4 States and lifecycle

| Dimension | Finding | Status |
|---|---|---|
| Controller bootstrap states | `CTR-2:3` — ABSENT, LOADING, VERIFYING, READY | defined |
| Controller session states | `E-CTR-12` (BLOCKING): UNREGISTERED, REGISTERED, ACTIVE, INERT, INVALIDATED are named but never defined with pre/post conditions | **gap** |
| Prompt states | `E-PMT-07`: INERT described as "continues normal operation" then restricted, and not in the controller's model; `UNVERIFIED` (`PMT-6:6`) undefined | **divergent** |
| HTSM session states | Ten — CREATED, ACTIVE, PENDING_CONSOLE, DEGRADED, PAUSED, HALTED, CLOSED, EXPIRED, REVOKED, SUPERSEDED — delegated to a non-existent `os10-lifecycle.md §6` | **gap** |
| `DEGRADED` | A HTSM session state (HTSM Part B §11.4) **and** a controller health condition (`CTR-2:8`) | **divergent** — one word, two meanings |
| Runtime lifecycle states | install, register, activate, deactivate, update, invalidate, remove (`RTM-3`) | defined |
| SAFE-HALT | `CTR-1:15` depends on it; `CTR-2:8` names it only as a failure condition with no entry actions or authorised exit | **gap** |
| Epoch vs generation | `epoch` = monotonic counter within a session (`amk-authority.md:52`); HTSM uses `generation` for the same role. `CORE-O-01` records the collision | **divergent** |
| Package lifecycle states | downloaded, verified, registered, loaded, active, failed, rejected (runtime Core Intent) — kept distinct | aligned |
| `REGISTERED` names two objects | Runtime **package** state, console-local (`amk-runtime/03-lifecycle.md:11`–`:12`) **and** a controller **session** state (`amk-controller/amk-controller.md:115`) | **divergent** |
| `NORMAL` vs `Normal` | Controller **health** (`amk-controller/01-bootstrap-state.md:21`, `CTR-2:8`) vs controller **anomaly level** (`amk-controller/05-work-preauth-capability.md:32`, `CTR-6:13`) — differ only by case, unrelated scales | **divergent** |
| `High` names two ladders | Operational **profile** (`amk-controller/07-profiles.md:11`, `CTR-8:1`) and session **anomaly level** (`CTR-6:13`) | **divergent** |
| `SAFE-HALT` casing | `amk-controller/01-bootstrap-state.md:21` (condition) and `amk-controller/00-foundations.md:34` (action, lowercase); the prompt reports `SAFE-HALT` (`amk-prompt/05-behavior-failure.md:19`, `:24`, `:28`) | **cosmetic** |
| `UNVERIFIED` names three things | Prompt reported state (`PMT-6:6`); console local-cache status (`amk-console/05-validation-revocation.md:19`); Authority opaque denial (`INV-ATA-04`) | **divergent** |
| `SUSPENDED` names two things | Runtime **package** state (`amk-runtime/03-lifecycle.md:11`) and the Authority's *suspend* policy outcome (`amk-authority/amk-authority.md:20`, `amk-authority/03-verification.md:49`) | **cosmetic** |

**Resolution (recommended).** One session state machine, owned by `abstract-kernel/canonical-data-model/`, with three
namespaces kept distinct: **bootstrap** (controller), **session** (all), and **package** (runtime). `DEGRADED` resolves
to `DEGRADED_READ_ONLY` for the session state — the HTSM error code `degraded_read_only` already exists at
`HTSM-PART-A.md:232` — leaving `DEGRADED` unambiguous as controller health. `generation` and `epoch` are reconciled in
`D-HTSM-04` / `K-INV-O-02` of the HTSM completion plan.

### 4.5 Envelopes

**Divergence.** The console and controller maintain **two parallel, non-identical catalogues**: 17 shared types, two
console-only, one controller-only, and one row whose type name is open.

| Catalogue difference | Detail |
|---|---|
| `HandoffDelivery`, `HandoffReceipt` | Console catalogue only (`amk-console/02-envelope-interface.md:33`), direction "Console ↔ external" — the only catalogue types outside the two defined channels |
| `CapabilityToken` | Controller catalogue only (`amk-controller/02-envelope-interface.md:35`) — yet `CNS-7:2` requires the console to discover its profile "through a status or capability envelope" |
| Runtime subtree intake | **No type name anywhere.** `amk-controller/02-envelope-interface.md:28` literally reads "Runtime subtree intake (type name open, see CTR-O-13)". The console catalogue has no row for it at all |
| `SessionPolicy`, `CheckIn`, `WorkOrderProposal` | Named in `CNS-O-11` / `CTR-O-14`, in no catalogue. `CTR-6:14` requires "the console sets, with a signed command" — the command is unnamed |
| `Init Envelope` | Cited by `amk-authority.md:80` and session-router `RTR-39`; in no catalogue |
| Behavioural rules | `StatusQuery`, `StatusResponse`, `Notification`, `ValidatorEvidence` have none (`CNS-O-12`); `CapabilityToken` has none (`CTR-O-15`). The two lists are not the same set: CNS names `HandoffReceipt` and not `CapabilityToken`; CTR the reverse |
| `Genesis` direction | Circular: the console signs `Genesis` "MUST be 0 at handoff", but `CTR-5:8` has the *controller* create the `Handoff` "with genesis at 0" — the console-signed anchor would have to exist before the handoff it must attest |
| Map/surface key scope | `CNS-2:2` — the map key signs **surface-acceptance envelopes**; `CTR-4:4` — the controller verifies it on **surface maps** |
| Field-level schemas | Undefined everywhere (`CNS-O-01` = `CTR-O-01`) |
| Common field set | The console states positive MUSTs for nonce, timestamp, sequence, type identifier and signing (`CNS-3:1`–`CNS-3:5`); the controller states only rejections (`CTR-3:15`, `CTR-3:3`, `CTR-3:5`, `CTR-3:9`) with no positive MUST |

**Resolution (recommended).** One catalogue in `abstract-kernel/canonical-data-model/`, cited by both, with the
controller's catalogue becoming a reference to it rather than a parallel copy. The runtime-subtree intake envelope gets
a name; `SessionPolicy`, `CheckIn` and `WorkOrderProposal` are either added or struck.

### 4.6 Errors and outcomes

**Gap.** `E-CTR-14` (BLOCKING-adjacent) and `E-PMT-06` both record the same absence.

| Vocabulary | Referenced by | Defined in |
|---|---|---|
| `fork_detected`, `chain_broken` | `PMT-7:5` | HTSM Part B §15.2 (17 codes) — **not** in any component |
| `work_order_required` | `PMT-7:7` | **nowhere** — absent from HTSM's error table |
| `E_UNREACHABLE` | `PMT-6:7` | **nowhere** |
| `UNVERIFIED` (a state) | `PMT-6:6` | **nowhere** |
| "the standard error code" on bootstrap failure | `CTR-2:4` | **nowhere** |
| in-band API error catalogue | `RTM-O-12` | **nowhere** |

There is also no outcome taxonomy separating rejection, acceptance, completion and uncertainty, or separating
recoverable faults, integrity failures and authorization denials — the exact thing `E-CTR-14` asks for and the thing
`INV-47` (an outcome is never presented as done until acknowledged) presupposes.

**Resolution (recommended).** A suite error taxonomy in `abstract-kernel/canonical-data-model/`, with the HTSM error
table as its protocol-local subset. `work_order_required`, `E_UNREACHABLE` and `UNVERIFIED` are decided there — note that
`work_order_required` is the same vocabulary question as §4.1's proposal-vs-work-order split.

### 4.7 Defaults

| Dimension | Finding | Status |
|---|---|---|
| Where a default lives | **Only one document in the entire repository has a Defaults block** — `amk-authority.md:137`–`:150`, `DV-ATA-01`…`10`, with per-profile values where relevant | the de facto convention |
| `amk-console` defaults | **None.** `CNS-3:7` requires a skew window; `CNS-3:17` requires a `ttl`. Neither has a value or a `DV-` ID | **gap** |
| `amk-controller` defaults | **None.** `CTR-3:3` skew, `CTR-3:11` `ttl`, `CTR-7:1` "short-lived" capability token — no values | **gap** |
| `amk-runtime` defaults | `DV-RTM-01`…`08` listed with meanings but **no values**, under the heading "Defaults Needed in `amk-defaults-manifest`" — a document the Authority declares **discontinued** | **divergent** |
| HTSM defaults | 26 `DV-HTSM-*` / `DV-PROPOSAL-*` / `DV-CAP-TOKEN-*` / `DV-SUBTREE-*` / `DV-SESSION-*` IDs referenced, none defined | **gap** |
| The "defaults manifest" | Referenced live by the runtime (3 places) and by HTSM Part A:6; declared discontinued by the Authority (2 places); **the file does not exist** | **divergent** |
| `amk-ssot.md` | Cited normatively by `CNS-3:16` / `CTR-3:10` as the source of `contract_version`. **Does not exist** | **gap** |

**Resolution (recommended).** Adopt the **Authority's** convention as the suite rule: *each component declares its own
defaults in its own Defaults block, and only there.* This is one document's actual practice, it keeps defaults next to
the rules that use them, and it avoids a seventh document. The `amk-defaults-manifest` citations are then retargeted —
which is what §7, F6 does. HTSM's `D-HTSM-05` (in the HTSM completion plan) asks the same question and should be
answered consistently with this.

### 4.8 Keys and trust anchors

**Divergence.**

| Issue | Evidence | Assessment |
|---|---|---|
| **Public keys required to be secret** | `RTM-8:6`, `RTM-8:11`, `RTM-8:16` require *verification keys* to be "non-exportable and protected by an OS keystore" / "HSM, secure element or TEE". Verification keys are public; the runtime holds no signing key (`RTM-5:3`, `RTM-5:20`) | **The runtime's own `E-RTM-03` calls this out.** What needs protection is the integrity of trust *anchors* — the Release Key, registered publisher keys, package-hash records |
| **Unscoped "non-exportable keys" rows** | `amk-console/06-profiles.md:57` and `amk-controller/07-profiles.md:46` both read "\| Non-exportable keys \| SHOULD \| MUST \| MUST \|". Both components hold *public* pins (`CNS-2:3`, `CTR-3:2`). The Authority's equivalent row is properly scoped: "\| Signing key protection \| …" (`amk-authority/06-profiles.md:58`) | Should be scoped to **private** keys |
| **Trust-anchor mismatch** | The Authority's anchor is a **root public key** pinned out of band (`amk-authority/01-attestation-format.md:63`). The console pins the **Release Key fingerprint**, controller URLs and **Authority URLs** — and never the root key (`amk-console/01-keys-authentication.md:31`, `CNS-O-13`) | A console holding only an "Authority public key" would be trusting a *signing* key, and the root key signs metadata only. Live contradiction |
| **Release private key has no custodian** | `CTR-10:3` forbids it on the controller; `RTM-6:1` assigns signing to an undefined "Release Authority"; `CNS-2:12` / `CTR-10:10` pin only the fingerprint. No profile protects it | The most sensitive key in the suite has no stated home |
| **Session private key is unprotected** | No profile in any of the five specs constrains the session private key, which signs PoP and presentation proofs | `ATA-06` covers only the Authority's own signing keys |
| **Root-key rotation has no lifecycle** | `amk-authority/01-attestation-format.md:63` — "Root rotation is out-of-band re-pinning". The `status` vocabulary is defined only for `signing_keys` | Needs a rule in `cryptographic-lifecycle/` |
| **"A key MUST NOT serve more than one role"** | `CTR-10:2` — but each Authority signing key signs both attestations *and* revocation lists (`amk-authority/01-attestation-format.md:64`) | Different component, so not a direct breach; the principle is applied unevenly |

### 4.9 Profiles

| Dimension | Finding | Status |
|---|---|---|
| Peer profile rule | Console `CNS-7:23` — authorizer is the **operator**; controller `CTR-8:22` — the **administrator**; Authority `ATA-06:07` — "**operator or administrator**" and scoped to attestations rather than peers | **divergent** — same rule, three texts, different authorizer |
| Peer profile rule — runtime | **Absent.** `E-RTM-07` (BLOCKING) notes the runtime is the only core spec without one | **gap** |
| Profile discovery | `CNS-7:2`, `CTR-8:2`, `RTM-8:2` share identical wording — "discoverable through a status or capability envelope" — but `StatusResponse` and `CapabilityToken` have **no behavioural rules** | **gap** — identical defect, worded identically, in three specs |
| "Soft isolation" | `CNS-7:7`, `CTR-8:7`, `RTM-8:7` — one term, undefined everywhere, and it collides with profile-independent isolation rules (`CTR-7:10`, `RTM-5:10`, `RTM-3:22`) | **gap** — `E-CTR-01`, `E-CNS-02`, `E-RTM-01` all flag the same term |
| Isolation — what is isolated | Console: "between the console and any less-trusted components" (`CNS-7:13`). Controller: "Process or container isolation is required" — **no object named** (`CTR-8:12`). Runtime: "**Each runtime** … with no shared memory" (`RTM-8:12`). All three share the table row label "Process / container isolation" | **divergent** |
| Remote attestation | `CNS-7:19` (console's key environment, SHOULD), `CTR-8:18` (unspecified, SHOULD), `RTM-8:18` (the loader, SHOULD), `ATA-06:04` (the Authority, SHOULD) — four subjects, one label, four table labels. At High the Authority **consumes** RATS evidence (`ATA-06:04`) while the other three only produce it on a SHOULD | **cosmetic** |
| Profile exclusion by platform | Controller excludes languages/platforms that cannot meet High (`CTR-8:21`); the console, runtime and Authority have no equivalent rule | **divergent** |
| Language/platform row | Present in the controller's table (`amk-controller/07-profiles.md:55`), absent from the runtime's | **cosmetic** |
| Authority fallback profile | `ATA-06:05` — an unverifiable High claim → "**no higher than Standard**"; `ATA-06:07` — a profile that cannot be determined → "treated as **Basic**". The same state satisfies both, yielding different answers | **divergent** |
| Sequencing relaxation | `CNS-3:9`, `CNS-7:8`, `CTR-3:5`, `CTR-8:8` — two wordings, one scope, and both collide with the unconditional nonce rule (`CNS-3:8`, `CTR-3:4`) | **divergent** — `E-CNS-02`, `E-CTR-02` |

### 4.10 Audit

| Dimension | Finding | Status |
|---|---|---|
| What is audited | Console audits *envelopes* (`CNS-7:16`); the controller audits *state plus a signed hash chain* (`CTR-8:14`); the runtime audits *package lifecycle events* (`RTM-8:14`); the Authority audits *issuance* (`ATA-06:10`) | **divergent** — four different objects |
| Format | Only the Authority defines one: JSON Lines, hash-chained, hardware-protected under High (`ATA-06:10`). The console (`E-CNS-10`), controller (`E-CTR-10`) and runtime define none | **gap** |
| Retention value | Only the Authority has one (`DV-ATA-10`). The console says "for the retention period defined by policy" (`CNS-7:16`) — no policy exists | **gap** |
| Runtime self-audit contradiction | `RTM-8:14` requires the runtime to maintain a signed audit; `RTM-5:20` forbids the loader from writing to the controller's audit chain, which the controller records and signs (`CTR-7:4`) | **divergent** — `E-RTM-08` |
| Handoff logging vs full audit | `CNS-4:9` bans clear-text handoff logging under Standard/High; `CNS-7:16` requires a complete audit of emitted envelopes. No redaction rule exists | **gap** — `E-CNS-07` |

### 4.11 Freshness, time and recovery

| Dimension | Finding | Status |
|---|---|---|
| Skew window | `CNS-3:7` and `CTR-3:3` both require one; neither has a value. Only the Authority has one (`DV-ATA-07`) | **gap** — `CNS-O-04` = `CTR-O-08` |
| Nonce lifetime | `CNS-3:8`, `CTR-3:4` — no value. Authority: `DV-ATA-04` (60 s) | **gap** |
| Sequence-number storage | Required by `CNS-3:3`, `CTR-3:5`; storage undefined | **gap** |
| Envelope `ttl` | `CNS-3:17`, `CTR-3:11` — no value | **gap** |
| Recovery protocol | `CNS-6:11`, `CTR-7:12` both require one at Standard/High; **neither exists** (`CNS-O-03` = `CTR-O-07`). The runtime has no recovery rule at all | **gap** |
| Key distribution and rotation | `CNS-O-13`, `CTR-O-16`, `O-ATA-11` — the Authority public key's path to the console, the controller signing key's path to consoles, and the controller-to-Authority interface are all undefined | **gap** |

### 4.12 Document references

| Referenced document | Cited by | Exists? |
|---|---|---|
| `amk-ssot.md` | `CNS-3:16`, `CTR-3:10` — **normative** source of `contract_version` | **No** |
| `specs/core/OPEN-ITEMS.md` | `CNS-99:35`, `CTR-99:39`, `ATA-99:41`, plus `CORE-O-03`/`CORE-O-05` cited normatively | **No** |
| `amk-test-matrix` | three components, as "not yet updated" | **No** |
| `ssot-index` | `amk-authority.md:175` | **No** |
| `amk-defaults-manifest` | live in the runtime (3×) and HTSM Part A:6; **discontinued** per the Authority (2×) | **No** |
| `os10-lifecycle` | live in HTSM Part A:6; **discontinued** per the Authority | **No** |
| `amk-contracts` | live in HTSM Part A:19; **discontinued** per the Authority | **No** |
| `amk-registry` | `RTM-O-16` — the former name of `amk-authority` | renamed |
| `ClarificationAnswer` | `amk-runtime/06-system-packages.md:34` — live citation to a **removed** envelope | removed |
| `CNS-3` as an envelope reference | `amk-runtime/06-system-packages.md:34` — `CNS-3` is now the console's *file number* | stale |

### 4.13 Suite-wide findings

Three defects are identical across specifications, which is what makes them suite defects rather than component defects.

| Defect | Appears as | Components |
|---|---|---|
| **"Soft isolation" undefined** | `CNS-7:7`, `CTR-8:7`, `RTM-8:7` | console, controller, runtime |
| **High is unverifiable** | `E-CNS-08`, `E-CTR-09`, `E-RTM-07` — remote attestation is a SHOULD, profile discovery relies on `StatusResponse`/`CapabilityToken` which have no behavioural rules, and the peer rule trusts that self-declared profile | console, controller, runtime |
| **Side-channel resistance "expected"** | `CNS-7:22`, `CTR-8:20` — not RFC 2119 language, untestable | console, controller |
| **Audit format undefined** | `E-CNS-10`, `E-CTR-10`, `E-RTM-08` | console, controller, runtime |
| **Fresh confirmation list** | `CNS-2:7` / `CNS-7:15` omit runtime registration, which `RTM-4:6` requires at **every** profile (`RTM-O-20`) | console, runtime |
| **Reference-ID stability rule missing** | Present in the Authority, console, controller and runtime; **absent from the prompt** (`E-PMT-12`) | prompt |
| **Suite-compatibility row missing** | Present in the Authority, console and controller header tables; **absent from the prompt and runtime** (`E-RTM-12`) | prompt, runtime |
| **"Exactly three components"** | `amk-runtime/99-open-items.md:11` (RTM-O-01) — the suite now has five | runtime |
| **The Authority has no non-conformance clause** | All four siblings have one (`CNS-1:16`, `CTR-1:20`, `PMT-1:15`, `RTM-1:20`) | authority |

### 4.14 Levels and ladders

The suite uses **at least six distinct level vocabularies** and never defines them in one place. `PMT-1:11` asserts
that "Assurance levels are distinct from HTSM operational levels and kernel operating levels" — but *operational* and
*operating* name two different things, and only one of them is defined anywhere in the five components.

| Ladder | Where it is used | Defined? |
|---|---|---|
| Assurance levels `L0` / `L1` / `L2` | `amk-prompt/00-foundations.md:24`–`:25` (`PMT-1:8`–`PMT-1:9`) | partially — the HIL attestation mapping is open |
| "HTSM operational levels" | `amk-prompt/00-foundations.md:27` (`PMT-1:11`) | **no** — defined in the HTSM protocol, outside the five components |
| "kernel operating level" | `amk-controller/04-surface-runtime.md:27` (`CTR-5:11`); `amk-prompt/04-authority-capability.md:14` (`PMT-5:4`); `amk-runtime/02-contract.md:37` (`RTM-3:12`); `amk-runtime/04-loader.md:23` (`RTM-5:4`) | **no** |
| "Operating level" — the lower of console-allowed and verified capability | `amk-controller/05-work-preauth-capability.md:28` (`CTR-6:12`) | partially |
| "baseline level" | `amk-controller/05-work-preauth-capability.md:27` (`CTR-6:11`) | **no** — `E-CTR-07` flags it |
| "minimum kernel level" / `min_kernel_level` | `amk-runtime/01-package-format.md:27` (`RTM-2:4`); `amk-runtime/02-contract.md:30` (`RTM-3:8`) | partially |
| "level cap" | `amk-console/99-open-items.md:22` (`CNS-O-11`); `amk-controller/99-open-items.md:25` (`CTR-O-14`) | **no** — `SessionPolicy` has no envelope |

**`profile` means two things inside one module.** `amk-controller/05-work-preauth-capability.md:26`–`:27` (`CTR-6:10`,
`CTR-6:11`) says "a test runs once per **Host AI profile**" and "a new or unverified **profile** starts at the
**baseline level**"; `amk-controller/07-profiles.md:11` (`CTR-8:1`) says the profile is one of `basic`, `standard`,
`high`. The same word, same component, same module file for the first two.

**The instruction class list and the precedence list do not match.** `PMT-3:1` lists six *classes* — kernel
instructions, runtime instructions, capability instructions, task instructions, agent requests, **external data**;
`PMT-3:2` lists six *precedence levels* — kernel invariant → kernel policy → runtime contract → capability policy →
task instruction → agent request; `CTR-1:11` lists a third variant — immutable invariants, kernel policy, runtime
contract, capability policy, **task policy**, agent request. Three six-element lists, three contents. `E-PMT-09`
already flags this; it is a suite-level defect, not a prompt defect.

### 4.15 Counters, epochs and revisions

| Counter | Defined where | Increments when | Scoped to | Problem |
|---|---|---|---|---|
| `epoch` — **session sense** | `amk-authority/amk-authority.md:52` only, in the informative Terms block | re-key or role change | session + role | **No normative module file of any of the five components defines it.** The controller owns the counter (`CTR-2:12`, `CTR-1:1`) but never defines what increments it or what an epoch change invalidates — `E-CTR-15` |
| `epoch` — **release anti-rollback sense** | `amk-controller/01-bootstrap-state.md:11` (`CTR-2:1`); `amk-prompt/01-identity-bootstrap.md:11`, `:18` (`PMT-2:1`, `PMT-2:5`) | **never defined** | release | The same word covers two counters inside one component set. This is the `CORE-O-01` collision |
| `generation` | **nowhere in the five components** — one occurrence, `amk-controller/04-surface-runtime.md:28` (`CTR-5:12`), delegating it out of the specification | HTSM: a re-handoff (`HTSM-PART-A.md:255`) | session incarnation | A word the controller uses and does not define |
| surface `revision` | `amk-controller/04-surface-runtime.md:23` (`CTR-5:10`); `amk-runtime/03-lifecycle.md:34`, `:41` | mid-session runtime inclusion and removal | session | **No definition, no counter, no envelope field.** The console never mentions a surface revision, yet `amk-console/05-validation-revocation.md:20` (`CNS-6:7`) requires strict freshness and sequencing on all state-bearing envelopes — with nothing to sequence against. HTSM distinguishes `surface_version` from subtree *revisions* (`HTSM-PART-B.md:38`); the core specs collapse both into "revision" |
| `sequence` | `amk-console/02-envelope-interface.md:13` (`CNS-3:3`) | per envelope | sender identity | Storage requirements undefined (`CNS-O-04` = `CTR-O-08`). The Authority's audit `seq` (`ATA-06:10`) is a separate global counter |
| `nonce` | `amk-console/02-envelope-interface.md:11` (`CNS-3:1`) | per envelope | **unstated** | `CNS-3:8` rejects any nonce "has been seen before", implying a global set, while `CNS-3:3` scopes *sequence* to the sender identity. Two independent senders can collide if the set is global; `CNS-3:8`'s wording is wrong if it is per-sender. Neither is stated |
| `re-key` | `amk-authority/amk-authority.md:52` (session key rotation) vs `amk-console/05-validation-revocation.md:30` (`CNS-6:11`, console identity recovery) | — | — | Two meanings, and **no rule says a console re-key advances an epoch** |

**The epoch counter's only supply route is blocked.** `INV-ATA-02` makes the controller's session record the sole source
of an epoch, but the controller-to-Authority interface that carries it (`O-ATA-11`) is **blocking for a 1.2.0 release**
(`amk-authority/99-open-items.md:25`). The session router defines an epoch lifecycle the controller never does
(`session-router/02-addressing-registration-epochs.md:33`).

### 4.16 Revocation, registration and object identity

| Dimension | Finding | Status |
|---|---|---|
| "Immediate revocation" | The catalogue says "**Immediate revocation** of session, runtime, or pre-auth" (`amk-console/02-envelope-interface.md:37`, `amk-controller/02-envelope-interface.md:32`), but `CTR-7:7` halts runtimes **at the next operation boundary** and `RTM-4:19` says capability tokens end **at expiry**. Only pre-authorizations have a revocation registry (`CTR-7:8`) — `E-CTR-05` | **divergent** — "immediate" is unachievable for capability tokens |
| `mandatory` update blocking | `RTM-6:16` assigns the block to the **console**; inclusion is authorized by the console but **enforced by the controller**; the loader's pre-load list (`RTM-5:4`) has no superseded check | **gap** — no component assigns the block to the party that can enforce it |
| Package lifecycle authority | `RTM-1:1` — the runtime is "the **sole defined authority** over … the lifecycle of runtime packages"; `RTM-4:2` — `ARRIVED`, `VERIFIED`, `REGISTERED` are **console-local** | **divergent** — two homes for one state, no precedence rule |
| `amk-core` identity | `amk-runtime/amk-runtime.md:16` — "the kernel itself … has no runtime. Everything beyond it, including `amk-core`, arrives as a runtime package"; `RTM-7:11` — "`amk-core` is a **system runtime**. Its prompts scaffold through the kernel subtree"; `RTM-7:7` — "a core runtime is a system package" | **divergent** — system package / system runtime / core runtime for one object, with a third inclusion path (session-start only, `RTM-7:13`) |
| Runtime registration obligations | `CNS-5:13` — registration is "console-local and involves **no envelope**"; `RTM-4:5` imposes verification duties on the console and `RTM-6:14` requires **both** console and loader to apply the conformance rule set | **divergent** — `E-RTM-10` |
| The console's first-run wizard | `RTM-7:5` — "Registration of the system packages is part of the console's **first-run wizard**"; the console spec removes all first-run-wizard material (`amk-console/99-open-items.md:28`) and puts first-run experience out of scope (`amk-console.md:21`) | **divergent** |
| Auto Assist continuation | `CNS-5:12` — continuation is governed "**solely**" by an applicable pre-authorization; `CTR-6:15` — continuation requires Auto Assist **on**, a covering envelope **present**, and the anomaly level at or below the ceiling. `CNS-5:11` allows Auto Assist to be "a standing pre-authorization scope", which can never satisfy `CTR-6:15` | **divergent** |
| The console sets a field it has no envelope for | `CTR-6:14` — "the console sets, with a signed command, the allowed anomaly ceiling and the Auto Assist flag"; `CNS-O-11` / `CTR-O-14` name a `SessionPolicy` envelope that is in no catalogue | **gap** |
| Role binding | `amk-console/00-foundations.md:22` conflates "Role / Runtime" as external entities; `INV-ATA-02` binds a role to a session record; the controller never mentions roles, a session record or `declared_roles` — `E-CTR-03` | **gap** |
| Controller bootstrap | `CTR-2:5` — after verification the controller is "**idle and waits for connections**"; `CTR-2:6` — it "MUST enforce strict bootstrap readiness **before accepting any connection**" | **cosmetic** |

---

## 5. The shared-vocabulary register

Some things must be **one** thing across the suite, not five parallel statements. This is the list; each entry names the
single home and the components that must cite it.

| # | Single thing | Home | Components that must cite it |
|---|---|---|---|
| SV-1 | The list of console-only operations | `abstract-kernel/authority-model/` | CNS, CTR, PMT, RTM, HTSM |
| SV-2 | The suite invariant register | `abstract-kernel/invariants/` ✔ **delivered** | ATA, CNS, CTR, PMT, RTM, HTSM |
| SV-3 | The envelope catalogue and field-level schemas | `abstract-kernel/canonical-data-model/` | CNS, CTR, RTM |
| SV-4 | The error and outcome taxonomy | `abstract-kernel/canonical-data-model/` | CNS, CTR, PMT, RTM, HTSM |
| SV-5 | The session state machine | `abstract-kernel/canonical-data-model/` | CNS, CTR, PMT, HTSM |
| SV-6 | The audit record format and retention | `abstract-kernel/canonical-data-model/` | CNS, CTR, RTM, ATA |
| SV-7 | Skew, nonce lifetime, TTL, sequence storage | `abstract-kernel/time-and-freshness/` | CNS, CTR, ATA |
| SV-8 | Key roles, protection classes and lifecycle | `abstract-kernel/cryptographic-lifecycle/` | ATA, CNS, CTR, RTM |
| SV-9 | Trust-anchor pinning and distribution | `abstract-kernel/trust-bootstrap/` | ATA, CNS, CTR, RTM |
| SV-10 | Profile definitions and the peer rule | `abstract-kernel/operating-levels/` | ATA, CNS, CTR, RTM, PMT |
| SV-11 | The cross-spec open-item register (`CORE-O-nn`) | `specifications/core/OPEN-ITEMS.md` | all five |
| SV-12 | Suite version and the `contract_version` value | `abstract-kernel/scope/` | CNS, CTR, HTSM |
| SV-13 | The conformance rule set ("protocol standards") | `abstract-kernel/security-requirements/` | CNS, RTM |
| SV-14 | The recovery protocol after key loss or compromise | `abstract-kernel/cryptographic-lifecycle/` | CNS, CTR |

**Only SV-2 exists today.** SV-11 and SV-12 are cited normatively by two and two components respectively and are
missing — the two most-referenced absences in the suite.

---

## 6. Cross-component open-item register

The suite's cross-component register (`CORE-O-nn`) is cited by three components and does not exist. It is proposed here,
consolidating the duplicated items from §4 into one owned list.

| ID | Item | Duplicated in | Owner area | Severity |
|---|---|---|---|---|
| **CORE-O-01** | Session state machine: states, transitions, pre/post conditions, and the relationship between bootstrap, session and package states | `E-CTR-12`, `E-PMT-07`, HTSM §2 | SV-5 | **Blocking** |
| **CORE-O-02** | Error and outcome taxonomy separating rejection, acceptance, completion and uncertainty, and recoverable faults, integrity failures and authorization denials | `E-CTR-14`, `E-PMT-06`, `RTM-O-12` | SV-4 | **Blocking** |
| **CORE-O-03** | The canonical console-only operation list | `E-PMT-10`, §4.1 | SV-1 | **Blocking** |
| **CORE-O-04** | Envelope catalogue with field-level schemas, one catalogue rather than two | `CNS-O-01`, `CTR-O-01`, `RTM-O-13` | SV-3 | **Blocking** |
| **CORE-O-05** | Audit record format, hash chain, retention and checkpoint protection | `E-CTR-10`, `E-CNS-10`, `E-RTM-08` | SV-6 | **Blocking** |
| **CORE-O-06** | Skew windows, nonce lifetime, TTL and sequence-number storage, with per-profile values | `CNS-O-04`, `CTR-O-08` | SV-7 | **Blocking** |
| **CORE-O-07** | Defaults location: the per-component Defaults block versus a suite manifest | `E-RTM-05`, `E-RTM-12`, HTSM Part A §8 | SV-4 / `D-HTSM-05` | **Blocking** |
| **CORE-O-08** | Trust-anchor pinning and key distribution: the Authority root key to the console, the controller signing key to consoles, the Release private key's custodian | `O-ATA-11`, `CNS-O-13`, `CTR-O-16` | SV-9 | **Blocking** |
| **CORE-O-09** | Recovery protocol after key loss or compromise at Standard and High | `CNS-O-03`, `CTR-O-07` | SV-14 | **Blocking** |
| **CORE-O-10** | The meaning of "soft isolation" and the minimum isolation Basic must keep | `E-CTR-01`, `E-CNS-02`, `E-RTM-01` | SV-10 | **Blocking** |
| **CORE-O-11** | The peer profile rule: one text, one authorizer, applied by all four profiled components | `E-RTM-07`, §4.9 | SV-10 | High |
| **CORE-O-12** | Profile discovery: behavioural rules for `StatusResponse` and `CapabilityToken` | `E-CNS-08`, `E-CTR-09`, `E-RTM-07`, `CNS-O-12`, `CTR-O-15` | SV-3, SV-4 | High |
| **CORE-O-13** | The epoch/generation collision | `CORE-O-01` (existing, renamed scope) | SV-5 | High |
| **CORE-O-14** | The suite version, `contract_version` and the missing `amk-ssot.md` | `CNS-3:16`, `CTR-3:10` | SV-12 | High |
| **CORE-O-15** | Behavioural rules for `Notification`, `ValidatorEvidence`, `HandoffReceipt` | `CNS-O-12`, `CTR-O-15` | SV-3 | Medium |
| **CORE-O-16** | The runtime-subtree intake envelope name, and whether `RuntimeInclusionDecision` is an envelope | `CNS-O-10`, `CTR-O-13`, `RTM-O-02` | SV-3 | Medium |
| **CORE-O-17** | `SessionPolicy`, `CheckIn` and `WorkOrderProposal` — in a catalogue or struck | `CNS-O-11`, `CTR-O-14` | SV-3 | Medium |
| **CORE-O-18** | The `Genesis` envelope direction and its relationship to the `Handoff` genesis value | `CNS-O-09`, `CTR-O-12` | SV-3 | Medium |
| **CORE-O-19** | The map/surface key scope: surface-acceptance envelopes, surface maps, or both | `CNS-2:2`, `CTR-4:4` | SV-8 | Medium |
| **CORE-O-20** | The fresh-confirmation list: whether runtime registration and credential changes are always-confirm | `E-CNS-04`, `RTM-O-20` | SV-10 | Medium |
| **CORE-O-21** | The conformance rule set ("protocol standards") applied by console and loader | `RTM-O-08` | SV-13 | Medium |
| **CORE-O-22** | Profile exclusion by platform capability | §4.9 | SV-10 | Low |
| **CORE-O-23** | The Authority's fallback profile when its profile cannot be determined | `ATA-06:05` vs `ATA-06:07` | SV-10 | Low |
| **CORE-O-24** | Session private key protection — unconstrained in all five profile tables | §4.8 | SV-8 | High |
| **CORE-O-25** | Root-key rotation lifecycle and the status vocabulary for root keys | §4.8 | SV-8 | Medium |
| **CORE-O-26** | The level vocabulary: one definition per ladder, and which ladders exist | §4.14 | SV-10 | High |
| **CORE-O-27** | `profile` used for both the operational profile and the Host AI profile in the same controller module | §4.14 | SV-10 | Medium |
| **CORE-O-28** | One instruction-class list and one precedence list, shared by the prompt and the controller | §4.14 | SV-4 | High |
| **CORE-O-29** | The two senses of `epoch` — session counter and release anti-rollback counter — and the controller's obligation to define and own the session one | §4.15 | SV-5 | **Blocking** |
| **CORE-O-30** | The definition and counter of a surface revision, and its console-side counterpart | §4.15 | SV-5 | High |
| **CORE-O-31** | Envelope nonce scope, and its relationship to the sender-scoped sequence number | §4.15 | SV-3, SV-7 | Medium |
| **CORE-O-32** | Revocation immediacy versus capability-token expiry | §4.16 | SV-4 | High |
| **CORE-O-33** | Which component holds authoritative package lifecycle state | §4.16 | SV-5 | High |
| **CORE-O-34** | `amk-core` identity: system package, system runtime, or core runtime | §4.16 | SV-5 | Medium |
| **CORE-O-35** | Who blocks a superseded version after a `mandatory` update takes effect | §4.16 | SV-3 | Medium |
| **CORE-O-36** | Auto Assist continuation conditions, and the console's `SessionPolicy` envelope | §4.16 | SV-3, SV-4 | High |
| **CORE-O-37** | Whether the console's runtime-registration duties need a counterpart in the console specification | §4.16 | SV-3 | Medium |
| **CORE-O-38** | The `Duplicate of …` stubs, which the ID scheme requires to be marked `RETIRED` | §4.13 | SV-12 | Low |

> **Note on numbering.** `CORE-O-01` is cited normatively by `amk-controller.md` and `amk-prompt.md` with an unknown
> scope, and the Authority's `CORE-O-05` is cited as the pending conversion of the Authority's own ID scheme. This
> proposal **reserves both numbers for their existing scope** and assigns new numbers from `CORE-O-02` onward only
> where the existing numbers are unclaimed. Where the existing `CORE-O-01` or `CORE-O-05` scope is unknown, the
> conflict is recorded here for the operator to settle — see §8, R-11.

---

## 7. Corrections applied

Mechanical or forced corrections, now in the working tree. Each is a case where one component already states the answer,
or where the correction is a fact about the repository.

| # | File | Correction | Basis |
|---|---|---|---|
| **F1** | `amk-prompt/amk-prompt.md` | Added the `Suite compatibility` row to the header table | Authority, console and controller all carry it; `E-RTM-12` flags its absence in the runtime |
| **F2** | `amk-runtime/amk-runtime.md` | Added the `Suite compatibility` row to the header table | As F1 |
| **F3** | `amk-prompt/amk-prompt.md` | Added "Once published, an ID is never reassigned. A withdrawn requirement keeps its ID and is marked `RETIRED`." to the Reference ID Scheme | The other four state it verbatim; `E-PMT-12` flags its absence |
| **F4** | `amk-runtime/99-open-items.md` | RTM-O-01: "AMK is defined as exactly three components" → the suite now defines five | `E-RTM-09` flags it; the suite has five core components |
| **F5** | `amk-runtime/06-system-packages.md` | Removed the live citation to the removed `ClarificationAnswer` envelope and the stale `CNS-3` reference; replaced with the `amk-core` module reference | `amk-console/99-open-items.md:33` and `amk-controller/99-open-items.md:37` both record it as removed; `E-RTM-12` flags the stale citation |
| **F6** | `amk-runtime/99-open-items.md`, `amk-runtime/04-loader.md` | Retargeted the three live `amk-defaults-manifest` citations to the component's own Defaults block, matching the Authority's convention | The Authority declares the manifest discontinued twice; `E-RTM-05` and `E-RTM-12` flag the citations |
| **F7** | `amk-console/99-open-items.md`, `amk-controller/99-open-items.md`, `amk-authority/99-open-items.md` | Retargeted the `specs/core/OPEN-ITEMS.md` citation to `specifications/core/OPEN-ITEMS.md` and noted that the register is not yet created | The path changed in the repository restructure; the cited path resolved to nothing |
| **F8** | all five `00-foundations.md` modules | Added a "Suite kernel invariants" pointer mapping the component's own invariants to the suite `INV-n` register, and (for the console, controller, runtime) a `RETIRED`-conforming cross-reference | Creates the alignment link that §3 of the register describes; purely additive |
| **F9** | `amk-authority/06-profiles.md` | Added "The invariants defined in file 1 apply at every profile." | The console (`CNS-7:5`), controller (`CTR-8:5`) and runtime (`RTM-8:5`) each state it; the Authority was the only omission |
| **F10** | `amk-authority/00-foundations.md` | Added the non-conformance clause equivalent to `CNS-1:16`, `CTR-1:20`, `PMT-1:15` and `RTM-1:20` | Four of five components carry it |
| **F11** | `amk-runtime/99-open-items.md` | RTM-O-16's dependency `ATA-10` and RTM-O-03's `CTR-13` / RTM-O-12's `CTR-14f` are review IDs that match no current rule (`E-RTM-12`) — **recorded, not corrected**, because the intended targets are ambiguous | `E-RTM-12` |
| **F12** | `amk-runtime/01-package-format.md` | RTM-2:4 points to `CTR-O-13` for risk classes; `CTR-O-13` concerns envelope naming (`E-RTM-12`) — **recorded, not corrected**, because the intended item is ambiguous | `E-RTM-12` |

---

## 8. Corrections that need an owner decision

Each of these is a genuine disagreement between specifications. The recommendation is stated; none has been applied.

| # | Question | Options | Recommendation | Blocks |
|---|---|---|---|---|
| **R-01** | Canonical term for the Host AI's request record: *proposal* or *work order*? | (a) *proposal* suite-wide; (b) *work order* suite-wide; (c) split — *work order* = console-issued grant, *proposal* = HTSM request record | **(c)**. The concepts are genuinely different: the console issues work orders (`CNS-5:3`), the Host AI proposes (`PMT-7:8`), and the controller separates them (`CTR-6:3`). This also makes `work_order_required` a legitimate code rather than an orphan | `CORE-O-03`, `D-HTSM-01` |
| **R-02** | Canonical console-only operation list | The union (§4.1) | **The union**, stated once in `authority-model/` | `CORE-O-03` |
| **R-03** | Who may authorise a lower-profile peer: the operator, the administrator, or both? | `CNS-7:23` (operator), `CTR-8:22` (administrator), `ATA-06:07` (operator or administrator) | **"operator or administrator"**, matching the Authority — and the console is the operator's seat while the controller's administrator is the deployment operator | `CORE-O-11` |
| **R-04** | Does the runtime get a peer profile rule? | Yes, mirroring the other three; or no, because the runtime is not an envelope endpoint | **Yes.** The runtime's High profile is otherwise unverifiable, exactly as `E-RTM-07` says | `CORE-O-11` |
| **R-05** | Defaults location | (a) per-component Defaults block; (b) a suite manifest; (c) kernel-area manifest | **(a)** — the Authority's convention, and the only practice in the repository | `CORE-O-07`, `D-HTSM-05` |
| **R-06** | What replaces the two parallel envelope catalogues? | (a) one catalogue in the kernel area; (b) keep both, cross-referenced; (c) controller catalogue becomes normative | **(a)** — a catalogue that exists in two places will diverge again, as it has | `CORE-O-04` |
| **R-07** | Name for the runtime-subtree intake envelope, and is package intake a separate envelope? | — | Name it; decide whether package bytes are uploaded by the console or fetched by hash — the runtime's own `RTM-O-02` prefers fetch-by-hash | `CORE-O-16` |
| **R-08** | Should the console-only operation list live in `authority-model/` or `canonical-data-model/`? | — | `authority-model/` — it is an authority boundary, not a data shape | `CORE-O-03` |
| **R-09** | Should the suite keep a separate `OPEN-ITEMS.md`, or fold `CORE-O-nn` into the kernel area? | (a) `specifications/core/OPEN-ITEMS.md`; (b) `abstract-kernel/scope/` | **(a)** — three components cite that exact path normatively; moving it again re-creates the problem F7 just fixed | `CORE-O-11`…`CORE-O-25` |
| **R-10** | What is `contract_version`, and where does the suite version live? | — | `abstract-kernel/scope/`, per `CNS-3:16` / `CTR-3:10` | `CORE-O-14` |
| **R-11** | What were `CORE-O-01` and `CORE-O-05` originally? | — | The Authority cites `CORE-O-05` as the pending conversion of its own ID scheme. `CORE-O-01` is cited by the controller with an unknown scope. Both numbers must be recovered before the register is published | the whole register |
| **R-12** | Rename the HTSM session state `DEGRADED`? | (a) `DEGRADED_READ_ONLY`; (b) qualify the controller's health state instead | **(a)** — the error code `degraded_read_only` already exists, so the rename costs one identifier | `CORE-O-01` |
| **R-13** | What is the `work_order_required` / `E_UNREACHABLE` / `UNVERIFIED` vocabulary, and where does it live? | — | The kernel error taxonomy (SV-4), with HTSM's 17 codes as its protocol subset | `CORE-O-02` |
| **R-14** | Does the `Genesis` envelope originate with the console or the controller? | (a) console signs, controller countersigns; (b) controller originates, console acknowledges | **(b)** — `CTR-5:8` has the controller producing the `Handoff` "with genesis at 0" after `SurfaceAcceptance`; the console-signed `Genesis` as currently written would have to predate the handoff it must attest | `CORE-O-18` |
| **R-15** | Which key signs a surface — the console key or a distinct map key? | (a) console key only; (b) map key for acceptance, console key for maps (current `CTR-4:4`); (c) map key for both | **(c)** — matches `CNS-2:2`'s "used exclusively" wording and removes the contradiction with `CTR-4:4` | `CORE-O-19` |
| **R-16** | Is runtime registration always-confirm at every profile? | — | **Yes**, per `RTM-4:6` and `RTM-O-20`; add it to `CNS-2:7` and `CNS-7:15` | `CORE-O-20` |
| **R-17** | What does "soft isolation" permit at Basic? | — | Define it in `security-requirements/`, or delete it. Under `INV-0` a profile that cannot state what it relaxes has relaxed nothing | `CORE-O-10` |
| **R-18** | Which of the two `epoch` senses keeps the name? | (a) session counter keeps `epoch`, release counter renamed; (b) release keeps `epoch`, session renamed; (c) both renamed | **(a)**. The session epoch is bound into attestation fields and the session record; the release counter is used in two places and can be renamed to `release_epoch` cheaply | `CORE-O-29` |
| **R-19** | Does the controller's `generation` survive, or does HTSM adopt `epoch`? | — | Keep `generation` in HTSM (it is already published as a protocol term) and forbid `generation` elsewhere in the suite | `CORE-O-29` |
| **R-20** | Which ladder is the "operating level" the HTSM protocol uses? | (a) HTSM operational level 1–2 (`HTSM-PART-B.md:45`); (b) the controller's `min_kernel_level`; (c) they are the same | **(c)** — the HTSM level and `min_kernel_level` are the same ladder seen from two ends, and naming them differently is the drift | `CORE-O-26` |
| **R-21** | Do "Duplicate of …" stubs get `RETIRED`? | (a) yes, mark them `RETIRED`; (b) introduce a third marker for delegations | **(b)** — a delegation is not a withdrawal, and marking it `RETIRED` would misstate it. The ID policy needs a third marker | `CORE-O-38` |
| **R-22** | Is revocation of a capability token immediate or at expiry? | (a) change the catalogue wording to "at the next operation boundary"; (b) add a capability-token revocation registry | **(a)**. `CTR-7:8` already says only pre-authorizations have a registry; the wording is what is wrong | `CORE-O-32` |
| **R-23** | Which component holds authoritative package lifecycle state? | (a) the runtime; (b) the console; (c) split, with a precedence rule | **(c)** — the console owns `ARRIVED`/`VERIFIED`/`REGISTERED` (`RTM-4:2`) and the runtime owns the rest, but the split must be stated | `CORE-O-33` |
| **R-24** | Does the console's registration workflow need an envelope? | (a) yes, name it; (b) no — keep it console-local and record the obligations in the console spec instead | **(b)** — `CNS-5:13` already says console-local; the missing piece is the console's verification duties, not an envelope | `CORE-O-37` |

---

## 9. Residual gaps after these corrections

Applying §7 and §8 leaves the suite in this state.

| Closed by this report | Still open |
|---|---|
| The suite invariant register and the eight HTSM anchor citations | The session state machine (`CORE-O-01`) |
| The prompt's missing invariant register and non-conformance clause | The error and outcome taxonomy (`CORE-O-02`) |
| The Authority's missing all-profile clause and non-conformance clause | The envelope catalogue and schemas (`CORE-O-04`) |
| Header and reference-scheme inconsistencies across all five | The audit format (`CORE-O-05`) |
| Stale citations to removed and discontinued documents | Freshness, TTL and sequence values (`CORE-O-06`) |
| "Exactly three components" | Trust-anchor distribution and the Release key's custodian (`CORE-O-08`) |
| The cross-spec register's path | The recovery protocol (`CORE-O-09`) |
| | "Soft isolation" (`CORE-O-10`), the peer rule (`CORE-O-11`), profile discovery (`CORE-O-12`) |

**Nothing in §7 changes the security posture of any component.** The applied corrections are metadata, stale
citations, and the addition of clauses that make existing invariants non-relaxable. Every behavioural change is in §8,
where it waits for a decision.

---

## 10. Verification

| Check | Method | Result |
|---|---|---|
| The invariant register defines all eight HTSM-cited IDs | `INV-18`, `INV-26`, `INV-29`…`INV-34` present with matching meaning | **Pass** |
| Every component invariant maps to a suite invariant | §5 of the register; 33 component invariants mapped | **Pass** |
| The prompt's five prose invariants map to suite invariants | `PMT-1:14` → `INV-1`, `INV-4`, `INV-38`, `INV-39`, `INV-20` | **Pass** |
| No component invariant is left unmapped | Authority 7, console 8, controller 7, prompt 2, runtime 9 = 33 | **Pass** |
| The corrections in §7 are applied | Working tree, `specifications/core/components/` | **Pass** |
| The corrections in §8 are **not** applied | Working tree | **Pass** — §8 is a register, not an edit |
| Every alignment finding cites a file and line | §4 | **Pass** |
| Every duplicated cross-component open item appears exactly once in §6 | `CNS-O-01`/`CTR-O-01`, `CNS-O-04`/`CTR-O-08`, `CNS-O-03`/`CTR-O-07`, `CNS-O-10`/`CTR-O-13`, `CNS-O-11`/`CTR-O-14`, `CNS-O-12`/`CTR-O-15`, `O-ATA-11`/`CNS-O-13`/`CTR-O-16` | **Pass** |

---

*Draft for review. The kernel invariant register this report depends on is at
[`kernel/invariants/README.md`](kernel/invariants/README.md).*
