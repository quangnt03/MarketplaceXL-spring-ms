---
name: codebase-review
description: Review software codebases systematically for bugs, regressions, security risks, reliability gaps, test gaps, architecture drift, and maintainability issues. Use when Codex is asked to review a repository, pull request, branch, subsystem, implementation plan, or code change and produce actionable engineering findings with file/line evidence.
---

# Codebase Review

## Review Stance

Prioritize correctness, security, reliability, and user-visible behavior over style. Produce findings first, ordered by severity, with precise file and line references. Prefer a short, evidence-backed review over a broad inventory of minor concerns.

Do not rewrite unrelated code during a review unless the user explicitly asks for fixes. If fixes are requested, inspect first, then make scoped changes and verify them.

## Workflow

1. Establish scope.
   - Identify whether the review is for the whole repo, a PR/diff, a branch, a file set, or a subsystem.
   - Read repository instructions first: `AGENTS.md`, `README`, project docs (typically in the `docs/` directory), package manifests, CI config, and test conventions.
   - Check `git status` and avoid treating unrelated dirty files as review targets unless they affect the requested scope.

2. Build the execution map.
   - Find entrypoints, public APIs, background jobs, schemas, migrations, config, auth boundaries, persistence, and external integrations.
   - Trace high-risk paths from user input to side effects, storage, network calls, rendered output, or billing/security decisions.
   - Use `rg`/`rg --files` first for source discovery.

3. Inspect behavior, not just syntax.
   - Compare the implementation against declared contracts, tests, schemas, docs, and caller expectations.
   - Look for edge cases: empty input, malformed input, missing config, timeouts, retries, concurrency, partial failures, timezone/date handling, numeric precision, unit conversion, idempotency, and permission boundaries.
   - Check whether errors fail open, leak secrets, lose data, or hide useful diagnostics.

4. Verify with tools when practical.
   - Run focused tests, type checks, linters, or small repro scripts that match the review scope.
   - If full test suites are expensive or require unavailable services, run the narrowest meaningful checks and report what was not run.
   - Do not require live credentials or production services unless the user asked for that environment.

5. Report findings.
   - Lead with actionable issues. Each finding should include severity, location, impact, and a concrete fix direction.
   - Avoid listing style-only nits unless they create real risk or violate explicit project rules.
   - Include open questions only when they affect risk assessment.
   - End with tests run and residual risk.

## Severity

- `P0`: Data loss, auth bypass, secret exposure, remote code execution, production outage, or a release-blocking regression.
- `P1`: Likely user-visible bug, incorrect result, security weakness, broken contract, or missing migration that should block merge.
- `P2`: Edge-case bug, reliability issue, operational blind spot, significant maintainability hazard, or missing focused test for risky behavior.
- `P3`: Low-risk maintainability issue, clarity problem with future bug potential, or minor test/documentation gap.

Use the highest severity justified by evidence. When uncertain, explain the uncertainty and keep the severity conservative.

## Output Format

Use this structure for normal reviews:

```markdown
Findings
- [P1] Short imperative title
  `path/to/file.ext:line` - Explain the bug, when it happens, why it matters, and the smallest credible fix.

Open Questions
- Question that changes the risk assessment, if any.

Tests
- `command` - result or reason it could not be run.
```

If there are no findings, say so directly and still mention tests run and remaining risk.

For inline review comments in Codex desktop, use `::code-comment{...}` only for actionable line-specific issues. Keep inline comments tight and avoid duplicating the full final review.

## Deeper Checklists

For broad or high-stakes reviews, read `references/review-checklists.md` and apply only the sections relevant to the stack and scope.
