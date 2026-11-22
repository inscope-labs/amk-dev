# Kernel Invariants — Abstract Kernel

| Field | Value |
|---|---|
| Area | `abstract-kernel/invariants/` — the suite-level invariant register |
| Status | **Draft.** Establishes the shared invariant vocabulary for the five core components, the HTSM protocol, and the companion protocols. |
| Version | `1.0-draft` |
| Suite compatibility | `OS10-AMK 2.x` |
| Scope | Every AMK deployment, at every operational profile. |
| Authority | **Kernel invariants outrank every other input.** A component, profile, protocol or implementation may add requirements; it may never relax one. |
| Normative language | MUST / MUST NOT / SHOULD / MAY as in RFC 2119. |
| Cross-references | `scope/`, `principles/`, `trust-model/`, `authority-model/`, `trust-bootstrap/`, `cryptographic-lifecycle/`, `time-and-freshness/`, `operating-levels/`, `canonical-data-model/`, `security-requirements/`, `threat-model/`, `architecture/`, `backend-extension-model/` |

---

## 1. Purpose

This document is the **single, shared inventory of invariants** for the Agentic Micro Kernel. It exists so that the five
core components, the protocols, and an implementer can all refer to the same numbered statements instead of each
restating the same principle in its own words.

It resolves three concrete defects recorded elsewhere in the suite:

- The Host AI Kernel Prompt specification requires that its invariants be "registered in the contracts"
  (`amk-prompt/00-foundations.md`, `PMT-1:14`) — a register that did not exist. This document is it.
- The HTSM protocol cites eight suite-level invariant IDs — `INV-18`, `INV-26`, `INV-29`, `INV-30`, `INV-31`,
  `INV-32`, `INV-33`, `INV-34` — that were defined nowhere in the repository. All eight are defined below, at the
  stated numbering, and are marked **⟨HTSM anchor⟩**.
- The five components each carry their own private invariant list (`INV-ATA-01`…`INV-ATA-07`, `CNS-1:9`…`CNS-1:15`,
  `CTR-1:14`…`CTR-1:19`, `PMT-1:14`–`PMT-1:15`, `RTM-1:12`…`RTM-1:19`) with no shared index and no stated relationship
  between them.

### 1.1 What an invariant is here

An invariant is a statement that **no conformant implementation may violate**, at any profile, for any reason, including
performance, convenience, legacy compatibility, or a permissive reading of another rule.

An invariant is not:

- a *default* — defaults are tunable values and live in the owning component's Defaults block (`DV-…`);
- a *recommendation* — recommendations are SHOULD and may be declined;
- a *profile relaxation* — a profile may add obligations; it may not remove an invariant.

### 1.2 Relationship to component invariants

Each component keeps its own invariants, because each is enforced at a different boundary and each component's ID
scheme must stay stable. **The two lists are not competing; they are two views of one set.** The mapping in §5 states,
for every component invariant, the suite invariant it realises.

Where a component invariant and a suite invariant appear to differ in wording, the suite invariant states the
*property* and the component states the *enforcement*. Neither overrides the other: the component rule is the
obligatory implementation of the suite invariant, and the suite invariant is what makes the component rule
non-negotiable.

### 1.3 Relationship to protocol invariants

Protocols declare protocol-local rules (`HTSM-01`…`HTSM-12` in the HTSM specification, `RTR-n` in the session router).
A protocol rule **realises** one or more suite invariants and never contradicts one. The mapping in §6 states which
suite invariant each HTSM rule realises.

### 1.4 Precedence

```
  kernel invariants          ← INV-n          (this document)      absolute
      │
      ├─► kernel policy       ← release policy
      │       │
      │       ├─► runtime contract
      │       ├─► capability policy
      │       ├─► task policy
      │       └─► agent request
      │
      └─► component invariants ← INV-ATA-nn, CNS-n:n, CTR-n:n, PMT-n:n, RTM-n:n
```

`CTR-1:11` gives the precedence order inside the controller; `PMT-2` gives the order the Host AI applies. This document
fixes the top of that stack. Where a lower layer would relax an invariant, the invariant wins and the request is denied,
not downgraded.

---

## 2. Terms

