# AMK HTSM Protocol — Phase / Stage Completion Plan

**Protocol:** HTSM — Host Transversal Surface Map
**Scope of this plan:** `specifications/protocols/htsm/` in `inscope-labs/amk-dev`
**Plan status:** Draft for operator review
**Plan version:** 1.0-draft
**Protocol baseline:** HTSM/1, spec version `1.0.1-draft` (`HTSM-PART-A.md:5`)
**Baseline commit analysed:** `1463d89` ("docs: update HTSM protocol specification")

---

## 1. Purpose

This plan turns the HTSM protocol from its current state — two substantive documents and thirty-seven placeholders — into a
closed, self-contained, independently implementable and testable protocol specification.

It is organised as **phases** (delivery milestones) and **stages** (the unit of work inside a phase). Every stage has a
named owner area, named inputs, named output files, and an exit criterion that can be checked without judgement.

The plan is deliberately written so that the work can be **split across several people or several sessions**. Phases 2, 3
and 6 have no dependencies on each other; only Phases 0, 1, 4, 5 and 7 sit on the critical path.

### 1.1 How to read this document

| Section | What it gives you |
|---|---|
| §2 | The verified baseline — what exists today, with counts and citations |
| §3 | The scope of HTSM, restated from the protocol itself |
| §4 | What "complete" means here — the completion criteria |
| §5 | The conflict register — 14 concrete contradictions with file-and-line evidence |
| §6 | The decision register — 10 questions only the operator can answer |
| §7 | The phase and gate overview |
| §8 | Phase detail, stage by stage |
| §9 | The conformance test map and the coverage gaps it exposes |
| §10 | The traceability matrix |
| §11 | The risk register |
| §12 | The critical path and a resourcing view |
| §13 | The file-by-file completion manifest |
| §14 | The definition of done |

Every factual claim in §2 is a measurement taken against the baseline commit, not an inference.

---

## 2. Verified baseline

### 2.1 File inventory

`specifications/protocols/htsm/` contains **39 files**.

| State | Count | Files |
|---|---|---|
| Substantive specification text | 2 | `specification/HTSM-PART-A.md` (19,934 B), `specification/HTSM-PART-B.md` (11,003 B) |
| Markdown placeholders (`TODO`, 5 bytes) | 31 | `README.md`, `CHANGELOG.md`, `LICENSE`, 3 × `specification/*.md`, 9 × `contracts/*/README.md`, 5 × `fixtures/*/README.md`, 5 × `mocks/*/README.md`, 6 × `tests/*/README.md` |
| JSON Schema placeholders (`description: "TODO"`, ~120 B) | 6 | `schemas/{node,handoff,request,response,proposal,error}.schema.json` |

**31 + 6 = 37 of 39 files (94.9%) are placeholders.**

### 2.2 What the two substantive documents actually contain

| Element | Count | Status |
|---|---|---|
| Protocol rules `HTSM-01` … `HTSM-12` | 12 | Defined, but with no per-rule ID scheme |
| Verification checks (§10.5) | 16 | Defined, in order, with the first-failure-wins rule |
| Kernel commands | 9 | Defined in Part A §8.2 |
| Node types | 5 | Defined in Part A §7.1 |
| Parameter types | 8 | Defined in Part A §9.1 |
| Error codes | 17 | Defined in Part B §15.2 |
| Result states | 7 | Defined in Part A §9.5 |
| Operational levels | 2 | Defined in Part B §14 |
| Conformance tests `HTSM-T01` … `HTSM-T24` | 24 | Named only — one-line descriptions, no preconditions, steps or expected results |
| Default value IDs `DV-…` | **26 referenced, 0 defined** | See §2.3 |
| Suite invariant IDs `INV-…` | **8 referenced, 0 defined** | See §2.4 |
| Sections | §1 … §19 + Appendix A | §11 is duplicated across both files; Part A has no Markdown heading structure |

### 2.3 Undefined default values (26)

Every default is referenced by the protocol and defined nowhere. Part A §8 states the rule explicitly: *"No default value
is stated in this guide; each is a DV- ID defined only in the defaults manifest."* The defaults manifest does not exist.

| # | ID | Referenced at | Category |
|---|---|---|---|
| 1 | `DV-HTSM-RESPONSE-MAX-BYTES` | Part A §5 | Size limit |
| 2 | `DV-HTSM-URL-MAX-BYTES` | Part A §5 | Size limit |
| 3 | `DV-HTSM-HANDLE-ENTROPY-BITS` | Part A §7 | Entropy |
| 4 | `DV-HTSM-INDEX-MAX-CHILDREN` | Part A §7.1 | Size limit |
| 5 | `DV-HTSM-NODE-CONTENT-MAX-BYTES` | Part A §7 | Size limit |
| 6 | `DV-HTSM-DEPTH-MAX` | Part A §7.1 | Size limit |
| 7 | `DV-HTSM-PARAMS-MAX-COUNT` | Part A §9.1 | Size limit |
| 8 | `DV-HTSM-PARAM-TEXT-MAX-LEN` | Part A §9.1 | Size limit |
| 9 | `DV-PROPOSAL-PENDING-TTL-S` | Part A §9.5 | Lifetime |
| 10 | `DV-PROPOSAL-RESULT-RETENTION-S` | Part A §9.5 | Retention |
| 11 | `DV-PROPOSAL-MAX-PENDING` | Part A §9.5 | Count |
| 12 | `DV-HTSM-TOKEN-ENTROPY-BITS` | Part A §10.3 | Entropy |
| 13 | `DV-HTSM-TOKEN-TTL-S` | Part A §10.3 | Lifetime |
| 14 | `DV-HTSM-PROBE-RATE-PER-MIN` | Part A §10.5 check 5 | Rate limit |
| 15 | `DV-HTSM-FP-ALERT-PER-HOUR` | Part A §10.5 | Rate limit |
| 16 | `DV-HTSM-BAD-REQUEST-ATTEMPTS` | Part A §9.6, §11.1 | Count |
| 17 | `DV-SESSION-HANDOFF-TTL-S` | Part A §10.2 | Lifetime |
| 18 | `DV-HTSM-CHECKPOINT-STEPS` | Part A §11.1 | Count |
| 19 | `DV-HTSM-TURN-WINDOW-S` | Part B §11.4 | Window |
| 20 | `DV-HTSM-DEGRADED-EXTENSION-STEPS` | Part B §11.4 | Count |
| 21 | `DV-HTSM-LEVEL-DEFAULT` | Part B §14 | Level |
| 22 | `DV-SUBTREE-MAX-NODES` | Part A §8.1, Part B §13 | Size limit |
| 23 | `DV-SUBTREE-MAX-BYTES` | Part A §8.1, Part B §13 | Size limit |
| 24 | `DV-SUBTREE-REVISIONS-MAX` | Part B §13 | Count |
| 25 | `DV-CAP-TOKEN-TTL-S` | Part B §12 | Lifetime |
| 26 | `DV-CAP-TOKEN-DESTRUCTIVE-TTL-S` | Part B §12 | Lifetime |

### 2.4 Undefined invariants (8)

`HTSM-PART-A.md` cites eight suite-level invariant IDs as the "Contracts invariant" column of its rule table. **None is
defined anywhere in the repository.** The repository's existing invariant namespaces are `INV-ATA-01` … `INV-ATA-07`
(authority) and `INV-1` … `INV-12` (session router). There is no suite-level `INV-n` list.

| Cited | Cited for | Where |
|---|---|---|
| `INV-18` | HTSM-10 (verification failure has no side effect) | Part A §3 |
| `INV-26` | HTSM-07 (handles and continuations are credentials) | Part A §3 |
| `INV-29` | HTSM-01 (HTSM is the baseline Host AI ingress) | Part A §3 |
| `INV-30` | HTSM-02 (the AI holds no secret and computes nothing) | Part A §3 |
| `INV-31` | HTSM-03 (a GET never executes) | Part A §3 |
| `INV-32` | HTSM-04 (console-only operations are unrepresentable) | Part A §3 |
| `INV-33` | HTSM-05 (parameters are untrusted and validated) | Part A §3 |
| `INV-34` | HTSM-06 (the kernel subtree is controller-owned) | Part A §3 |

