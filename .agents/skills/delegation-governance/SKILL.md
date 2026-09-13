---
name: delegation-governance
description: Classify Marketplace work by delegation tier, preserve human ownership of architecture and other protected decisions, define manual zones, and produce a risk-based implementation and review contract. Use when planning, delegating, implementing, or reviewing work that needs explicit decision boundaries, a do-not-decide list, learning-value protection, or a formal delegation handoff.
---

# Delegation Governance

Protect the project's educational and architectural value while still delegating repetitive implementation efficiently.
Apply the repository's `Delegation and Human Ownership` rules throughout the workflow.

## Workflow

### 1. Establish the decision baseline

Read the request, `AGENTS.md`, the relevant feature specification, API or event contracts, architecture decision records, and existing implementation patterns.
Treat a decision as approved only when the user states it explicitly or an approved project artifact records it.
Do not treat silence, generated code, or an accidental existing pattern as approval for a protected decision.

### 2. Classify the work

Assign every material work item to one tier:

- Tier 1 - human-owned decision work.
  Limit agent activity to evidence gathering, critique, options, tradeoffs, recommendations, and failure analysis until the decision is approved.
- Tier 2 - shared implementation work.
  Implement only after its dependent Tier 1 decisions, invariants, and boundaries are fixed.
- Tier 3 - delegatable mechanical work.
  Implement autonomously within the approved boundaries and validate the result.

Use a compact table when the classification is part of the deliverable:

| Work item | Tier | Established decision or constraint | Agent action | Human review |
|---|---:|---|---|---|
| Item | 1, 2, or 3 | Source of authority | Critique, implement, or wait | Required judgment |

### 3. Define the delegation contract

Before changing Tier 2 or mixed-tier work, state or derive:

- objective
- approved inputs and decisions
- constraints and invariants
- manual or learning zones
- do-not-decide list
- definition of done
- required validation
- stopping and approval points

Keep the contract proportional to the task.
Do not add ceremony to a clearly bounded Tier 3 change.

### 4. Handle unresolved protected decisions

Pause only work that depends on an unresolved Tier 1 decision.
Present two or three viable options when alternatives exist, explain their tradeoffs, recommend one, and ask a focused question.
Continue independent in-scope work that does not depend on the decision.
Never encode the recommendation as implementation before approval.

### 5. Implement within the boundary

For high-risk or important work, follow this order:

1. Inspect the human or approved-artifact design.
2. Critique it for failure modes, security, consistency, and operational risk.
3. Confirm that protected decisions are resolved.
4. Implement the bounded Tier 2 and Tier 3 work.
5. Run the narrowest relevant validation, then all affected checks.
6. Review the final diff by risk.

Preserve the first meaningful implementation of a user-designated learning concept as a manual zone.
After an established example exists, delegate analogous repetition unless the user says otherwise.

### 6. Review by risk

Apply extremely careful review to authentication, authorization, tenant isolation, transactions, concurrency, migrations, payments, public contracts, secrets, production delivery, and destructive infrastructure behavior.
Apply moderate review to business services, repository queries, caching, adapter behavior, and integration tests.
Apply light review to DTOs, mappers, repetitive fixtures, formatting, documentation, and routine configuration.

Do not infer Oracle compatibility from H2-only tests.
Do not claim a check passed without running it and inspecting its current result.

### 7. Produce the handoff

Report:

1. Files changed
2. Behavior changed
3. Assumptions made
4. Architectural decisions used or proposed
5. Tests added and validation run
6. Risks and unverified items
7. Items requiring human review

State `None` for an empty category when its absence matters.
Explain enough that the user can understand and debug the changed critical path without relying on the agent.