| Term | Meaning |
|---|---|
| Invariant | A property that holds in every conformant deployment, at every profile. |
| Kernel invariant | A suite-level invariant, numbered `INV-n` in this document. |
| Component invariant | An invariant enforced at one component's boundary, carrying that component's own ID. |
| Protocol rule | A protocol-local requirement, e.g. `HTSM-01`, `RTR-7`. |
| Enforcement point | The component at which an invariant is actually checked. |
| Realises | The relationship between a component rule and the suite invariant it implements. |
| Precedence | The total order over input classes; a lower class never relaxes a higher one. |

---

## 3. The invariant register

Forty-eight invariants in eight groups. Each entry states the invariant, where it is enforced, and which component or
protocol rules instantiate it.

### 3.1 Group A — Authority and identity (INV-1 … INV-8)

| ID | Invariant | Enforced at | Instantiated by |
|---|---|---|---|
| **INV-1** | **No implicit authority.** Authority is never inferred from presence, context, prompt content, package class, name, repository, or authentication alone. | controller, console, runtime, prompt | `CTR-1:9`, `CNS-1:9`, `RTM-1:12`, `PMT-1:14` |
| **INV-2** | **Evidence is not permission.** A signature, attestation, token, hash, attestation of presence, or a credential is evidence *for* a check, never a grant. | controller | `CTR-1:10`, `CTR-1:14` |
| **INV-3** | **Authentication is not authorization.** Proving who or what a party is never establishes what it may do. | all | `CNS-1:3`, `CNS-1:5`, `CTR-1:10`, `amk-authority` Core Intent |
| **INV-4** | **No self-minting or elevation.** No component, Host AI, task, package, adapter or external input may mint, alter, escalate or widen an attestation, capability token or pre-authorization. | controller, runtime, authority | `CTR-1:18`, `RTM-1:3`, `RTM-1:4`, `amk-authority` Core Intent |
| **INV-5** | **The console is the sole representation of the operator.** There is no separate user identity in the AMK; the console's cryptographic identity is how the operator is present. | console, authority | `CNS-1:10`, `amk-authority` Core Intent |
| **INV-6** | **Enforcement authority lies outside the console, the prompt, the runtime and the Authority.** Only the controller decides. | all | `CNS-1:11`, `PMT-1:6`, `RTM-1:5`, `amk-authority` ATA-3 |
| **INV-7** | **Long-term identity never crosses a component boundary,** and long-term private keys never leave a protected key store. | console, authority, runtime | `INV-ATA-03`, `CNS-1:13`, `RTM-1:15` |
| **INV-8** | **The Host AI and the prompt layer are untrusted** for enforcement, authorization and evidence. | controller, prompt | `CTR-1:6`, `CTR-1:7`, `PMT-1:5` |

### 3.2 Group B — Verification, integrity and evidence (INV-9 … INV-17)

| ID | Invariant | Enforced at | Instantiated by |
|---|---|---|---|
| **INV-9** | **Nothing is acted on without cryptographic verification.** | all | `CTR-1:14`, `CNS-1:6`, `CNS-1:14`, `ATA-00:02` |
| **INV-10** | **Failed verification is opaque denial.** Failure causes are not distinguished to an unauthenticated party. | authority | `INV-ATA-04`, `ATA-03:05` |
| **INV-11** | **Revocation status is checked on every verification.** Unavailable or stale revocation data is never treated as "not revoked". | authority, runtime, controller | `INV-ATA-05`, `ATA-04:01` |
| **INV-12** | **No attestation without proof of possession** of the session private key. | authority | `INV-ATA-01` |
| **INV-13** | **No attestation for a role outside the session's declared role list.** No session record, no attestation. | authority | `INV-ATA-02` |
| **INV-14** | **Attestation validity never exceeds the epoch** plus the grace window `G`. | authority | `INV-ATA-06` |
| **INV-15** | **Only keys the signed metadata lists as `active` or `retired` are accepted** for attestation signing. | authority | `INV-ATA-07` |
| **INV-16** | **Package code is never executed before** its signature, hash and contract version have been verified. | runtime | `RTM-1:13`, `RTM-5:4` |
| **INV-17** | **A failed integrity check triggers safe-halt** and is never bypassed. | controller | `CTR-1:15` |

### 3.3 Group C — Failure, determinism and precedence (INV-18 … INV-22)