### 2.5 Missing companion documents

The protocol depends on four documents that do not exist in this repository:

| Referenced as | Required by | Expected home | Status |
|---|---|---|---|
| `os10-lifecycle.md` (all state machines, session states in §6) | Part A §2, §6 | `contracts/lifecycle/` | Placeholder |
| `amk-defaults-manifest.md` (every `DV-` value) | Part A §8, Part B §12, §13, §14 | `contracts/defaults/` | Placeholder |
| `amk-contracts §6` (signed console envelopes) | Part A §1 | `specifications/contracts/` | Placeholder (`specifications/contracts/README.md` is `TODO`) |
| Runtime registration protocol | Part A §4 | `contracts/runtime-registration/` | Placeholder |

### 2.6 The published-public constraint

`public/assets/aistudio/htsm-protocol-part-a_bf2f3c55.md` and `…part-b_6ce4ffdc.md` are byte-identical to the two
specification files. Part A and Part B are therefore **published assets**, not private drafts. Any edit to them changes a
published artefact. This is recorded as decision `D-HTSM-09` and risk `R-01`.

---

## 3. Protocol scope, restated from the protocol

HTSM defines the **control plane** between a Host AI and the controller. Part A §1 is the authority on this.

**In scope:** the Host AI → controller channel; addressing; parameters; authentication (the traversal chain); console
turns as seen by the AI; the surface structure; errors.

**Out of scope:** console → controller communication (signed envelopes); runtime → controller registration; capability
token use by adapters; the content of runtime subtrees; the dispatch and completion of effects (§12).

**Non-goals:** proving that the AI is who it claims to be; confidentiality beyond TLS; authorizing effects. An HTSM
request is a **proposal**, never an effect.

**Layer separation:** the effect path is deliberately external to HTSM and may be hosted on a different platform from the
ingress.

**Two consequences for this plan.**

1. The eight `contracts/` subdirectories are **not** eight independent documents. They are the boundary documents that
   HTSM points *into*. Each must state its interface with HTSM and its own owner, not re-derive HTSM.
2. The `mocks/` subdirectory is the seam where the layer separation becomes testable: the effect dispatcher is mocked
   precisely because it is outside the protocol.

---

## 4. What "complete" means here

The protocol is complete when **all** of the following hold. These are the acceptance criteria for this plan.

| # | Criterion | Verified by |
|---|---|---|
| **C-1** | Zero placeholder files remain anywhere under `specifications/protocols/htsm/` | Grep for `TODO`; the repository's documented rule is that a strict release build fails on any placeholder |
| **C-2** | All 26 `DV-` IDs are defined with a value, in exactly one place | `contracts/defaults/` |
| **C-3** | All 8 cited `INV-` IDs resolve to a published invariant, or the citations are remapped | §2.4 resolution, tracked in `99-open-items.md` |
| **C-4** | Every one of the 12 protocol rules is restated as a numbered normative requirement with a stable ID | Phase 1 |
| **C-5** | Every one of the 24 conformance tests is specified with preconditions, steps, expected results and observable evidence | Phase 5 |
| **C-6** | All 6 JSON Schemas validate and are exercised by at least one fixture | Phase 4, Phase 6 |
| **C-7** | Every companion contract states its interface to HTSM and closes every referenced default | Phase 3 |
| **C-8** | The session state machine exists and every state named in Part A §2 has entry conditions, exit conditions and legal transitions | Phase 3, stage S3.1 |
| **C-9** | No mandatory contradiction with any of the five core component specifications | Phase 7, stage S7.1 |
| **C-10** | The README, CHANGELOG and LICENSE are published and the CHANGELOG records the delta from `1.0.1-draft` | Phase 7, stage S7.3 |
| **C-11** | The five mock roles are runnable enough to execute the conformance suite end to end | Phase 6 |
| **C-12** | A reader who has never seen the AMK can implement HTSM from the protocol text alone, without inventing normative behaviour | Phase 7, stage S7.5 |

---

## 5. Conflict register

Fourteen concrete contradictions, each with evidence. **C-01 to C-04 are blocking** — they must be resolved before the
normative spine is written, because every rule ID, node name and error code depends on them.

| ID | Conflict | Evidence | Severity | Owner |
|---|---|---|---|---|
| **C-01** | **Vocabulary: "proposal" vs "work order".** HTSM uses *proposal* throughout (`kernel/proposals`, "pending proposals", `DV-PROPOSAL-*`). The prompt, console and controller use *work order*. The prompt requires the Host AI to handle a `work_order_required` error code that is **not in the HTSM error table**. | `HTSM-PART-A.md:133,182` vs `amk-prompt/06-htsm-conduct.md:20`, `amk-prompt/03-command-set.md:12`, `amk-controller/04-surface-runtime.md:17` | **Blocking** | `D-HTSM-01` |
| **C-02** | **`kernel/verify` vs `kernel/validate`.** HTSM defines `kernel/verify` as a *request* node with a `target` enum (Part A §8.2). The prompt defines `kernel/validate` as an *index* node of the optional session-validation layer, and `PMT-7:13` describes the Host AI visiting designated nodes and relaying a challenge key. These are different mechanisms under similar names. | `HTSM-PART-A.md:131` vs `amk-prompt/03-command-set.md:12`, `amk-prompt/06-htsm-conduct.md:32` | **Blocking** | `D-HTSM-02` |
| **C-03** | **Missing session state machine.** HTSM names ten session states and says they are defined in `os10-lifecycle.md §6`. That document does not exist. `DEGRADED` is also a *controller health* state (`CTR-2:8`), so the same word denotes two things. | `HTSM-PART-A.md:33` vs `amk-controller/01-bootstrap-state.md:21` | **Blocking** | `D-HTSM-03` |
| **C-04** | **Dangling invariant citations.** Eight `INV-` IDs are cited with no definitions anywhere in the repository. | `HTSM-PART-A.md:46-55`; repository has only `INV-ATA-*` and `INV-1..12` | **Blocking** | `D-HTSM-04` |
| **C-05** | **Registry version required but not exposed.** §18 states the node-type registry "has its own version, reported in `kernel/identity`". The `kernel/identity` node description does not list it. | `HTSM-PART-B.md:107` vs `HTSM-PART-A.md:125` | High | `D-HTSM-08` |
| **C-06** | **Handoff artefact shape.** HTSM §10.2 defines a text form: `htsm=1 base={base} surface={surface_version} root={handle} continuation=G.{token}`. The controller returns a signed `Handoff` envelope whose genesis anchor binds the approved surface. Are these the same credential in two renderings, or two artefacts? | `HTSM-PART-A.md:211` vs `amk-controller/04-surface-runtime.md:21-22`, `amk-console/03-session-lifecycle.md:15,21` | High | `D-HTSM-06` |
| **C-07** | **Fingerprint vs session identity.** HTSM says the transport fingerprint is optional, may be absent, and never blocks a request. The controller defines session identity as "the Host AI client fingerprint set at first use plus the delivery gateway". On a platform with no fingerprint, session identity is empty, which undercuts `CTR-5:13` ("a different identity is a fork"). | `HTSM-PART-A.md:243` vs `amk-controller/04-surface-runtime.md:30` | High | `D-HTSM-07` |
| **C-08** | **Surface revision vs re-handoff.** HTSM §13 says subtree revisions "do not change the surface version, so the chain continues without a re-handoff". The controller says inclusion/removal produces "a new surface revision" and a kernel-subtree change "requires a new session start". The mechanism differs: re-handoff vs new session start. | `HTSM-PART-B.md:38-39` vs `amk-controller/04-surface-runtime.md:23` | High | `D-HTSM-06` |
| **C-09** | **`kernel/runtimes` vs the `runtime` command.** Part A §8.2 names the node `kernel/runtimes` but the command set is `runtime` (singular). `PMT-4:1` lists the command as `runtime`. | `HTSM-PART-A.md:129` vs `amk-prompt/03-command-set.md:11` | Medium | `D-HTSM-01` |
| **C-10** | **§11 is duplicated across Part A and Part B.** Part A §11.1–§11.2 and Part B §11.1–§11.6 define the same console-turn section, with Part B's version being the superset. A reader of Part A alone gets an incomplete turn model. | `HTSM-PART-A.md:261-273` vs `HTSM-PART-B.md:3-25` | Medium | `D-HTSM-09` |
| **C-11** | **Undefined error vocabulary on the console side.** `E-PMT-06` records that the Host AI must surface `E_UNREACHABLE`, `fork_detected`, `chain_broken`, `work_order_required` and the state `UNVERIFIED`, and that "the controller spec defines no matching codes or state (`E-CTR-14`)". HTSM defines `fork_detected` and `chain_broken` but not the other three. | `amk-prompt/amk-prompt.md:88`, `amk-controller/amk-controller.md:117` | High | `D-HTSM-01`, `D-HTSM-03` |
| **C-12** | **Runtime subtree provenance is an open item the controller defers.** `CTR-5:3` says provenance binding "remains an open item; until defined, the controller treats a successfully validated subtree as authoritative". HTSM §13 requires the provider to sign the subtree and the operator to authorise per session — which appears to close it. | `amk-controller/04-surface-runtime.md:13` vs `HTSM-PART-B.md:35-37` | Medium | `D-HTSM-10` |
| **C-13** | **Part A has no heading structure.** Part A is plain text with numbered lines and one `# Check Failure` table header; Part B uses full Markdown headings. The two halves of one protocol are formatted differently, and Part A's section anchors cannot be linked. | `HTSM-PART-A.md` (1 heading) vs `HTSM-PART-B.md` (19 headings) | Medium | `D-HTSM-09` |
| **C-14** | **The `contracts/` tree duplicates and conflicts with `specifications/contracts/`.** Part A §1 cites `amk-contracts §6`, but `specifications/contracts/README.md` is a placeholder and the `specifications/contracts/` directory contains nothing else. Meanwhile `specifications/protocols/htsm/contracts/` holds nine stub directories that appear to be the intended home. | `HTSM-PART-A.md:19` vs `specifications/contracts/README.md` | Medium | `D-HTSM-10` |

