# How trust anchors are established and distributed

**Area:** `abstract-kernel/trust-bootstrap/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

The bootstrap chain: Release Key, controller signing key, Authority root key, publisher keys and console keys — where each is pinned, how it reaches the party that holds it, and how it is rotated.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `CORE-O-08` | Key distribution and pinning: `O-ATA-11` (controller-to-Authority interface, blocking for 1.2.0), `CNS-O-13` and `CTR-O-16`. | **Blocking** |
| `—` | The Release private key has no stated custodian or protection rule (`CTR-10:3`, `RTM-6:1`). | High |
| `—` | Console key enrollment at the controller (`CTR-4:5`, `CTR-O-03`). | High |

---

## Cross-references

- [`trust-model`](../trust-model/README.md)
- [`cryptographic-lifecycle`](../cryptographic-lifecycle/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