| ID | Invariant | Enforced at | Instantiated by |
|---|---|---|---|
| **INV-18** ⟨HTSM anchor⟩ | **Any verification failure produces an error and no side effect.** No state changes outside the failing check's own bookkeeping. | controller, all | `HTSM-10`, `HTSM-03` |
| **INV-19** | **Fail closed.** No failure condition resolves permissively; a failed validation gate is never overridden. | all | `CTR-1:16`, `RTM-1:17`, `CNS-1:12` |
| **INV-20** | **No silent downgrade** of protocol, contract, package, capability or prompt versions. | all | `CTR-1:17`, `RTM-1:19`, `PMT-1:14` |
| **INV-21** | **Precedence is deterministic and layered.** Lower-ranked inputs never relax higher-ranked ones; a conflict that no deterministic rule resolves fails closed. | controller, prompt | `CTR-1:11`, `PMT-3:2` |
| **INV-22** | **Every governed decision is attributable to policy and state,** and is recorded in audit without exposing secrets. | controller, authority, runtime | `CTR-1:3`, `ATA-06:10`, `RTM-5:20` |

### 3.4 Group D — Scope, containment and credentials (INV-23 … INV-28)

| ID | Invariant | Enforced at | Instantiated by |
|---|---|---|---|
| **INV-23** | **A runtime holds no authority of its own** and cannot widen any authority granted to it. | runtime, controller | `RTM-1:3` |
| **INV-24** | **A runtime never receives** console keys, operator credentials, controller keys, or another runtime's state. | runtime | `RTM-1:15` |
| **INV-25** | **Runtime and package rules never weaken kernel boundaries.** | runtime, controller | `RTM-1:16`, `CTR-1:12` |
| **INV-26** ⟨HTSM anchor⟩ | **Issued credentials are credentials.** Handles, continuations and capability tokens are controller-minted, session-bound, short-lived, and single-use where the protocol states so. | controller | `HTSM-07`, `CTR-1:19` |
| **INV-27** | **A capability never proves that its preconditions still hold.** Loading, presence or a prior grant is never standing authorisation. | controller, runtime | `amk-controller` Core Intent, `RTM-1:12` |
| **INV-28** | **A pre-authorization is bound to a named scope** and never widens silently. | console, controller | `CTR-1:18`, `CNS-5:6`, `RTM-4:24` |

### 3.5 Group E — The Host AI channel (INV-29 … INV-36)

This group governs the Host AI's only path to the kernel. It is the group the HTSM protocol implements.

| ID | Invariant | Enforced at | Instantiated by |
|---|---|---|---|
| **INV-29** ⟨HTSM anchor⟩ | **HTSM is the baseline Host AI ingress to the controller.** No other Host AI method may exist at the kernel level. | controller | `HTSM-01`, `CTR-1:2`, `CTR-5:11` |
| **INV-30** ⟨HTSM anchor⟩ | **The Host AI holds no secret and computes nothing.** A request carries only a handle, a step, a continuation and declared parameters. | controller | `HTSM-02`, `PMT-7:3` |
| **INV-31** ⟨HTSM anchor⟩ | **A GET never executes.** A request node records a proposal; it causes no state change outside the session's own chain and proposal log. | controller | `HTSM-03`, `HTSM-04` |
| **INV-32** ⟨HTSM anchor⟩ | **Console-only operations are unrepresentable.** They have no node; they are not rejected at runtime, they cannot be expressed. | controller | `HTSM-04`, `CTR-4:7`, `PMT-4:4` |
| **INV-33** ⟨HTSM anchor⟩ | **Parameter values are untrusted data,** validated against the node's declared schema. Anything outside it is rejected. | controller | `HTSM-05` |
| **INV-34** ⟨HTSM anchor⟩ | **The kernel subtree is controller-owned.** No runtime may define, shadow or override a kernel node. | controller, runtime | `HTSM-06`, `CTR-5:2`, `RTM-1:16` |
| **INV-35** | **The Host AI never receives a capability token.** It learns outcomes only. | controller | `CTR-1:19`, `HTSM` Part B §12 |
| **INV-36** | **Errors and responses reveal nothing** beyond the session's own projection. | controller | `HTSM-08`, `PMT-7:5` |

### 3.6 Group F — The prompt (INV-37 … INV-40)