---

## 6. Decision register

Ten questions that only the operator can answer. Each one is a hard blocker for the stages named against it.

| ID | Question | Why it blocks | Blocks | Recommendation |
|---|---|---|---|---|
| **D-HTSM-01** | Is the canonical term **proposal** or **work order**? And what is the error code for "no covering work order/proposal" — `work_order_required` (new) or an existing code? | Determines every rule ID, the node name `kernel/proposals` vs `kernel/work-orders`, the `DV-PROPOSAL-*` default family, and the error table. | S1.1, S1.2, S4.1 | Adopt **proposal** as the protocol term (it is used 30+ times in the normative text and `work_order_required` appears in exactly one prompt rule), and add `work_order_required` as an alias **only** if the console-side pre-authorization model is genuinely a separate concept. Splitting "proposal" (HTSM control-plane record) from "work order" (console-issued grant) is also defensible and arguably more accurate. |
| **D-HTSM-02** | Is the optional session-validation layer the `kernel/verify` request node, a `kernel/validate` index, or a separate mechanism outside HTSM? | Determines the registry, the node types and the prompt's `PMT-7:13` behaviour. | S1.1, S4.1, S7.1 | Keep `kernel/verify` as the HTSM request node and make `kernel/validate` an **index node that points at verify nodes**, reconciling both texts rather than picking one. |
| **D-HTSM-03** | Where does the session state machine live, and is `DEGRADED` (session) the same word as `DEGRADED` (controller health)? | Ten states are referenced by name in Part A §2 and Part B §11.4 without definitions. | S3.1, S1.2 | Put it in `contracts/lifecycle/`. Disambiguate by renaming the session state to `DEGRADED_READ_ONLY` (the error code `degraded_read_only` already exists at Part A §10.5 check 7) or by qualifying the controller's health state as `health-DEGRADED`. |
| **D-HTSM-04** | Publish a suite-level `INV-` list, or remap the eight HTSM citations onto existing per-component invariant namespaces? | The citations are currently unresolvable, which fails the release gate "no obsolete normative reference". | S1.1, S7.1 | Remap onto the component namespaces that already exist (`INV-ATA-*` for attestation, session-router `INV-1..12` for the routing invariants) and add a small HTSM-local invariant set with a distinct prefix, e.g. `INV-HTSM-01…`. Publishing a new suite-level list is a framework-wide change and out of HTSM's scope. |
| **D-HTSM-05** | Do `DV-` values vary by profile (Basic / Standard / High), and does the defaults manifest live in `contracts/defaults/` or in the protocol's own index document? | 26 values must be written; the location decision affects every later phase. Part A §9 requires the resolved values to also be published machine-readably. | S2.2, S2.3 | Values vary by profile where the risk differs (TTLs, budgets, retention) and are single-valued where they are correctness-critical (entropy bits, parameter type constraints). Put the normative block in `contracts/defaults/` and mirror it in the protocol index. |
| **D-HTSM-06** | Is the HTSM handoff text form a rendering of the controller's signed `Handoff` envelope, or a second credential? And is a surface revision a re-handoff or a new session start? | Two artefact-shape questions that otherwise force implementers to invent behaviour. | S1.2, S3.4, S7.1 | Declare the text form to be a **canonical serialisation of the signed `Handoff` envelope** — one credential, two renderings — and adopt "re-handoff bumps the generation" (HTSM §10.7) as the single mechanism the controller's "new session start" must resolve to. |
| **D-HTSM-07** | What is session identity when the platform exposes no transport fingerprint? | `CTR-5:14` makes identity fingerprint-based; HTSM says the fingerprint may be absent and never blocks. | S3.6, S7.1 | Define identity as the **delivery gateway identifier** (always present, since HTSM §5 requires a gateway) **plus** the fingerprint when available. Record the absence explicitly rather than treating it as a change. |
| **D-HTSM-08** | Does `kernel/identity` carry the registry version, or is the §18 requirement dropped? | A one-line internal inconsistency in the normative text. | S4.1 | Add `registry_version` to the `kernel/identity` content. §18 is the versioning rule; the node table is the incomplete side. |
| **D-HTSM-09** | Are Part A / Part B retained as the normative spine with module files added alongside, or replaced by module files with a regenerated consolidated document? | Both files are published public assets (`public/assets/aistudio/`), so the choice changes externally visible artefacts. | S1.1, S1.2, S7.3 | Restructure into the repository's module-file convention (`00-`…`99-`) **and** regenerate `HTSM-PART-A.md` / `HTSM-PART-B.md` as build outputs from those modules — the same pattern the five core components use, where `amk-<name>.md` is the index and the module files carry the requirements. This keeps the published assets alive without keeping them as the source of truth. |
| **D-HTSM-10** | Where do the console-envelope contract (`amk-contracts §6`) and the runtime-registration contract live, and which licence covers the protocol text? | `specifications/contracts/` and `htsm/contracts/` currently overlap; `htsm/LICENSE` is a placeholder and framework decision D-04 (licence) is still open. | S3.4, S3.8, S7.3 | Keep `htsm/contracts/` as the **HTSM boundary view** — each file states only the interface HTSM depends on and points outward — and treat `specifications/contracts/` as the authoritative home for the envelopes themselves. Licence: block on framework D-04 and record the protocol as "all rights reserved pending D-04" until it resolves. |

