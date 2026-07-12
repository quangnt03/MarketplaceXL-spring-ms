# Review Checklists

Load only the sections that match the codebase and review scope.

## API And Backend

- Validate request parsing, schema evolution, and backwards compatibility.
- Check authn/authz on every route, job, webhook, and internal admin operation.
- Verify tenant, user, organization, and ownership filters are applied at query time.
- Check error handling for fail-open behavior, swallowed exceptions, and noisy retries.
- Inspect timeouts, cancellation, backpressure, retry limits, and idempotency keys.
- Confirm pagination, sorting, filtering, and limits are deterministic and bounded.
- Verify migrations, default values, indexes, nullability, and rollback assumptions.
- Check secrets and tokens are never logged, returned, committed, or embedded in generated artifacts.

## Frontend

- Validate loading, empty, error, disabled, optimistic, and permission-denied states.
- Check form validation, server error display, duplicate submission, and stale data handling.
- Inspect accessibility basics: labels, keyboard navigation, focus management, contrast, and semantic controls.
- Confirm responsive layouts avoid clipping, overlap, hidden actions, and layout shift.
- Trace user-visible state to source of truth; avoid divergent local copies when server state can change.
- Check XSS surfaces: rendered HTML, markdown, URLs, filenames, rich text, and third-party embeds.

## Data And ML

- Check train/test leakage, prompt/data leakage, non-deterministic evaluation, and metric mismatch.
- Validate parsing and normalization of model outputs before scoring or persistence.
- Confirm timeouts, retries, and fallback paths fit latency budgets.
- Check numerical precision, unit conversions, locale/date parsing, and scientific notation handling.
- Verify evaluation scripts reconstruct production inputs and outputs faithfully.

## Security

- Trace untrusted input into shell commands, SQL/NoSQL queries, path access, templates, logs, and browser rendering.
- Check path traversal, SSRF, command injection, unsafe deserialization, and arbitrary file writes.
- Validate CORS, cookies, CSRF, session expiry, token scope, and privilege escalation paths.
- Confirm dependencies, build scripts, and CI secrets are not exposed to untrusted code paths.

## Concurrency And Reliability

- Check shared mutable state, caches, locks, race windows, and duplicate job execution.
- Verify crash recovery, partial writes, cleanup, and consistency after cancellation.
- Confirm observability covers failures that users or operators need to diagnose.
- Look for unbounded memory growth, file descriptor leaks, process leaks, and queue buildup.

## Tests

- Prefer focused tests that prove the risky behavior, not broad snapshots.
- Add regression tests for fixed bugs when implementing changes.
- Check tests cover error paths, authorization boundaries, malformed input, and edge values.
- Report skipped checks with the exact blocker: missing dependency, service unavailable, credentials absent, or time cost.