| ID | Invariant | Enforced at | Instantiated by |
|---|---|---|---|
| **INV-37** | **The prompt is advisory.** It is untrusted for enforcement, authorization and evidence, and cannot override or bypass the controller. | prompt | `PMT-1:5`, `PMT-1:6`, `PMT-1:7` |
| **INV-38** | **External data is never converted into authority.** Instructions embedded in documents, code, tool output or web pages do not rewrite the contract merely by being imperative. | prompt | `PMT-1:14`, `PMT-2` |
| **INV-39** | **Kernel state is never fabricated.** A model response, textual marker or confident phrasing is never an attestation. | prompt | `PMT-4:3`, `PMT-1:13` |
| **INV-40** | **The prompt holds no state and no credentials.** Conversation memory is never authoritative state. | prompt | `PMT-1:12`, `PMT-1:13` |

### 3.7 Group G — Packages and loading (INV-41 … INV-44)

| ID | Invariant | Enforced at | Instantiated by |
|---|---|---|---|
| **INV-41** | **A package is never loaded, registered or included** because of its presence, class, name or repository. | runtime, console | `RTM-1:12` |
| **INV-42** | **A package is never loaded into a session** without a console-signed inclusion decision or a covering console-signed pre-authorization. | runtime, console | `RTM-1:14`, `CNS-4:16` |
| **INV-43** | **System packages carry the same authority as any other runtime.** Class affects provenance and load order only. | runtime | `RTM-1:7` |
| **INV-44** | **The bytes executed are the bytes verified.** A verification result does not survive a substitution between check and use. | runtime | `RTM-1:13`, `RTM-5:4`, `RTM-5:6` |

### 3.8 Group H — Audit and honest reporting (INV-45 … INV-48)

| ID | Invariant | Enforced at | Instantiated by |
|---|---|---|---|
| **INV-45** | **Every governed action produces an audit record** identifying subject, decision and outcome, without secrets. | controller, authority, runtime | `CTR-1:3`, `ATA-06:10`, `RTM-5:20` |
| **INV-46** | **Audit history is never rewritten.** Revocation, correction and supersession append; they do not edit. | authority, controller | `ATA-04:02` |
| **INV-47** | **An outcome is never presented as done** until the authoritative component acknowledges it. Delivery, acceptance and completion are distinct. | console, controller | `amk-console` Core Intent, `CNS-4` |
| **INV-48** | **Unknown state is reported as unknown,** never inferred from cache, from a stale view, or from the absence of a denial. | console, prompt, controller | `amk-console` Core Intent, `PMT-1:13` |

---

## 4. The invariant that no profile may relax

Three of the five components each carry a profile-independent "non-conformance" clause — `CNS-1:16`, `CTR-1:20`,
`RTM-1:20` — and the prompt carries `PMT-1:15`. The Authority does not yet carry one.

This document generalises it. It is stated here once, and it applies to every invariant in this register:

> **INV-0 — An implementation that violates any invariant in this register is non-conformant, at every profile,
> regardless of how many other requirements it satisfies.**

`INV-0` is not numbered in the register above because it governs the register itself; it is stated here so that no
profile table needs to restate it, and so that `PROF-` rules can only ever *add* obligations.

**This closes a specific recorded defect.** `E-CTR-01`, `E-CNS-02` and `E-RTM-01` each flag the same problem: the Basic
profile accepts "soft isolation", which is defined nowhere, while `CTR-7:10`, `CNS-1:13` and `RTM-5:10` require isolation
without a profile qualifier. Under `INV-0` and `INV-19` the reading is forced: a profile that cannot state what it
relaxes has relaxed nothing. "Soft isolation" is therefore either defined in `security-requirements/` or it is not
available at any profile — it is not a licence to omit the invariant.

---

## 5. Component invariant mapping

Every component invariant, and the suite invariant it realises. This is the index that makes the five private lists
coherent.

### 5.1 `amk-authority` — `INV-ATA-01` … `INV-ATA-07`

| Component invariant | Text (abridged) | Suite invariant |
|---|---|---|
| `INV-ATA-01` | No attestation without proof-of-possession of the session private key | **INV-12** |
| `INV-ATA-02` | No attestation for a role outside the session's declared role list | **INV-13** |
| `INV-ATA-03` | Long-term identity is never revealed to session participants | **INV-7** |
| `INV-ATA-04` | Failed verification is opaque denial | **INV-10** |
| `INV-ATA-05` | Revocation status is checked on every verification | **INV-11** |
| `INV-ATA-06` | `valid_from`/`valid_to` bounded by the epoch plus `G` | **INV-14** |
| `INV-ATA-07` | Only attestations signed by an `active` or `retired` key are accepted | **INV-15** |