---

## 7. Phase and gate overview

| Phase | Name | Stages | Gate | Depends on |
|---|---|---|---|---|
| **P0** | Baseline and decisions | S0.1 – S0.4 | **G0** — baseline accepted, blocking decisions answered | — |
| **P1** | Normative spine | S1.1 – S1.5 | **G1** — every rule numbered, no dangling citation | P0 |
| **P2** | Defaults manifest | S2.1 – S2.4 | **G2** — 26/26 `DV-` IDs resolved | P1 (structure), P0 (D-05) |
| **P3** | Companion contracts | S3.1 – S3.8 | **G3** — every contract closed, state machine exists | P1, P0 |
| **P4** | Registry and schemas | S4.1 – S4.8 | **G4** — registry published, 6 schemas validate | P1, P3 |
| **P5** | Conformance and security profile | S5.1 – S5.4 | **G5** — 24+ tests specified, profile defined | P1, P2, P3, P4 |
| **P6** | Fixtures and mocks | S6.1 – S6.10 | **G6** — suite executes end to end | P4, P5 |
| **P7** | Integration, reconciliation, release | S7.1 – S7.5 | **G7** — no mandatory contradiction; release-ready | P2…P6 |

```
P0 ──► P1 ──┬──► P2 ──┐
            ├──► P3 ──┼──► P4 ──┬──► P5 ──► P6 ──┐
            │          │        │              │    │
            └──────────┴────────┴──────────────┴────┴──► P7
```

---

## 8. Phase detail

### Phase 0 — Baseline and decisions

**Purpose:** turn "we know it is incomplete" into "we know exactly what is missing and who must decide what". No normative
text is written in this phase.

#### S0.1 — Verify the file inventory
- **Output:** this plan's §2, re-verified against the baseline commit.
- **Tasks:** recount files by state; record the 39-file manifest as the baseline for stage S7.4's placeholder check.
- **Exit:** the manifest in §13 matches the repository exactly.

#### S0.2 — Build the conflict register
- **Output:** §5 of this plan, with every row carrying file-and-line evidence.
- **Tasks:** diff the protocol vocabulary against all five core components and the session router; enumerate every `INV-`, `DV-`, `HTSM-`, `PMT-`, `CTR-`, `CNS-` cross-reference; mark each as resolvable, contradictory or dangling.
- **Exit:** 14 conflicts recorded; every dangling reference has an owner.

#### S0.3 — Resolve the blocking decisions
- **Output:** answered entries for `D-HTSM-01` … `D-HTSM-04`, `D-HTSM-06` … `D-HTSM-10`.
- **Tasks:** brief the operator with the evidence for each; record the answer in the decision register with a date and rationale; where the answer is "deferred", record the interim rule that lets the spine proceed and mark the dependent open item as blocking.
- **Exit:** G0. **No stage in P1 may start while `D-HTSM-01` or `D-HTSM-02` is unanswered.**

#### S0.4 — Freeze the target structure and naming
- **Output:** the target file tree and the ID scheme.
- **Tasks:** agree the module-file split for HTSM (proposed below); agree the rule-ID scheme (`HTSM-<file>:<rule>` to match `PMT-4:1`, `CTR-5:1`, `CNS-4:1`, `RTR-7`, `ATA-…`); agree the open-item ID prefix (`HTSM-O-nn`) and the review-finding prefix (`E-HTSM-nn`).
- **Exit:** the structure in §13 is approved; stage S1.1 can start without further design work.

**Proposed HTSM module split** (subject to S0.4):

| File | Contents | Source |
|---|---|---|
| `00-foundations.md` | Scope, terms, normative language, principles, the 12 rules as numbered requirements | Part A §1–§3 |
| `01-transport-addressing.md` | Transport, addressing, reserved parameters, encoding rules | Part A §5–§6 |
| `02-surface-model.md` | Node model, node types, kernel subtree, runtime subtrees, projection | Part A §7–§8 |
| `03-parameters-requests.md` | Parameter schema, strictness, templates, semantics, result states, retries | Part A §9 |
| `04-chain-authentication.md` | Genesis, handoff, continuations, chain state, verification order, forks, generations, release changes | Part A §10 |
| `05-console-turns.md` | Triggers, flow, polling, missed windows, expired tokens, operator authorisation | Part A §11.1–§11.2 + Part B §11.1–§11.6 |
| `06-effects.md` | Proposal → effect boundary, capability issuance | Part B §12 |
| `07-subtrees.md` | Attachment, revision, size caps, operator authorisation | Part B §13 |
| `08-levels.md` | Operational levels | Part B §14 |
| `09-responses-errors.md` | Success and error responses, the code table | Part B §15 |
| `10-audit-alerts.md` | Audit record, console alerts | Part B §16 |
| `11-security.md` | Security considerations | Part B §17 |
| `12-versioning.md` | Versioning and extensibility | Part B §18 |
| `13-conformance.md` | The 24 tests, specified | Part B §19, expanded in P5 |
| `99-appendices.md` | Worked example, glossary, change log | Part B Appendix A |
| `htsm.md` | Index and informative overview, mirroring `amk-authority.md` | new |

### Phase 1 — Normative spine

#### S1.1 — Modules `00`–`04`
- **Files:** `00-foundations.md` … `04-chain-authentication.md`.
- **Tasks:** restate each of the 12 rules as a numbered requirement with an ID, keeping the rule text intact; apply the §10.5 verification order as a numbered list so the first-failure-wins property is a single readable artefact; convert Part A to Markdown headings; resolve or carry forward each `INV-` citation per `D-HTSM-04`.
- **Exit:** every sentence in Part A §§1–10 is either a numbered requirement, a table row referenced by one, or explicitly marked informative.

#### S1.2 — Modules `05`–`13`
- **Files:** `05-console-turns.md` … `13-conformance.md`.
- **Tasks:** merge the duplicated §11 into one module (C-10) that keeps the Part B superset; specify the 24 conformance tests as **one-line test titles here**, with the full test specification deferred to P5; carry the error table and the worked example across.
- **Exit:** the full protocol text exists as numbered modules; Part B's content is fully absorbed; nothing is duplicated between files.

#### S1.3 — Resolve the §11 duplication
- **Files:** `05-console-turns.md`.
- **Tasks:** confirm that Part B §11.1–§11.6 is a strict superset of Part A §11.1–§11.2 (it is, on inspection — Part B adds §11.3 polling, §11.4 missed windows, §11.5 expired tokens, §11.6 operator authorisation); delete the Part A fragment; record the supersession in the CHANGELOG.
- **Exit:** exactly one definition of console turns in the protocol.

#### S1.4 — Write the protocol index
- **Files:** `htsm.md`.
- **Tasks:** informative overview in the house style of `amk-authority.md` and `amk-prompt.md` — role, trust boundary, the 12 rules at a glance, a pointer to each module, a Defaults pointer, an open-items pointer, and a "what this is not" paragraph.
- **Exit:** `htsm.md` is complete and every module is reachable from it.

#### S1.5 — Open items register
- **Files:** `99-open-items.md`.
- **Tasks:** record every unresolved question that survived P0 — including every `INV-` citation that could not be remapped, the fingerprint/session-identity question if deferred, and the release-manifest question.
- **Exit:** the open-items register mirrors the format of `amk-prompt/99-open-items.md` and `amk-controller/99-open-items.md`.

**Gate G1:** all 12 rules numbered; zero dangling citations; the protocol reads end to end without referring to an undefined term or document.

### Phase 2 — Defaults manifest

#### S2.1 — Assign each default an owner and a rationale class
- **Output:** a table mapping all 26 `DV-` IDs to the contract that owns them (`defaults`, `capability`, `runtime-registration`, `console`).
- **Exit:** every ID has exactly one owner; no ID is claimed twice.

