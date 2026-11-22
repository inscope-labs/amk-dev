# Key lifecycle, rotation and recovery

**Area:** `abstract-kernel/cryptographic-lifecycle/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

The lifecycle of every key the suite knows: generation, protection class, distribution, rotation, revocation, retirement, and the recovery protocol after loss or compromise.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `CORE-O-09` | The recovery protocol at Standard and High. Required by `CNS-6:11` and `CTR-7:12`; it does not exist (`CNS-O-03` = `CTR-O-07`). | **Blocking** |
| `CORE-O-24` | Session private key protection. Unconstrained by all five profile tables; `ATA-06` covers only the Authority's own signing keys. | High |
| `CORE-O-25` | Root-key rotation lifecycle and the status vocabulary for root keys. The `status` vocabulary is defined only for `signing_keys` entries. | Medium |
| `—` | `E-RTM-03`: the runtime requires *public* verification keys to be non-exportable (`RTM-8:6`, `RTM-8:11`, `RTM-8:16`). What needs protection is the integrity of trust anchors. | **Blocking** |

---

## Cross-references

- [`trust-bootstrap`](../trust-bootstrap/README.md)
- [`time-and-freshness`](../time-and-freshness/README.md)
- [`operating-levels`](../operating-levels/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