*Also realises:* **INV-2** (evidence is not permission), **INV-3**, **INV-6**, **INV-9**.

### 5.2 `amk-console` — `CNS-1:9` … `CNS-1:15`

| Component invariant | Text (abridged) | Suite invariant |
|---|---|---|
| `CNS-1:9` | No implicit authority; every state effect is an explicit signed envelope | **INV-1** |
| `CNS-1:10` | The console is the sole human representation | **INV-5** |
| `CNS-1:11` | Enforcement authority lies outside the console | **INV-6** |
| `CNS-1:12` | A failed validation gate is never overridden | **INV-19** |
| `CNS-1:13` | Long-term private keys never leave the console's protected key store | **INV-7** |
| `CNS-1:14` | The console never accepts an envelope whose signature or freshness checks fail | **INV-9** |
| `CNS-1:15` | All external communication occurs through defined envelopes (exception `CNS-3:21`) | **INV-9**, **INV-48** |
| `CNS-1:16` | Violating any invariant makes an implementation non-conformant | **INV-0** |

*Also realises:* **INV-3** (authentication ≠ authorization), **INV-28**, **INV-45**, **INV-47**, **INV-48**.

### 5.3 `amk-controller` — `CTR-1:14` … `CTR-1:19`

| Component invariant | Text (abridged) | Suite invariant |
|---|---|---|
| `CTR-1:14` | No action without cryptographic verification, valid tokens and explicit authorization | **INV-9**, **INV-2** |
| `CTR-1:15` | A failed integrity check triggers safe-halt and is never bypassed | **INV-17** |
| `CTR-1:16` | Fail-closed on any failure condition | **INV-19** |
| `CTR-1:17` | No silent downgrades of protocol, contract or capability versions | **INV-20** |
| `CTR-1:18` | The controller never creates or widens a pre-authorization envelope | **INV-4**, **INV-28** |
| `CTR-1:19` | The Host AI never receives a capability token | **INV-35**, **INV-26** |
| `CTR-1:20` | Violating any invariant makes an implementation non-conformant | **INV-0** |

*Also realises:* **INV-1**, **INV-18**, **INV-21**, **INV-22**, **INV-26**, **INV-29**–**INV-36**, **INV-45**, **INV-47**.

### 5.4 `amk-prompt` — `PMT-1:14` … `PMT-1:15`

`PMT-1:14` states the prompt's invariants as a prose list "as registered in the contracts". That register is this
document. The mapping is therefore exact rather than approximate:

| Prompt invariant (from `PMT-1:14`) | Suite invariant |
|---|---|
| no implicit authority | **INV-1** |
| no self-authorization | **INV-4** |
| no conversion of external data into authority | **INV-38** |
| no fabrication of kernel state | **INV-39** |
| no silent version downgrade | **INV-20** |
| *(plus the prompt-specific invariants)* | **INV-37**, **INV-40**, **INV-30**, **INV-8** |

| Component invariant | Text | Suite invariant |
|---|---|---|
| `PMT-1:15` | Violating these rules makes an implementation or rendering non-conformant | **INV-0** |

### 5.5 `amk-runtime` — `RTM-1:12` … `RTM-1:19`

| Component invariant | Text (abridged) | Suite invariant |
|---|---|---|
| `RTM-1:12` | No implicit authority; never loaded because of presence, class, name or repository | **INV-1**, **INV-41** |
| `RTM-1:13` | Package code is never executed before signature, hash and contract version are verified | **INV-16**, **INV-44** |
| `RTM-1:14` | Never loaded into a session without a console-signed inclusion decision or covering pre-authorization | **INV-42** |
| `RTM-1:15` | A runtime never receives console keys, operator credentials, controller keys or another runtime's state | **INV-24**, **INV-7** |
| `RTM-1:16` | A runtime MUST NOT weaken or alter kernel boundaries | **INV-25**, **INV-34** |
| `RTM-1:17` | Any verification failure fails closed and is audited | **INV-19**, **INV-45** |
| `RTM-1:18` | No runtime provides an external user interface | **INV-25** |
| `RTM-1:19` | No silent downgrades of contract, package or runtime versions | **INV-20** |
| `RTM-1:20` | Violating any invariant makes an implementation non-conformant | **INV-0** |

