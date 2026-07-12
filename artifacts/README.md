# Feature Delivery Workflow

Use this workflow for every Marketplace MVP feature. The repository and
`docs/md` remain the source of truth. A feature must stay within the MVP scope
and follow `AGENTS.md`.

## Artifacts

For a feature named `<feature-slug>`, use:

```text
docs/artifacts/
├── README.md
├── templates/
│   ├── feature-intake.md
│   └── feature-spec.md
└── <feature-slug>/
    ├── intake.md
    └── specification.md
```

- `intake.md` is filled in by the user from `templates/feature-intake.md`.
- `specification.md` is created by the agent from `templates/feature-spec.md`.
- Automated tests live beside the code in the repository's normal test
  directories; they are not stored under `artifacts/`.

## Work Structure

### 1. Prepare the branch — user

The user creates and checks out a branch manually. Recommended name:
`feature/<feature-slug>`.

Before the agent changes code, the user confirms that the intended branch is
checked out. The agent may inspect branch and working-tree status, but does not
create or switch branches unless explicitly asked.

**Exit gate:** the feature branch is active and existing unrelated changes are
identified.

### 2. Define the outcome — user with agent support

The agent copies `templates/feature-intake.md` to
`<feature-slug>/intake.md`. The user fills in the unknown product decisions,
especially success criteria, the main flow, and extreme cases. The agent may
suggest missing cases, but must mark suggestions as assumptions until the user
accepts them.

Extreme cases include boundary values, invalid input, empty states, duplicate
requests, concurrency, authorization, tenant isolation, dependency failures,
timeouts, retries, and partial transaction failure where relevant.

**Exit gate:** the goal, scope, measurable success criteria, main flow, and
critical extreme cases are unambiguous. Open questions that affect behavior are
resolved.

### 3. Specify the feature — agent, reviewed by user

The agent reads `AGENTS.md`, the intake, and the relevant files in `docs/md`,
then creates `<feature-slug>/specification.md` using
`templates/feature-spec.md`.

The specification traces requirements through acceptance criteria, flows,
contracts, persistence/state changes, security, observability, and tests. Use
Mermaid diagrams only when branching or cross-component behavior benefits from
them.

**Exit gate:** the user approves the specification, or explicitly authorizes
the agent to proceed on documented assumptions. Every acceptance criterion has
planned test coverage.

### 4. Write tests first — agent

The agent implements the relevant test layers before production behavior:

1. **Unit tests** for isolated business rules, validation, calculations, and
   state transitions.
2. **Integration tests** for Spring/JPA/Oracle behavior, transactions, tenant
   isolation, RBAC, API boundaries, and external-adapter behavior.
3. **End-to-end tests** for critical user journeys across frontend and backend.
4. **Smoke tests** for startup, health, and one minimal critical path suitable
   for local or deployed verification.

Not every acceptance criterion needs all four layers. The specification must
state which layer owns each criterion and why a layer is not applicable. Tests
must initially fail for the missing behavior or otherwise demonstrate that they
detect the intended regression.

**Exit gate:** test names trace to acceptance-criteria IDs, required fixtures
are defined, tests compile, and new behavioral tests fail for the expected
reason rather than setup errors.

### 5. Implement and refine — agent and user

The agent implements the smallest vertical slice that makes the approved tests
pass. The user supplies product decisions, reviews behavior, and performs any
manual validation that requires subjective judgment. If implementation exposes
a requirement gap, update the intake/specification and tests before continuing.

Recommended order:

1. Migration and persistence model, if needed.
2. Backend domain behavior, authorization, and API DTOs.
3. API contract updates.
4. Frontend typed client and user-facing flow, if needed.
5. Error handling and observability.
6. Full test suite and smoke verification.

**Done gate:** all applicable checks in `AGENTS.md` pass; tests are green;
authorization, tenant isolation, errors, and migrations are covered; the
frontend calls the API for user-facing behavior; and no out-of-scope feature
was added.

## Change Control

- A changed requirement updates `intake.md`, `specification.md`, acceptance
  criteria, and tests in that order.
- Unknowns are recorded as open questions; assumptions are never presented as
  confirmed requirements.
- Any scope expansion requires explicit user approval.
- Implementation discoveries and intentional deviations are recorded in the
  specification's decision log.

