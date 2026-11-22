# How third parties extend the kernel without weakening it

**Area:** `abstract-kernel/backend-extension-model/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

The extension boundary: what an extension point may add, what it may never do, how an extension is authorised and versioned, and how it is revoked.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `—` | The extension-point directories the framework README lists are placeholders; this area defines what belongs in them. | Medium |
| `—` | The runtime boundary itself — `RTM-1:16` forbids weakening kernel boundaries but no rule states how a boundary is tested. | Medium |

---

## Cross-references

- [`invariants`](../invariants/README.md)
- [`architecture`](../architecture/README.md)
- [`security-requirements`](../security-requirements/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