*Also realises:* **INV-4**, **INV-23**, **INV-26**, **INV-43**, **INV-45**.

---

## 6. Protocol invariant mapping — HTSM

The HTSM protocol's twelve rules realise suite invariants. The eight marked ⟨HTSM anchor⟩ are the IDs HTSM cites
directly in its rule table.

| HTSM rule | Statement (abridged) | Suite invariant | Anchor |
|---|---|---|---|
| `HTSM-01` | HTSM is the baseline Host AI ingress; no other Host AI method at kernel level | **INV-29** | ⟨HTSM anchor⟩ |
| `HTSM-02` | The AI holds no secret and computes nothing | **INV-30** | ⟨HTSM anchor⟩ |
| `HTSM-03` | A GET never executes | **INV-31**, **INV-18** | ⟨HTSM anchor⟩ |
| `HTSM-04` | Console-only operations have no node; they are unrepresentable | **INV-32** | ⟨HTSM anchor⟩ |
| `HTSM-05` | Parameter values are untrusted, validated against the node's declared schema | **INV-33** | ⟨HTSM anchor⟩ |
| `HTSM-06` | The kernel subtree is controller-owned; no runtime may shadow a kernel node | **INV-34** | ⟨HTSM anchor⟩ |
| `HTSM-07` | Handles and continuations are credentials: controller-minted, session-bound, short-lived, single-use | **INV-26** | ⟨HTSM anchor⟩ |
| `HTSM-08` | Errors and responses reveal nothing beyond the session's own projection | **INV-36** | — |
| `HTSM-09` | The AI changes session state only by advancing the chain | **INV-1** | — |
| `HTSM-10` | Any verification failure produces an error and no side effect | **INV-18** | ⟨HTSM anchor⟩ |
| `HTSM-11` | While the deployment is not READY, every request receives only `unavailable` | **INV-19** | — |
| `HTSM-12` | The operational level is set by the operator; the AI cannot claim or raise it | **INV-1** | — |

**Anchor reconciliation.** HTSM's rule table cites `INV-18` for `HTSM-10`, `INV-26` for `HTSM-07`, and `INV-29` through
`INV-34` for `HTSM-01` through `HTSM-06`. Each citation now resolves to the statement above with matching meaning. No
citation is reinterpreted or renumbered; this register was written to fit the citations that already existed.

**Two protocol-level observations that are not invariants.** HTSM's `HTSM-03` note distinguishes *effect-safety* from
RFC 9110 *safety*, and its §9.6 replay rule distinguishes an identical repeat from a fork. Both are protocol mechanics
that realise **INV-18** and **INV-26**; they are not separate invariants and no additional number is reserved for them.

---

## 7. Suite-level gaps this register does not close

The register fixes the *vocabulary*. It does not fix the suite-level gaps that caused the components to drift. Each of the
following is currently duplicated in two component registers with no owner, and is recorded in the cross-component
alignment report. **None of them is an invariant** — each is a missing contract.

