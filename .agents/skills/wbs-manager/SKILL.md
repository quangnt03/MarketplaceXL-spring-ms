---
name: wbs-manager
description: Create and manage Linear issues for new feature planning from a WBS. Use when Codex needs to turn a feature request, PRD, product idea, or implementation plan into a Linear-ready work breakdown containing scope, user stories, acceptance criteria, decision-flow flowcharts, cross-component sequence diagrams, API contracts, ERD changes, state changes, and test cases; also use when updating an existing Linear feature issue set to match that WBS.
---

# WBS Manager

## Overview

Use this skill to convert a new feature into a structured work breakdown and create or update the corresponding Linear issues. It coordinates product planning details with Linear issue management, keeping the WBS consistent across parent and child issues.

For the issue body template and section-specific guidance, read `references/wbs-linear-template.md` before drafting or creating issues.

## Workflow

1. Clarify the target Linear context: team, project, existing parent issue if any, desired priority, labels, assignee, cycle, due date, and whether to create issues now or draft them first.
2. Read Linear context before writing: teams/projects/statuses/labels, plus any existing feature issue set that may need updating.
3. Gather or infer the feature context: user goal, affected surfaces, components, APIs, data model, states, edge cases, and test expectations.
4. Produce the WBS using the nine required sections:
   - Scope
   - User stories
   - Acceptance criteria
   - Flowchart for decision-heavy flows
   - Sequence diagram for cross-component flows
   - API contract
   - ERD changes
   - State changes
   - Test cases
5. Choose issue granularity:
   - Default to one parent feature issue plus one child issue per WBS section.
   - Split user stories, API work, schema work, or test work into additional child issues when they are independently deliverable.
   - Combine sections only for very small features, and state the reason in the summary.
6. Draft titles, descriptions, dependencies, labels, and acceptance/done checks before making Linear writes.
7. Use the Linear skill workflow and Linear tools for live operations: read first, then create or update issues in logical batches.
8. Summarize the created or updated Linear issue keys, remaining assumptions, and recommended next actions.

## Linear Write Rules

- Do not create live Linear issues until the target team is known.
- If the user has not clearly asked for live creation, present a draft WBS issue set first and ask before writing.
- Preserve existing issue content unless the user asked for replacement; append or update the WBS sections surgically.
- Prefer existing labels/statuses/projects over creating new ones. Create labels only when the user requests them or no suitable label exists.
- For bulk creation, create the parent issue first, then section issues, then add cross-links or dependency notes.

## Quality Bar

- Make each issue actionable: include deliverables, acceptance/done checks, dependencies, and open questions.
- Keep diagrams in Mermaid code fences when they belong in issue descriptions.
- Mark unknown requirements as assumptions or open questions instead of silently inventing product decisions.
- Ensure API, ERD, state, and test issues reference the user stories and acceptance criteria they support.
- Call out sequencing: discovery/design issues should precede implementation and tests when details are missing.