#### S2.2 — Decide the profile dimension
- **Decided by:** `D-HTSM-05`.
- **Tasks:** for each ID decide whether the value is profile-dependent (TTL, budget, retention, rate limit) or absolute (entropy bits, parameter constraints, structural limits).

#### S2.3 — Write the defaults manifest
- **Files:** `contracts/defaults/README.md`.
- **Tasks:** for each of the 26 IDs give the ID, meaning, unit, Basic value, Standard value, High value where it differs, and a one-line rationale. Every value must be justified by the risk it bounds, not chosen for convenience — an implementer must be able to argue a different number.
- **Exit:** 26/26 resolved; the manifest is the single source of truth and Part A's rule ("each is a DV- ID defined only in the defaults manifest") holds.

#### S2.4 — Publish the resolved values machine-readably
- **Output:** a JSON form of the resolved defaults, plus the rule that a conforming deployment MUST publish the resolved values it uses (Part A §9).
- **Tasks:** define the deployment-published form; state how a deployment records a deviation.
- **Exit:** the self-containment requirement in Part A §9 is satisfiable.

**Gate G2:** 26/26 `DV-` IDs defined; no default stated in two places.

### Phase 3 — Companion contracts

Each contract states **its own** boundary, its interface with HTSM, and what it does not do. None re-derives HTSM.

#### S3.1 — Lifecycle
- **Files:** `contracts/lifecycle/README.md`.
- **Tasks:** define the ten session states from Part A §2 — `CREATED`, `ACTIVE`, `PENDING_CONSOLE`, `DEGRADED`, `PAUSED`, `HALTED`, `CLOSED`, `EXPIRED`, `REVOKED`, `SUPERSEDED` — each with entry conditions, exit conditions, legal transitions and the HTSM error codes reachable in that state; resolve the `DEGRADED` collision per `D-HTSM-03`; map each state to the `console_turn_pending` / `session_paused` / `session_ended` behaviour in the error table.
- **Exit:** every state named in the protocol is defined, and the Part A §10.5 check-7 list is justified by this document.

#### S3.2 — Policy
- **Files:** `contracts/policy/README.md`.
- **Tasks:** define the risk classes that trigger a console turn (Part A §10.5 check 15), the policy that binds them, the level-to-node gate, and which anomalies policy marks as needing review (Part A §11.1).
- **Exit:** check 15 is fully determined by this document; no implementation must invent a risk classification.

#### S3.3 — Capability
- **Files:** `contracts/capability/README.md`.
- **Tasks:** define capability token issuance, the two lifetimes (`DV-CAP-TOKEN-TTL-S`, `DV-CAP-TOKEN-DESTRUCTIVE-TTL-S`), the single-use rule for destructive operations, and the guarantee that the Host AI never sees the token (Part B §12).
- **Exit:** the effect boundary in §12 is implementable.

#### S3.4 — Console
- **Files:** `contracts/console/README.md`.
- **Tasks:** the console's obligations towards HTSM — the signed receipt format (`sig_t = Sign(console_key, H(read_set_hash || current_continuation || session_id || nonce_t))`), the handoff rendering per `D-HTSM-06`, the console-only operation list that must be unrepresentable (HTSM-04), and the pointer to the authoritative envelope contract.
- **Exit:** the console's HTSM obligations are closed, and C-06 and C-14 are resolved.

#### S3.5 — Cryptography
- **Files:** `contracts/cryptography/README.md`.
- **Tasks:** the hash and signature constructions used by HTSM — `H(…)`, `Sign(…)`, the genesis anchor, the receipt, the subtree signature — with the requirement that they be non-repudiable and that the specification not mandate a single algorithm where several are acceptable.
- **Exit:** an implementer can choose an algorithm and still be conformant, and the choice is auditable.

#### S3.6 — Effect dispatch
- **Files:** `contracts/effect-dispatch/README.md`.
- **Tasks:** the boundary of §12 — what the dispatcher does with an accepted proposal, how `failed` and `revoked` result states are produced, and how the result reaches the AI without exposing the capability token.
- **Exit:** the effect path is implementable independently of the ingress, honouring the layer separation in Part A §1.

#### S3.7 — Release manifest
- **Files:** `contracts/release-manifest/README.md`.
- **Tasks:** the `release_ref` (hash and epoch), the kernel-subtree definition hash that the manifest must carry (Part B §18), and the rule that a manifest change altering `release_ref` halts live sessions (Part A §10.8).
- **Exit:** §10.8 is implementable, and a manifest can be validated against a live session.

#### S3.8 — Runtime registration
- **Files:** `contracts/runtime-registration/README.md`.
- **Tasks:** subtree submission, provider signing, controller validation against kernel policy and the size caps, the operator's per-session authorisation, and the revision cap — with an explicit statement on whether this closes `CTR-5:3` (C-12).
- **Exit:** Part B §13 is implementable end to end.

**Gate G3:** all nine contracts published; the session state machine exists; every `DV-` ID referenced by a contract is defined.

### Phase 4 — Registry and machine-readable schemas

#### S4.1 — The node-type registry
- **Files:** `specification/HTSM-REGISTRY-V1.md`.
- **Tasks:** publish registry v1 as the authoritative list of the five node types, the eight parameter types, the 17 error codes and the reserved parameter names, each with its constraints and its version; state the compatibility rule for a registry bump (Part A §7.1, Part B §18); add `registry_version` to `kernel/identity` per `D-HTSM-08`.
- **Exit:** the registry is a complete, versioned, closed list — nothing in the protocol names a type that the registry does not contain, and vice versa.

#### S4.2 – S4.7 — The six schemas
- **Files:** `schemas/node.schema.json`, `handoff.schema.json`, `request.schema.json`, `response.schema.json`, `proposal.schema.json`, `error.schema.json`.
- **Tasks:** for each, replace the placeholder with a real JSON Schema (draft 2020-12, matching the `$schema` already declared) that encodes the normative shape from the protocol:
  - `node` — the seven fields of Part A §7, with `type` constrained to the registry enum and `owner` to `kernel` or `runtime:{id}`;
  - `handoff` — the `htsm=1 base=… surface=… root=… continuation=G.…` form of Part A §10.2, including the `R.` resume prefix of §11.5, as both a string form and a structured form;
  - `request` — the request node's parameter declarations, `type` constrained to the eight parameter types, with the enum/int/bool/slug/hex/b64url/text/handle-ref constraints of Part A §9.1 expressed as schema keywords where possible;
  - `response` — the success body of Part B §15.1 including `htsm`, `ok`, `step`, `ttl`, `node`, `content`, `next`, `continuation`, `budget`, `turn`, `result`;
  - `proposal` — the proposal record with its seven result states and the `DV-PROPOSAL-*` bounds;
  - `error` — the error body of Part B §15.2 with `code` constrained to the registry enum and a fixed generic `message` per code.
- **Exit:** all six validate against their examples, and the `error` schema's code enum is byte-equal to the registry's error list.

#### S4.8 — Schema validation harness
- **Output:** a validation script and a CI step.
- **Tasks:** validate every fixture against every schema; fail the build on an unknown property where the schema is closed.
- **Exit:** the harness runs in CI and the tree has no placeholders left.
- **Note:** the build tooling referenced by the root README (`amk/scripts/`, `.github/workflows/`) is **not present in this repository tree**. Confirm the real home of the build scripts before wiring CI.

**Gate G4:** the registry is published and versioned; the six schemas validate; the schemas and the registry agree with each other and with the protocol text.

### Phase 5 — Conformance and security profile

