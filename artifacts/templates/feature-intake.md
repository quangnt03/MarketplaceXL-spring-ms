# Feature Intake: <Feature Name>

> User: replace every `<...>` value. Write `N/A` when a section genuinely does
> not apply. Short answers are fine; the agent will turn this into a detailed
> specification.

## Identity

- **Feature name:** <name>
- **Feature slug:** <lowercase-kebab-case>
- **Branch checked out:** <branch name>
- **Requested by:** <name or role>
- **Target milestone:** <docs/md/06_implementation_roadmap.md milestone>

## Problem and Outcome

- **Who has the problem?** <BUYER, MERCHANT, ADMIN, public visitor, or system>
- **What problem should be solved?** <current pain or missing capability>
- **What outcome should the feature create?** <observable user/business result>
- **Why is it part of the Marketplace MVP?** <scope reference>

## Scope

- **In scope:** <behaviors and surfaces included>
- **Out of scope:** <explicit non-goals>
- **Affected areas:** <backend domain, API, database, frontend, integration>

## Success Criteria

Write measurable, testable outcomes.

1. <Given/When/Then or checkable outcome>
2. <Given/When/Then or checkable outcome>
3. <Given/When/Then or checkable outcome>

## Main Flow

1. <actor starts here>
2. <action or system decision>
3. <result>

## Extreme and Failure Cases

Fill the relevant rows. Add cases specific to the feature.

| Area | What can happen? | Expected behavior |
|---|---|---|
| Minimum/maximum/empty input | <case> | <result> |
| Invalid or stale input | <case> | <result> |
| Unauthenticated/wrong role | <case> | <status or UI behavior> |
| Wrong user or tenant | <case> | <status and data isolation> |
| Duplicate/retried request | <case> | <idempotent/conflict behavior> |
| Concurrent requests | <case> | <consistency behavior> |
| Dependency timeout/failure | <case> | <retry/failure behavior> |
| Partial transaction failure | <case> | <rollback/recovery behavior> |
| Existing/legacy data | <case> | <compatibility behavior> |

## Product and UX Decisions

- **Required UI states:** <loading, empty, success, validation, failure>
- **User-visible messages:** <important wording or tone>
- **Manual approval or subjective behavior:** <decision owner>

## Constraints

- **Security/privacy:** <requirements>
- **Performance/volume:** <expected limits>
- **Compatibility:** <API/data/browser constraints>
- **Rollout or migration:** <requirements>

## Open Questions

1. <question and decision owner>

## User Approval

- [ ] I confirm the goal and scope.
- [ ] I confirm the success criteria.
- [ ] I confirm the main flow and critical extreme cases.
- [ ] The agent may create the detailed feature specification.

