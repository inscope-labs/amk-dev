# Threats, and the invariants that answer them

**Area:** `abstract-kernel/threat-model/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

The suite threat model: each threat, the invariant that answers it, the control that implements it, and the residual risk that is accepted rather than closed.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `—` | No suite-wide threat model exists; each component states its own considerations. | High |
| `—` | The residual risks the suite accepts must be stated as accepted, with a version — HTSM §17 already does this for side channels. | Medium |

---

## Cross-references

- [`invariants`](../invariants/README.md)
- [`security-requirements`](../security-requirements/README.md)
- [`trust-model`](../trust-model/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
