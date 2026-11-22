# Abstract Kernel

**Area:** `abstract-kernel/` — the suite-level contract that the five core components share.
**Status:** **Draft.** The `invariants/` area is written; the other thirteen are reserved and scoped.
**Suite compatibility:** `OS10-AMK 2.x`

---

## What this area is

The five core specifications — `amk-authority`, `amk-console`, `amk-controller`, `amk-prompt`, `amk-runtime` —
are five partial views of one contract. The contract itself was never written. Each component states its own
boundary precisely, and each points outward for the things that belong to the suite as a whole: the shared
invariants, the envelope catalogue, the state machines, the error taxonomy, the audit format, the freshness
rules, the key lifecycle, and the profile rules.

`abstract-kernel/` is that missing contract.

It is **not** a seventh component. It has no runtime, no state and no enforcement point. It states properties
that hold across the suite, and the components remain the places where those properties are checked.

---

## Areas

| Area | Holds | State |
|---|---|---|
| [`invariants/`](invariants/README.md) | The shared invariant register (`INV-1` … `INV-48`) and the `INV-0` non-conformance rule | **Written** |
| [`scope/`](scope/README.md) | Suite scope, suite version, `contract_version`, the cross-spec open-item register | Reserved |
| [`principles/`](principles/README.md) | The design principles the invariants realise | Reserved |
| [`trust-model/`](trust-model/README.md) | Who trusts whom, and for what | Reserved |
| [`authority-model/`](authority-model/README.md) | How authority flows; the console-only operation list | Reserved |
| [`trust-bootstrap/`](trust-bootstrap/README.md) | How trust anchors are established and distributed | Reserved |
| [`cryptographic-lifecycle/`](cryptographic-lifecycle/README.md) | Key lifecycle, rotation and recovery | Reserved |
| [`time-and-freshness/`](time-and-freshness/README.md) | Skew, nonce lifetime, TTL, sequence storage | Reserved |
| [`operating-levels/`](operating-levels/README.md) | Basic/Standard/High, L0/L1/L2, the peer rule, the level family | Reserved |
| [`canonical-data-model/`](canonical-data-model/README.md) | Envelopes, states, errors, audit records, defaults | Reserved |
| [`security-requirements/`](security-requirements/README.md) | Suite-wide security obligations | Reserved |
| [`threat-model/`](threat-model/README.md) | Threats and the invariants that answer them | Reserved |
| [`architecture/`](architecture/README.md) | How the components fit together | Reserved |
| [`backend-extension-model/`](backend-extension-model/README.md) | How third parties extend without weakening | Reserved |

---

## The invariant register

[`invariants/README.md`](invariants/README.md) holds forty-eight suite-level invariants in eight groups, plus
`INV-0` — the rule that an implementation violating any invariant is non-conformant at every profile.

It exists to give the suite one shared vocabulary. Before it, the five components each carried a private
invariant list with no index and no stated relationship between them; the Host AI Kernel Prompt required its
invariants to be "registered in the contracts", a register that did not exist; and the HTSM protocol cited eight
suite invariant IDs — `INV-18`, `INV-26`, `INV-29` … `INV-34` — that were defined nowhere in the repository.

All eight now resolve, at the numbering HTSM already cited.

---

## Relationship to the components

| Component | Owns | Defers to this area for |
|---|---|---|
| `amk-authority` | Attestation issuance, verification and revocation | key lifecycle, time and freshness, profile rules |
| `amk-console` | Operator intent, envelope emission, validation gates | the envelope catalogue, the error taxonomy, the freshness rules |
| `amk-controller` | Enforcement, state, capability tokens, the surface | the state machines, the profile rules, the audit format |
| `amk-prompt` | The advisory Host AI contract | the instruction boundary, the error vocabulary |
| `amk-runtime` | Package provenance, conformance, loading | the key lifecycle, the conformance rule set, the extension boundary |

The cross-component alignment report —
[`../COMPONENT-ALIGNMENT.md`](../COMPONENT-ALIGNMENT.md) — records the measured alignment state, the corrections
applied to the components, and the corrections that need an owner decision.

---

*Suite version `OS10-AMK 2.x` · draft for review*