#### S5.1 — Specify the conformance tests
- **Files:** `specification/HTSM-CONFORMANCE.md`.
- **Tasks:** for each of `HTSM-T01` … `HTSM-T24`, write the **test specification**: purpose, the rule or section under test, preconditions and fixture, the procedure, the expected observable result (HTTP status, error code, response shape, session state, audit record), and the evidence a conformant implementation must retain. Apply the repository's `PASS`/`FAIL`/observability discipline. Fold in the new tests proposed in §9.2.
- **Exit:** 24 tests specified in full, each traceable to a rule or section, each executable against a mock.

#### S5.2 — The security profile
- **Files:** `specification/HTSM-SECURITY-PROFILE.md`.
- **Tasks:** expand Part B §17 from six bullets into a threat model with a table of threat, mitigation, residual risk and the profile that raises the mitigation; define the HTSM-specific profile obligations for Basic / Standard / High, consistent with the suite's three-profile model, and state that the console-key requirement is inherited from `amk-console` rather than restated.
- **Exit:** a profile is declarable, and the §17 residual ("side channels from response size and timing are not analyzed") is stated as an explicit accepted residual with a version.

#### S5.3 — The six test-suite directories
- **Files:** `tests/protocol/README.md`, `tests/authentication/README.md`, `tests/lifecycle/README.md`, `tests/security/README.md`, `tests/conformance/README.md`, `tests/interoperability/README.md`.
- **Tasks:** allocate the 24 tests (plus the new ones) across the six suites; each suite states its scope, its fixtures, its harness interface and its pass criteria; `tests/interoperability` covers the Part A §3 host-side wrapper clause (a wrapper is a pass-through only) and cross-implementation checks.
- **Exit:** every test belongs to exactly one suite and every suite is executable.

#### S5.4 — Rule-to-test cross-map
- **Output:** the matrix in §10, maintained as the tests are written.
- **Tasks:** confirm every one of the 12 rules has at least one test, and that no test asserts something the protocol does not require.
- **Exit:** no rule is untested and no test is unfounded.

**Gate G5:** every test specified; the profile defined; the coverage gaps in §9.2 either closed by new tests or explicitly accepted.

### Phase 6 — Fixtures and mocks

#### S6.1 – S6.5 — The five fixture sets
- **Files:** `fixtures/valid/README.md`, `fixtures/invalid/README.md`, `fixtures/replay/README.md`, `fixtures/fork-detection/README.md`, `fixtures/authorization/README.md`.
- **Tasks:** for each set, define the fixture format (JSON request/response pairs plus the setup state), the naming scheme, the catalogue of cases, and the expectation for each case. The five sets map onto the protocol's five verification behaviours: valid traversal, malformed requests, replay of a consumed continuation, reuse of a continuation with different parameters, and the operator-authorisation path.
- **Exit:** every fixture is expressible in the `request`/`response`/`handoff`/`proposal` schemas and is tied to at least one conformance test.

#### S6.6 – S6.10 — The five mocks
- **Files:** `mocks/controller/README.md`, `mocks/operator-console/README.md`, `mocks/runtime-provider/README.md`, `mocks/capability-issuer/README.md`, `mocks/effect-dispatcher/README.md`.
- **Tasks:** for each mock, specify the interface it presents, the behaviour it must reproduce faithfully, the behaviour it must *not* implement (it is a mock, not an implementation), and the tests that depend on it. The `effect-dispatcher` mock is the one that proves the layer separation: the conformance suite runs with effects fully simulated outside the ingress.
- **Exit:** the five mocks together can host the whole conformance suite.

**Gate G6:** fixtures validate against the schemas; the suite runs against the mocks.

### Phase 7 — Integration, reconciliation and release

#### S7.1 — Cross-spec reconciliation
- **Tasks:** walk the five core components and the session router against the completed HTSM; close or explicitly re-open the conflicts each one already records:
  - `E-PMT-06` — the undefined error vocabulary (`E_UNREACHABLE`, `fork_detected`, `chain_broken`, `work_order_required`, `UNVERIFIED`) — HTSM's error registry is the natural home for the HTSM-side codes; `E_UNREACHABLE` and `UNVERIFIED` may belong to the prompt's own layer and must be stated as such.
  - `E-CTR-12` — the missing lifecycle state machine — now closed by `contracts/lifecycle/`.
  - `E-CTR-14` — the undefined standard error code and the absent outcome taxonomy — closed by the error registry plus an outcome taxonomy.
  - `CORE-O-01` — the epoch terminology collision — check whether HTSM's *generation* concept collides with it.
  - `CTR-O-06`, `RTM-O-17` — channels beyond HTSM — HTSM §1 already answers that other methods exist at the runtime level; confirm the controller's framing agrees.
- **Exit:** no mandatory contradiction remains; every conflict in §5 is closed or recorded as an open item with an owner.

#### S7.2 — Index and cross-reference upkeep
- **Tasks:** update the root `README.md` and any suite index to point at the completed HTSM tree. **Note (out of scope but recorded):** the root README still describes the pre-restructure `amk/` layout — `amk/amk-ssot.md`, `amk/components/…`, `amk/protocols/…` — while the tree is now `specifications/{core,contracts,protocols}/`. The paths in the root README are stale independently of HTSM. Record it; do not fix it inside this plan.

#### S7.3 — Publish the README, CHANGELOG and licence
- **Files:** `README.md`, `CHANGELOG.md`, `LICENSE`.
- **Tasks:** publish the README (delivered with this plan); write the CHANGELOG recording the delta from `1.0.1-draft` — the §11 de-duplication, the rule renumbering, the module split, the registry version, the new tests, and the resolved conflicts; resolve the licence per `D-HTSM-10` or state its pending status explicitly.
- **Exit:** C-10 satisfied.

#### S7.4 — Build and CI integration
- **Tasks:** wire the placeholder check, the schema validation, the link check and the conformance run into CI; confirm the merge/build tooling's real location (see the note in S4.8); ensure the merged specification regenerates deterministically and that the published `public/assets/aistudio/` copies are regenerated from the module files rather than hand-edited.
- **Exit:** the tree builds clean; the strict build no longer fails on an HTSM placeholder.

#### S7.5 — Final conformance sweep
- **Tasks:** re-read the completed protocol as if implementing it from scratch; list every place an implementer would have to invent behaviour; close each one or record it as an open item with an owner and a target stage; confirm C-1 … C-12.
- **Exit:** C-12 satisfied; the plan is closed.

**Gate G7:** all twelve completion criteria met.

---

## 9. Conformance test map

### 9.1 The existing 24 tests, mapped to rules and modules

| Test | Subject | Rule / section | Module | Phase |
|---|---|---|---|---|
| T01 | No Host AI route other than HTSM GETs | HTSM-01 | `00` | P5 |
| T02 | Non-GET on an HTSM path is `bad_request` | §5 | `01` | P5 |
| T03 | No valid continuation → rejected, no side effect | HTSM-02, HTSM-10 | `04` | P5 |
| T04 | GET on a request node records a proposal, executes nothing | HTSM-03 | `03` | P5 |
| T05 | No node exists for any console-only operation | HTSM-04 | `02` | P5 |
| T06 | Every unknown, duplicate, missing, over-length, mis-encoded parameter is `bad_request` | HTSM-05 | `03` | P5 |
| T07 | A runtime subtree cannot define or shadow a kernel node | HTSM-06 | `07` | P5 |
| T08 | A handle from another session returns `not_found`, identical to an unknown handle | HTSM-07 | `02` | P5 |
| T09 | The chain advances and the old continuation is rejected | HTSM-09 | `04` | P5 |
| T10 | An identical repeat replays; a different repeat is `fork_detected` and HALTS | §9.6, §10.6 | `03`, `04` | P5 |
| T11 | A rejected `bad_request` does not consume the token; the attempt cap forces a console turn | §9.6 | `03` | P5 |
| T12 | `expired_token` → PENDING_CONSOLE; a resume handoff restores it | §11.5 | `05` | P5 |
| T13 | During a pending turn only polls are served; the token clock is paused | §11.3 | `05` | P5 |
| T14 | A missed turn window degrades the session and caps further steps | §11.4 | `05` | P5 |
| T15 | A receipt not signed by the registered console key is refused | §11.2 | `05` | P5 |
| T16 | Subtree attachment is a revision; the chain continues with no re-handoff | §13 | `07` | P5 |
| T17 | A kernel-subtree change requires re-handoff and voids old tokens and handles | §10.7 | `04` | P5 |
| T18 | Level 1 cannot call a Level 2 node (`level_insufficient`) | HTSM-12, §14 | `08` | P5 |
| T19 | Budgets and rate limits produce `budget_exhausted` and `rate_limited` | §10.5 | `04` | P5 |
| T20 | Errors leak no structure beyond the session's projection | HTSM-08 | `09` | P5 |
| T21 | While not READY every request receives only `unavailable` | HTSM-11 | `04` | P5 |
| T22 | Handles and tokens have the required entropy and are never reused | HTSM-07, §10.3 | `04` | P5 |
| T23 | Responses never exceed size limits; `next` templates validate against their own schemas | §5, §9.3 | `01`, `03` | P5 |
| T24 | Concurrent requests on one chain are serialized: exactly one succeeds | §10.4 | `04` | P5 |

