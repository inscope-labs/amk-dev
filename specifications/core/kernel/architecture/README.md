# How the components fit together

**Area:** `abstract-kernel/architecture/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

The suite architecture: component boundaries, what each one owns, where state lives, and the paths between them — including the paths that no component currently owns.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `—` | The loader is absent from the controller. `RTM-5:1` requires the Release Manifest to pin the loader's identity and hash; `CTR-2:2` lists neither (`E-RTM-09`). | **Blocking** |
| `CORE-O-33` | Which component holds authoritative package lifecycle state. | High |
| `—` | `amk-core` identity: system package, system runtime, or core runtime (`amk-runtime.md:16` vs `RTM-7:11`). | Medium |

---

## Cross-references

- [`trust-model`](../trust-model/README.md)
- [`canonical-data-model`](../canonical-data-model/README.md)
- [`backend-extension-model`](../backend-extension-model/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
