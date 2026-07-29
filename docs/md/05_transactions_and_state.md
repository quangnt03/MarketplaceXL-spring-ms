# Transactions and State Design

## Stateful entity
1. User management
- User (`ACTIVE/INACTIVE/SUSPENDED`)
```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> DEACTIVATED: Deactivate account
    DEACTIVATED --> ACTIVE: Reactivate account

    ACTIVE --> SUSPENDED: Suspend
    DEACTIVATED --> SUSPENDED: Suspend

    SUSPENDED --> ACTIVE: Reinstate
    SUSPENDED --> DEACTIVATED: Reinstate as deactivated
    SUSPENDED --> [*]
```

2. Store
- Store (`DRAFT/ACTIVE/INACTIVE/PENDING_REVIEW/SUSPENDED/CLOSED`)

```mermaid
stateDiagram-v2
    [*] --> DRAFT

    DRAFT --> PENDING_REVIEW: Submit for review
    PENDING_REVIEW --> ACTIVE: Approve
    PENDING_REVIEW --> DRAFT: Reject

    ACTIVE --> DEACTIVATED: Deactivate
    DEACTIVATED --> ACTIVE: Reactivate

    ACTIVE --> SUSPENDED: Suspend
    DEACTIVATED --> SUSPENDED: Suspend
    PENDING_REVIEW --> SUSPENDED: Suspend

    SUSPENDED --> ACTIVE: Reinstate
    SUSPENDED --> DEACTIVATED: Reinstate without activation

    DRAFT --> CLOSED: Close
    PENDING_REVIEW --> CLOSED: Close
    ACTIVE --> CLOSED: Close
    DEACTIVATED --> CLOSED: Close
    SUSPENDED --> CLOSED: Close

    CLOSED --> [*]
```

- Store Membership (`ACTIVE/SUSPENDED/REMOVED`)
```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> SUSPENDED: Suspend
    SUSPENDED --> ACTIVE: Reinstate

    ACTIVE --> REMOVED: Remove
    SUSPENDED --> REMOVED: Remove

    REMOVED --> [*]
```

3. Tenant

- Tenant (`ACTIVE/INACTIVE/SUSPENDED/CLOSED`)

```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> DEACTIVATED: Deactivate
    DEACTIVATED --> ACTIVE: Reactivate

    ACTIVE --> SUSPENDED: Suspend
    DEACTIVATED --> SUSPENDED: Suspend

    SUSPENDED --> ACTIVE: Reinstate
    SUSPENDED --> DEACTIVATED: Reinstate without activation

    ACTIVE --> CLOSED: Close
    DEACTIVATED --> CLOSED: Close
    SUSPENDED --> CLOSED: Close

    CLOSED --> [*]
```

- Tenant Membership (`ACTIVE/SUSPENDED/REMOVED`)

```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> SUSPENDED: Suspend
    SUSPENDED --> ACTIVE: Reinstate

    ACTIVE --> REMOVED: Remove
    SUSPENDED --> REMOVED: Remove

    REMOVED --> [*]
```

- TenantInvitation (`PENDING/APPROVED/REJECTED/CANCELLED/EXPIRED`)
```mermaid
stateDiagram-v2
    [*] --> PENDING

    PENDING --> APPROVED: Approve
    PENDING --> REJECTED: Reject
    PENDING --> CANCELLED: Cancel
    PENDING --> EXPIRED: Expire

    APPROVED --> [*]
    REJECTED --> [*]
    CANCELLED --> [*]
    EXPIRED --> [*]
```

- TenantVerification (`PENDING/APPROVED/CANCELLED/EXPIRED`)
```mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> APPROVED: Approve
    PENDING --> REJECTED: Reject
    PENDING --> CANCELLED: Cancel
    PENDING --> EXPIRED: Expire
    APPROVED --> [*]
    REJECTED --> [*]
    CANCELLED --> [*]
    EXPIRED --> [*]
```

4. Role
- Role (`ACTIVE/ARCHIVED`)
```mermaid
stateDiagram-v2
    [*] --> ACTIVE
    ACTIVE --> ARCHIVED: Archive
    ARCHIVED --> ACTIVE: Reactivate
```

5. Product
- Product (`ACTIVE/DISCONTINUED/ARCHIVED`)
```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> DISCONTINUED: Discontinue
    DISCONTINUED --> ACTIVE: Resume selling

    ACTIVE --> ARCHIVED: Archive
    DISCONTINUED --> ARCHIVED: Archive

    ARCHIVED --> [*]
```

- ProductCategory (`ACTIVE/ARCHIVED`)
```mermaid 
stateDiagram-v2
    [*] --> ACTIVE
    ACTIVE --> ARCHIVED 
    ARCHIVED --> [*]
```
- ProductVersion (`DRAFT/IN_REVIEW/ACTIVE/INACTIVE/ARCHIVED/SUSPENDED`)
```mermaid
stateDiagram-v2
    [*] --> DRAFT

    DRAFT --> IN_REVIEW: Submit
    IN_REVIEW --> DRAFT: Withdraw
    IN_REVIEW --> REJECTED: Reject
    REJECTED --> DRAFT: Revise

    IN_REVIEW --> PUBLISHED: Approve and publish
    PUBLISHED --> SUPERSEDED: Publish newer version

    DRAFT --> ARCHIVED: Discard
    REJECTED --> ARCHIVED: Archive
    SUPERSEDED --> ARCHIVED: Archive

    ARCHIVED --> [*]
```

- ProductVariant (`ACTIVE/DISCONTINUED/ARCHIVED`)
```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> DISCONTINUED: Discontinue
    DISCONTINUED --> ACTIVE: Resume selling

    ACTIVE --> ARCHIVED: Archive
    DISCONTINUED --> ARCHIVED: Archive

    ARCHIVED --> [*]
```
- ProductVersionPrice (`SCHEDULED/ACTIVE/EXPIRED/CANCELLED`)
```mermaid 
stateDiagram-v2
    [*] --> DRAFT
    DRAFT --> PENDING_REVIEW: Publish
    PENDING_REVIEW --> ACTIVE: Approve
    PENDING_REVIEW --> DRAFT: Disapprove
    ACTIVE --> INACTIVE: Unpublish
    ACTIVE --> INACTIVE: Expired
    ACTIVE --> DRAFT: Modify
    INACTIVE --> DRAFT: Modify/Republish
    ACTIVE --> SUSPENDED: Suspend
    PENDING_REVIEW --> SUSPENDED: Suspend
    INACTIVE --> SUSPENDED: Suspend
```