### 9.2 Coverage gaps — proposed additional tests

The 24 tests do not cover four behaviours the protocol states normatively. These are proposed as **T25 – T32** and must be
adjudicated in stage S5.1.

| Proposed | Gap it closes | Protocol basis |
|---|---|---|
| **T25** | A Release Manifest change that alters `release_ref` fails check 8 for every subsequent request and moves the session to HALTED with a console alert | §10.8 — currently untested; T17 covers only the kernel-subtree case |
| **T26** | `kernel/identity` reports the registry version | §18 vs §8.2 (C-05) |
| **T27** | Every console-only operation is unrepresentable — the explicit list: authorize, deny, revoke, activate, enroll, recover, and every session, surface and subtree lifecycle action | §8.2 note — T05 tests by enumeration, not against the declared list |
| **T28** | A node in an attached-but-unauthorised runtime subtree returns `not_found`, identical to an unknown handle | §8.4 projection — T08 covers cross-session only |
| **T29** | A request whose risk class exceeds policy records a proposal in state `needs_console` and causes no effect | §10.5 check 15 — the policy branch has no test |
| **T30** | An over-length URL is rejected as `bad_request` **before** the handle is resolved | §6, §10.5 check 4 vs check 5 — ordering is normative and untested |
| **T31** | A `not_found` counts against the probe rate limit | §10.5 check 5 — the counting rule is stated but untested |
| **T32** | The response carries `Cache-Control: no-store` and `Referrer-Policy: no-referrer`, sets no cookie and issues no redirect; an identical repeat carries `X-HTSM-Replay: 1` and a fresh poll does not | §5, §9.6 — transport headers are normative and untested |

---

## 10. Traceability matrix

| Rule | Subject | Module | Default IDs touched | Tests |
|---|---|---|---|---|
| HTSM-01 | HTSM is the baseline Host AI ingress | `00` | — | T01 |
| HTSM-02 | The AI holds no secret and computes nothing | `00` | `DV-HTSM-TOKEN-ENTROPY-BITS` | T03, T22 |
| HTSM-03 | A GET never executes | `00` | — | T04 |
| HTSM-04 | Console-only operations are unrepresentable | `00` | — | T05, T27 |
| HTSM-05 | Parameters are untrusted and validated | `00` | `DV-HTSM-PARAMS-MAX-COUNT`, `DV-HTSM-PARAM-TEXT-MAX-LEN` | T06 |
| HTSM-06 | The kernel subtree is controller-owned | `00` | — | T07 |
| HTSM-07 | Handles and continuations are credentials | `00` | `DV-HTSM-HANDLE-ENTROPY-BITS`, `DV-HTSM-TOKEN-TTL-S` | T08, T22 |
| HTSM-08 | Errors reveal nothing beyond the projection | `00` | — | T20 |
| HTSM-09 | The AI changes session state only by advancing the chain | `00` | — | T09, T24 |
| HTSM-10 | Any verification failure produces an error and no side effect | `00` | — | T03 |
| HTSM-11 | While not READY every request receives only `unavailable` | `00` | — | T21 |
| HTSM-12 | The operational level is set by the operator | `00` | `DV-HTSM-LEVEL-DEFAULT` | T18 |

---

## 11. Risk register

| ID | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| **R-01** | Part A / Part B are published public assets; editing them changes externally visible artefacts. | High | High | Decide `D-HTSM-09` **before** any edit. Prefer regenerating them as build outputs from the module files, so the published copies stay in step automatically. |
| **R-02** | The spine is written before `D-HTSM-01` / `D-HTSM-02` are answered, forcing a rewrite of every rule ID, node name and error code. | Medium | High | G0 is a hard gate. The cost of a rewrite grows with every module written. |
| **R-03** | The session state machine stays missing, so §11 cannot be closed and criterion C-8 fails. | Medium | High | S3.1 is on the critical path and has a named exit criterion. |
| **R-04** | The dangling `INV-` citations survive into a published version, tripping the release gate "no unresolved mandatory contradiction or obsolete normative reference". | Medium | High | S0.3 (`D-HTSM-04`) plus the S7.1 sweep, which checks citations mechanically. |
| **R-05** | Defaults are written across several contracts and drift apart. | Medium | Medium | S2.1 assigns each of the 26 IDs exactly one owner. |
| **R-06** | The `contracts/` tree and `specifications/contracts/` overlap, producing two authoritative homes. | Medium | Medium | `D-HTSM-10`; S3.4 and S3.8 make `htsm/contracts/` a boundary view that points outward. |
| **R-07** | The repository's documented build and CI tooling is not present in this tree, so the placeholder check and schema validation cannot be wired as assumed. | High | Medium | Verified during S4.8 before any CI claim is made. No CI statement in this plan should be treated as verified until that stage runs. |
| **R-08** | The root `README.md` still points at the pre-restructure `amk/` layout, so links from the suite level into HTSM are broken. | High | Medium | Recorded in S7.2 as out of scope for HTSM and reported to the operator separately. |
| **R-09** | The fixtures and mocks are specified but never executed, so the conformance suite is a document rather than a gate. | Medium | High | S6 is gated on executability (G6), not on documentation. |
| **R-10** | The optional session-validation layer is settled in isolation and later conflicts with the prompt's `PMT-7:13` conduct rules. | Medium | Medium | `D-HTSM-02` is answered with the prompt's text in view; S7.1 re-checks it. |
| **R-11** | Licence remains unresolved, so `LICENSE` cannot be published and the strict build fails. | Medium | Low | `D-HTSM-10` records the pending status explicitly rather than publishing a placeholder. |

---

## 12. Critical path and resourcing

**Critical path:** S0.1 → S0.3 (decisions) → S0.4 → S1.1 → S1.2 → S4.1 → S5.1 → S5.2 → S6.1 → S7.1 → S7.5.

Two structural observations:

1. **The decisions are worth more than the writing.** `D-HTSM-01` and `D-HTSM-02` determine the vocabulary of every one of
   the 12 rules, the registry, the error table, the schemas and the fixtures. One hour spent resolving them correctly
   saves the cost of re-writing three phases.
2. **Phases 2, 3 and 6 parallelise.** A team of three can run P2 (defaults), P3 (contracts) and P6 (fixtures and mocks)
   concurrently once P1 establishes the structure, provided all three read the same module split from S0.4.

**Indicative effort per phase** (relative, not absolute — one unit ≈ one working day of a spec author):

