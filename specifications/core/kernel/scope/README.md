# Scope, suite identity and the suite's own registers

**Area:** `abstract-kernel/scope/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

What the kernel is and is not; the suite version and `contract_version`; the cross-component open-item register (`CORE-O-nn`); the conformance and reference-ID rules that all five components share.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `CORE-O-14` | The suite version and the value of `contract_version`. `amk-ssot.md` is cited normatively by `CNS-3:16` and `CTR-3:10` and does not exist. | **Blocking** |
| `CORE-O-38` | Whether `Duplicate of …` stubs are marked `RETIRED` or given a third marker for delegations. | Low |
| `—` | The cross-spec open-item register itself: `specifications/core/OPEN-ITEMS.md` is cited by three components and does not exist. | **Blocking** |

---

## Cross-references

- [`invariants`](../invariants/README.md)
- [`operating-levels`](../operating-levels/README.md)
- [`canonical-data-model`](../canonical-data-model/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
