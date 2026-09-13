# State Design

## State Machines

### 1. User Management

#### User

States: `ACTIVE`, `INACTIVE`, `SUSPENDED`.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> INACTIVE: Deactivate account
    INACTIVE --> ACTIVE: Reactivate account

    ACTIVE --> SUSPENDED: Suspend
    INACTIVE --> SUSPENDED: Suspend

    SUSPENDED --> ACTIVE: Reinstate
    SUSPENDED --> INACTIVE: Reinstate as inactive
```

### 2. Store

#### Store

States: `DRAFT`, `PENDING_REVIEW`, `ACTIVE`, `INACTIVE`, `SUSPENDED`, `CLOSED`.

```mermaid
stateDiagram-v2
    [*] --> DRAFT

    DRAFT --> PENDING_REVIEW: Submit for review
    PENDING_REVIEW --> ACTIVE: Approve
    PENDING_REVIEW --> DRAFT: Reject

    ACTIVE --> INACTIVE: Deactivate
    INACTIVE --> ACTIVE: Reactivate

    ACTIVE --> SUSPENDED: Suspend
    INACTIVE --> SUSPENDED: Suspend
    PENDING_REVIEW --> SUSPENDED: Suspend

    SUSPENDED --> ACTIVE: Reinstate and activate
    SUSPENDED --> INACTIVE: Reinstate without activation
    SUSPENDED --> PENDING_REVIEW: Resume review

    DRAFT --> CLOSED: Close
    PENDING_REVIEW --> CLOSED: Close
    ACTIVE --> CLOSED: Close
    INACTIVE --> CLOSED: Close
    SUSPENDED --> CLOSED: Close

    CLOSED --> [*]
```

#### Store Membership

States: `ACTIVE`, `SUSPENDED`, `REMOVED`.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> SUSPENDED: Suspend
    SUSPENDED --> ACTIVE: Reinstate

    ACTIVE --> REMOVED: Remove
    SUSPENDED --> REMOVED: Remove

    REMOVED --> [*]
```

### 3. Tenant

#### Tenant

States: `ACTIVE`, `INACTIVE`, `SUSPENDED`, `CLOSED`.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> INACTIVE: Deactivate
    INACTIVE --> ACTIVE: Reactivate

    ACTIVE --> SUSPENDED: Suspend
    INACTIVE --> SUSPENDED: Suspend

    SUSPENDED --> ACTIVE: Reinstate and activate
    SUSPENDED --> INACTIVE: Reinstate without activation

    ACTIVE --> CLOSED: Close
    INACTIVE --> CLOSED: Close
    SUSPENDED --> CLOSED: Close

    CLOSED --> [*]
```

#### Tenant Membership

States: `ACTIVE`, `SUSPENDED`, `REMOVED`.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> SUSPENDED: Suspend
    SUSPENDED --> ACTIVE: Reinstate

    ACTIVE --> REMOVED: Remove
    SUSPENDED --> REMOVED: Remove

    REMOVED --> [*]
```

#### Tenant Invitation

States: `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED`, `EXPIRED`.

```mermaid
stateDiagram-v2
    [*] --> PENDING

    PENDING --> ACCEPTED: Accept
    PENDING --> REJECTED: Reject
    PENDING --> CANCELLED: Cancel
    PENDING --> EXPIRED: Expire

    ACCEPTED --> [*]
    REJECTED --> [*]
    CANCELLED --> [*]
    EXPIRED --> [*]
```

#### Tenant Verification

States: `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`, `EXPIRED`.

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

### 4. Role

#### Role

States: `ACTIVE`, `ARCHIVED`.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE
    ACTIVE --> ARCHIVED: Archive
    ARCHIVED --> ACTIVE: Reactivate
```

### 5. Product

#### Product

States: `ACTIVE`, `DISCONTINUED`, `ARCHIVED`.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> DISCONTINUED: Discontinue
    DISCONTINUED --> ACTIVE: Resume selling

    ACTIVE --> ARCHIVED: Archive
    DISCONTINUED --> ARCHIVED: Archive

    ARCHIVED --> [*]
```

