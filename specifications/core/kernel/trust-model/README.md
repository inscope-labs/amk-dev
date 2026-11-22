# Who trusts whom, and for what

**Area:** `abstract-kernel/trust-model/`
**Status:** **Placeholder.** The area is reserved and its scope is fixed; its content is not yet written.

---

## Purpose

The suite trust graph: which component is trusted for which statement, what each trust relationship does and does not imply, and the residual knowledge each party holds.

---

## Known gaps this area closes

Each row is a cross-component defect recorded in
[`../../COMPONENT-ALIGNMENT.md`](../../COMPONENT-ALIGNMENT.md). The ID is the cross-spec open-item
identifier proposed there; it is not yet assigned by a published register.

| ID | Gap | Severity |
|---|---|---|
| `CORE-O-08` | Trust-anchor mismatch: the Authority's anchor is a root public key pinned out of band (`amk-authority/01-attestation-format.md:63`), but the console pins an Authority *URL* and never the root key (`amk-console/01-keys-authentication.md:31`). | **Blocking** |
| `—` | The controller's trust in a runtime subtree while provenance binding is open (`CTR-5:3`, `E-CTR-08`). | Medium |

---

## Cross-references

- [`trust-bootstrap`](../trust-bootstrap/README.md)
- [`authority-model`](../authority-model/README.md)
- [`invariants`](../invariants/README.md)

---

*Part of the `abstract-kernel` area of the OS/10 Agentic Micro Kernel. The suite invariant register is at
[`../invariants/README.md`](../invariants/README.md).*
