# Suite-wide security obligations

**Area:** `abstract-kernel/security-requirements/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

The requirements that hold at every profile because they are security properties rather than engineering quality: isolation, key protection, attestation, audit integrity, and the conformance rule set.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `CORE-O-10` | The minimum isolation Basic must keep, stated once instead of three times. | **Blocking** |
| `CORE-O-21` | The conformance rule set ("protocol standards") that both the console and the loader must apply (`RTM-O-08`). | Medium |
| `—` | Remote attestation: four subjects, four table labels, all SHOULD — while the Authority consumes RATS evidence at High (`ATA-06:04`). | High |
| `—` | Side-channel resistance is "expected" rather than RFC 2119 language (`CNS-7:22`, `CTR-8:20`). | Low |

---

## Cross-references

- [`invariants`](../invariants/README.md)
- [`threat-model`](../threat-model/README.md)
- [`operating-levels`](../operating-levels/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
