# Envelopes, states, errors, audit records and defaults

**Area:** `abstract-kernel/canonical-data-model/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

The shapes the suite agrees on: one envelope catalogue with field-level schemas; the session and package state machines; the error and outcome taxonomy; the audit record format; and where default values live.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `CORE-O-01` | The session state machine. Ten HTSM states, five controller bootstrap states, five runtime package states and four prompt-reported states, with no shared model. | **Blocking** |
| `CORE-O-02` | The error and outcome taxonomy. `work_order_required`, `E_UNREACHABLE` and `UNVERIFIED` are required by the prompt and defined nowhere (`E-PMT-06`, `E-CTR-14`). | **Blocking** |
| `CORE-O-04` | The envelope catalogue. Two parallel, non-identical catalogues today; one row with no type name; `SessionPolicy`, `CheckIn` and `WorkOrderProposal` in neither. | **Blocking** |
| `CORE-O-05` | The audit record format, hash chain, retention and checkpoint protection. Only the Authority defines one (`ATA-06:10`). | **Blocking** |
| `CORE-O-07` | Where default values live. Only one document in the repository has a Defaults block (`amk-authority.md:137`); `DV-RTM-01`…`08` and 26 `DV-HTSM-*` have no home. | **Blocking** |
| `CORE-O-30` | The definition and counter of a surface revision, and its console-side counterpart. | High |
| `CORE-O-32` | Revocation immediacy versus capability-token expiry. | High |
| `CORE-O-36` | The console's `SessionPolicy` envelope and the Auto Assist flag. | High |

---

## Cross-references

- [`invariants`](../invariants/README.md)
- [`operating-levels`](../operating-levels/README.md)
- [`time-and-freshness`](../time-and-freshness/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