#### Product Category

States: `ACTIVE`, `ARCHIVED`.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE
    ACTIVE --> ARCHIVED: Archive
    ARCHIVED --> [*]
```

#### Product Version

States: `DRAFT`, `IN_REVIEW`, `REJECTED`, `PUBLISHED`, `SUPERSEDED`, `ARCHIVED`.

```mermaid
stateDiagram-v2
    [*] --> DRAFT

    DRAFT --> IN_REVIEW: Submit for review
    IN_REVIEW --> DRAFT: Withdraw
    IN_REVIEW --> REJECTED: Reject
    REJECTED --> DRAFT: Revise

    IN_REVIEW --> PUBLISHED: Approve and publish
    PUBLISHED --> SUPERSEDED: Publish a newer version

    DRAFT --> ARCHIVED: Discard
    REJECTED --> ARCHIVED: Archive
    SUPERSEDED --> ARCHIVED: Archive

    ARCHIVED --> [*]
```

#### Product Variant Version

States: `DRAFT`, `IN_REVIEW`, `REJECTED`, `PUBLISHED`, `SUPERSEDED`, `ARCHIVED`.

```mermaid
stateDiagram-v2
    [*] --> DRAFT

    DRAFT --> IN_REVIEW: Submit parent version for review
    IN_REVIEW --> DRAFT: Withdraw parent version
    IN_REVIEW --> REJECTED: Reject parent version
    REJECTED --> DRAFT: Revise parent version

    IN_REVIEW --> PUBLISHED: Publish parent version
    PUBLISHED --> SUPERSEDED: Publish newer parent version

    DRAFT --> ARCHIVED: Discard parent draft
    REJECTED --> ARCHIVED: Archive parent version
    SUPERSEDED --> ARCHIVED: Archive parent version

    ARCHIVED --> [*]
```

#### Product Variant

States: `ACTIVE`, `DISCONTINUED`, `ARCHIVED`.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> DISCONTINUED: Discontinue
    DISCONTINUED --> ACTIVE: Resume selling

    ACTIVE --> ARCHIVED: Archive
    DISCONTINUED --> ARCHIVED: Archive

    ARCHIVED --> [*]
```

#### Product Version Price

States: `SCHEDULED`, `ACTIVE`, `EXPIRED`, `CANCELLED`.

```mermaid
stateDiagram-v2
    [*] --> SCHEDULED: Schedule future price
    [*] --> ACTIVE: Activate currently effective price

    SCHEDULED --> ACTIVE: Activate scheduled price
    SCHEDULED --> EXPIRED: Expire unactivated price
    SCHEDULED --> CANCELLED: Cancel scheduled price
    SCHEDULED --> CANCELLED: Cancel price for retired parent version

    ACTIVE --> EXPIRED: Expire price at effective period end
    ACTIVE --> EXPIRED: Expire price for retired parent version
    ACTIVE --> CANCELLED: Cancel active price

    EXPIRED --> [*]
    CANCELLED --> [*]
```

### 6. Inventory

#### Inventory Item

States: `ACTIVE`, `INACTIVE`, `ARCHIVED`.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE

    ACTIVE --> INACTIVE: Disable new reservations
    INACTIVE --> ACTIVE: Enable new reservations

    ACTIVE --> ARCHIVED: Retire item with no stock or reservations
    INACTIVE --> ARCHIVED: Retire item with no stock or reservations

    ARCHIVED --> [*]
```

#### Inventory Reservation

States: `ACTIVE`, `CONSUMED`, `RELEASED`, `EXPIRED`.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE: Reserve inventory quantity

    ACTIVE --> CONSUMED: Consume reserved quantity
    ACTIVE --> RELEASED: Release reserved quantity
    ACTIVE --> EXPIRED: Expire reservation

    CONSUMED --> [*]
    RELEASED --> [*]
    EXPIRED --> [*]
```