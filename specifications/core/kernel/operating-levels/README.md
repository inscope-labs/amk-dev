# Operational profiles, assurance levels and the level family

**Area:** `abstract-kernel/operating-levels/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

Basic / Standard / High and the L0 / L1 / L2 assurance levels; the profile rules that are common to all components; the peer rule; profile discovery; and every "level" the suite uses.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `CORE-O-10` | What "soft isolation" permits at Basic. One term, three specifications, no definition anywhere, colliding with profile-independent isolation rules (`E-CTR-01`, `E-CNS-02`, `E-RTM-01`). | **Blocking** |
| `CORE-O-11` | The peer profile rule. `CNS-7:23` names the operator, `CTR-8:22` the administrator, `ATA-06:07` both — and the runtime has none at all (`E-RTM-07`). | High |
| `CORE-O-12` | Profile discovery. `CNS-7:2`, `CTR-8:2` and `RTM-8:2` share identical wording that relies on `StatusResponse` and `CapabilityToken`, neither of which has behavioural rules. | High |
| `CORE-O-26` | The level vocabulary: at least six ladders (assurance, HTSM operational, kernel operating, operating, baseline, `min_kernel_level`, level cap). | High |
| `CORE-O-27` | `profile` names both the operational profile and the Host AI profile inside one controller module (`CTR-6:10`, `CTR-6:11` vs `CTR-8:1`). | Medium |
| `—` | Profile exclusion by platform capability. Only the controller has one (`CTR-8:21`). | Low |

---

## Cross-references

- [`invariants`](../invariants/README.md)
- [`security-requirements`](../security-requirements/README.md)
- [`canonical-data-model`](../canonical-data-model/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