| Gap | Duplicated in | Natural home in this area |
|---|---|---|
| Session state machine (states, transitions, pre/post conditions) | `E-CTR-12`, `E-PMT-07`, HTSM §2 | `operating-levels/` + `canonical-data-model/` |
| Error and outcome taxonomy (rejection vs acceptance vs completion vs uncertainty) | `E-CTR-14`, `E-PMT-06`, `RTM-O-12` | `canonical-data-model/` |
| Console-only operation list (one list, cited by all five) | `E-PMT-10` | `authority-model/` |
| Envelope catalogue with field-level schemas | `CNS-O-01`, `CTR-O-01`, `RTM-O-13` | `canonical-data-model/` |
| Freshness, skew, nonce lifetime, sequence storage | `CNS-O-04`, `CTR-O-08` | `time-and-freshness/` |
| Audit entry format, retention, checkpoint protection | `E-CTR-10`, `E-CNS-10`, `E-RTM-08` | `canonical-data-model/` + `security-requirements/` |
| Defaults location and the `DV-` naming authority | `E-RTM-05`, `E-RTM-12`, HTSM Part A §8 | `canonical-data-model/` |
| Cross-spec open-item register (`CORE-O-nn`) | cited by 3 components; the file does not exist | `scope/` (as the suite's register index) |
| Recovery protocol after key loss or compromise | `CNS-O-03`, `CTR-O-07` | `cryptographic-lifecycle/` |
| Key distribution and rotation across components | `O-ATA-11`, `CNS-O-13`, `CTR-O-16` | `trust-bootstrap/` + `cryptographic-lifecycle/` |
| Conformance rule set ("protocol standards") | `RTM-O-08` | `security-requirements/` |
| Suite versioning and reference-ID alignment | `RTM-O-19`, `CORE-O-03`, `CORE-O-05` | `scope/` |

---

## 8. Conformance

- **INV-0** applies to every entry in this register, at every profile.
- An implementation declares the suite version it implements and, separately, its operational profile.
- A conformance claim MUST evidence each invariant it relies on. The evidence classes are: a normative rule in the
  implementation, a conformance test, or a published reason why the invariant is vacuous in that deployment.
- **An invariant that is vacuous in a deployment MUST be reported as vacuous,** not as satisfied. This distinguishes a
  deployment that genuinely has no runtime from one that failed to implement the rule.
- The profile tables of the five components MAY add obligations. They MUST NOT contain any clause that relaxes an
  invariant; a clause that appears to do so is interpreted as adding a requirement, and the ambiguity is reported
  against `scope/`.

---

## 9. Open items

| ID | Item | Depends on |
|---|---|---|
| **K-INV-O-01** | Whether `INV-0` should be numbered in the register or stated as a preamble rule. Stated as a preamble here; numbering it would leave `INV-1` as the first invariant and forbid a future `INV-0` use for a different purpose. | — |
| **K-INV-O-02** | The suite-level `INV-n` namespace versus the component-local `INV-ATA-nn`. This register adopts the suite namespace `INV-n` because HTSM already cites it. The Authority's `INV-ATA-nn` is retained unchanged for ID stability. | `D-HTSM-04` in the HTSM completion plan |
| **K-INV-O-03** | Whether `INV-44` (bytes executed are bytes verified) is a runtime-only invariant or a suite invariant. It is placed here as suite-level because a time-of-check/time-of-use gap is a kernel concern wherever it arises, but no non-runtime component currently realises it. | `E-RTM-06` |
| **K-INV-O-04** | Whether the Authority should adopt an `INV-0`-equivalent non-conformance clause. It is the only component without one. | — |
| **K-INV-O-05** | The mapping of the session-router's `INV-1`…`INV-12` into this register. The session router is a companion, not a core component, and its invariants are scoped to the router instance; they are currently left unmapped rather than renumbered. | — |
| **K-INV-O-06** | Whether `CNS-1:15`'s exception (`CNS-3:21`, handoff delivery) implies an exception to **INV-9** for the Host AI channel, given that HTSM does not use envelopes. | `D-HTSM-06` in the HTSM completion plan |

---

## 10. Cross-references

| Area | What it holds | Relationship to this register |
|---|---|---|
| `scope/` | What the kernel is and is not | Defines the boundary of every invariant here |
| `principles/` | The design principles the invariants realise | The "why" behind the register |
| `trust-model/` | Who trusts whom, and for what | Defines the enforcement points named in §3 |
| `authority-model/` | How authority flows and who may not widen it | Defines **INV-1**, **INV-4**, **INV-28** |
| `trust-bootstrap/` | How trust anchors are established | Defines the preconditions for **INV-9**, **INV-15** |
| `cryptographic-lifecycle/` | Key lifecycle, rotation, recovery | Defines the enforcement of **INV-7**, **INV-14**, **INV-15** |
| `time-and-freshness/` | Skew, nonce lifetime, TTL, sequence storage | Defines the enforcement of **INV-14**, **INV-26** |
| `operating-levels/` | Basic/Standard/High and L0/L1/L2 | Defines what a profile may add under **INV-0** |
| `canonical-data-model/` | Envelopes, states, errors, audit records | Defines the shapes behind **INV-22**, **INV-45**, **INV-47** |
| `security-requirements/` | Suite-wide security obligations | Restates and extends this register |
| `threat-model/` | Threats and the invariants that answer them | The justification for each group |
| `architecture/` | How the components fit | Realises the enforcement points in §3 |
| `backend-extension-model/` | How third parties extend without weakening | Defines the boundary of **INV-23**, **INV-25** |

---

*Suite version `OS10-AMK 2.x` · kernel invariant register `1.0-draft` · draft for review*