| Phase | P0 | P1 | P2 | P3 | P4 | P5 | P6 | P7 |
|---|---|---|---|---|---|---|---|---|
| Units | 3 | 8 | 4 | 12 | 7 | 9 | 8 | 6 |
| Parallelism | — | — | ▸ P2 | ▸ P3 | — | — | ▸ P6 | — |

P3 is the largest phase. It carries eight contracts and the session state machine, and the state machine in particular
depends on decisions that may not be fully settled at G0.

---

## 13. File-by-file completion manifest

All 39 files in `specifications/protocols/htsm/`, with the stage that completes them. **"Current"** is the state at the
baseline commit `1463d89`.

| # | File | Current | Completed by | Result |
|---|---|---|---|---|
| 1 | `README.md` | `TODO` | **S7.3** | Protocol overview, reading order, status, links |
| 2 | `CHANGELOG.md` | `TODO` | **S7.3** | Delta from `1.0.1-draft` |
| 3 | `LICENSE` | `TODO` | **S7.3** | Licence terms, or the pending statement |
| 4 | `specification/HTSM-PART-A.md` | Substantive | **S1.1**, regenerated in **S7.4** | Generated from `00`–`04` |
| 5 | `specification/HTSM-PART-B.md` | Substantive | **S1.2**, regenerated in **S7.4** | Generated from `05`–`13` |
| 6 | `specification/HTSM-CONFORMANCE.md` | `TODO` | **S5.1** | 24 + 8 test specifications |
| 7 | `specification/HTSM-REGISTRY-V1.md` | `TODO` | **S4.1** | Node types, parameter types, error codes, versioning |
| 8 | `specification/HTSM-SECURITY-PROFILE.md` | `TODO` | **S5.2** | Threat model and profile obligations |
| 9 | `htsm.md` (new) | — | **S1.4** | Index and informative overview |
| 10 | `00-foundations.md` (new) | — | **S1.1** | Scope, terms, principles, the 12 rules |
| 11 | `01-transport-addressing.md` (new) | — | **S1.1** | Transport and addressing |
| 12 | `02-surface-model.md` (new) | — | **S1.1** | Node model, kernel and runtime subtrees, projection |
| 13 | `03-parameters-requests.md` (new) | — | **S1.1** | Parameters, templates, semantics, result states, retries |
| 14 | `04-chain-authentication.md` (new) | — | **S1.1** | Genesis, continuations, verification order, forks, generations |
| 15 | `05-console-turns.md` (new) | — | **S1.2**, **S1.3** | Console turns, polling, missed windows, expired tokens |
| 16 | `06-effects.md` (new) | — | **S1.2** | Proposal → effect boundary |
| 17 | `07-subtrees.md` (new) | — | **S1.2** | Attachment and revision |
| 18 | `08-levels.md` (new) | — | **S1.2** | Operational levels |
| 19 | `09-responses-errors.md` (new) | — | **S1.2** | Success and error responses |
| 20 | `10-audit-alerts.md` (new) | — | **S1.2** | Audit record and console alerts |
| 21 | `11-security.md` (new) | — | **S1.2** | Security considerations |
| 22 | `12-versioning.md` (new) | — | **S1.2** | Versioning and extensibility |
| 23 | `13-conformance.md` (new) | — | **S1.2**, expanded **S5.1** | Test titles, then full specifications |
| 24 | `99-open-items.md` (new) | — | **S1.5** | Open items register |
| 25 | `99-appendices.md` (new) | — | **S1.2** | Worked example, glossary |
| 26 | `contracts/capability/README.md` | `TODO` | **S3.3** | Capability issuance and lifetimes |
| 27 | `contracts/console/README.md` | `TODO` | **S3.4** | Console obligations to HTSM |
| 28 | `contracts/cryptography/README.md` | `TODO` | **S3.5** | Hash and signature constructions |
| 29 | `contracts/defaults/README.md` | `TODO` | **S2.3** | All 26 `DV-` values |
| 30 | `contracts/effect-dispatch/README.md` | `TODO` | **S3.6** | The §12 boundary |
| 31 | `contracts/lifecycle/README.md` | `TODO` | **S3.1** | The session state machine |
| 32 | `contracts/policy/README.md` | `TODO` | **S3.2** | Risk classes and level gating |
| 33 | `contracts/release-manifest/README.md` | `TODO` | **S3.7** | `release_ref`, epoch, subtree hash |
| 34 | `contracts/runtime-registration/README.md` | `TODO` | **S3.8** | Subtree submission and validation |
| 35 | `fixtures/valid/README.md` | `TODO` | **S6.1** | Valid traversal cases |
| 36 | `fixtures/invalid/README.md` | `TODO` | **S6.2** | Malformed request cases |
| 37 | `fixtures/replay/README.md` | `TODO` | **S6.3** | Replay cases |
| 38 | `fixtures/fork-detection/README.md` | `TODO` | **S6.4** | Fork cases |
| 39 | `fixtures/authorization/README.md` | `TODO` | **S6.5** | Operator authorisation cases |
| 40 | `mocks/capability-issuer/README.md` | `TODO` | **S6.9** | Capability mock contract |
| 41 | `mocks/controller/README.md` | `TODO` | **S6.6** | Controller mock contract |
| 42 | `mocks/effect-dispatcher/README.md` | `TODO` | **S6.10** | Effect dispatcher mock contract |
| 43 | `mocks/operator-console/README.md` | `TODO` | **S6.7** | Console mock contract |
| 44 | `mocks/runtime-provider/README.md` | `TODO` | **S6.8** | Runtime provider mock contract |
| 45 | `schemas/error.schema.json` | Placeholder | **S4.7** | Error body schema |
| 46 | `schemas/handoff.schema.json` | Placeholder | **S4.3** | Handoff schema |
| 47 | `schemas/node.schema.json` | Placeholder | **S4.2** | Node schema |
| 48 | `schemas/proposal.schema.json` | Placeholder | **S4.6** | Proposal schema |
| 49 | `schemas/request.schema.json` | Placeholder | **S4.5** | Request schema |
| 50 | `schemas/response.schema.json` | Placeholder | **S4.4** | Response schema |
| 51 | `tests/authentication/README.md` | `TODO` | **S5.3** | Chain authentication suite |
| 52 | `tests/conformance/README.md` | `TODO` | **S5.3** | Conformance suite |
| 53 | `tests/interoperability/README.md` | `TODO` | **S5.3** | Interoperability suite |
| 54 | `tests/lifecycle/README.md` | `TODO` | **S5.3** | Lifecycle suite |
| 55 | `tests/protocol/README.md` | `TODO` | **S5.3** | Protocol suite |
| 56 | `tests/security/README.md` | `TODO` | **S5.3** | Security suite |
| 57 | `HTSM-COMPLETION-PLAN.md` (new) | — | **this document** | The plan itself |

*Rows 9–25 and 57 are new files. Rows 4 and 5 remain in place as generated outputs.*

---

## 14. Definition of done

The plan is complete when, for the HTSM tree:

1. **C-1** — no placeholder file remains (39 → 57 files, 0 placeholders);
2. **C-2** — 26/26 `DV-` IDs defined, each in exactly one place;
3. **C-3** — 8/8 `INV-` citations resolved or remapped;
4. **C-4** — 12/12 rules carry stable IDs;
5. **C-5** — 24/24 conformance tests specified, plus the adjudicated additions from §9.2;
6. **C-6** — 6/6 schemas validate and are exercised by fixtures;
7. **C-7** — 9/9 contracts published with their HTSM interfaces stated;
8. **C-8** — the session state machine exists with all ten states defined;
9. **C-9** — no mandatory contradiction with any core component;
10. **C-10** — README, CHANGELOG and LICENSE published;
11. **C-11** — the mocks can host the conformance suite;
12. **C-12** — an independent implementer can build HTSM without inventing normative behaviour.

**And the plan itself is archived** in the CHANGELOG, with the decision register's answers and their dates, so the reasoning
survives the artefacts.
