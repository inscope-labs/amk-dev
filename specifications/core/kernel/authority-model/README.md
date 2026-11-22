# How authority flows, and who may not widen it

**Area:** `abstract-kernel/authority-model/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

The authority graph and the boundaries around it: the single canonical list of console-only operations; how a work order differs from a proposal; how pre-authorization is scoped; and who may never widen a grant.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `CORE-O-03` | The canonical console-only operation list. Three specifications state three different lists (`CTR-4:7`, `PMT-4:4`, `HTSM-PART-A.md:136`). | **Blocking** |
| `CORE-O-36` | Auto Assist continuation: `CNS-5:12` says "solely" the pre-authorization governs; `CTR-6:15` requires three conditions together. | High |
| `—` | Role binding. The console conflates "Role / Runtime" (`amk-console/00-foundations.md:22`); the controller never mentions roles, a session record or `declared_roles` (`E-CTR-03`). | High |

---

## Cross-references

- [`invariants`](../invariants/README.md)
- [`canonical-data-model`](../canonical-data-model/README.md)
- [`operating-levels`](../operating-levels/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
