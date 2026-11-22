# Skew, nonce lifetime, TTL and sequence storage

**Area:** `abstract-kernel/time-and-freshness/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

Every place the suite measures time: clock skew tolerance, nonce freshness and lifetime, envelope and token TTLs, sequence-number storage, and the grace window at an epoch boundary.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `CORE-O-06` | Skew windows, nonce lifetime, TTL and sequence storage, with per-profile values. `CNS-O-04` = `CTR-O-08`; only the Authority has values today (`DV-ATA-04`, `DV-ATA-07`). | **Blocking** |
| `CORE-O-31` | Envelope nonce scope. `CNS-3:8` implies a global seen-set; `CNS-3:3` scopes sequence to the sender identity. Neither is stated. | Medium |
| `—` | Capability-token lifetime. `CTR-7:1` says "short-lived" with no value (`E-CTR-05`). | High |

---

## Cross-references

- [`invariants`](../invariants/README.md)
- [`cryptographic-lifecycle`](../cryptographic-lifecycle/README.md)
- [`canonical-data-model`](../canonical-data-model/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
