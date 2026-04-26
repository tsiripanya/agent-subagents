# 02 — Technical Specification

> Produced by the specification agent from `01-business-requirement.md`.
> This document is the contract for both the implementation and the tests.

## 1. Inputs

A `Ticket` consists of:

| Field         | Type     | Required | Validation                                  |
|---------------|----------|----------|---------------------------------------------|
| `id`          | `String` | Yes      | Non-null, non-blank.                        |
| `customerId`  | `String` | Yes      | Non-null, non-blank.                        |
| `message`     | `String` | No       | May be `null`, blank, or up to 10 000 chars.|

The `Ticket` itself must not be `null`.

## 2. Outputs

A `TicketClassificationResult` consists of:

| Field        | Type                | Meaning                                                                 |
|--------------|---------------------|-------------------------------------------------------------------------|
| `ticketId`   | `String`            | Echoes the input ticket id.                                             |
| `category`   | `TicketCategory`    | Exactly one of `BILLING`, `TECHNICAL`, `CONTRACT`, `COMPLAINT`, `GENERAL`. |
| `priority`   | `TicketPriority`    | Exactly one of `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.                     |
| `reason`     | `String`            | Short, human-readable explanation referring to the matched keyword(s).  |
| `confidence` | `double` in `[0,1]` | Deterministic score derived from the number of matching keywords.       |

## 3. Classification rules

Matching is **case-insensitive** and operates on the whole message text. A keyword matches if it appears as a substring of the message after lower-casing.

### 3.1 Categories (evaluated in this order)

1. **BILLING** — `invoice`, `payment`, `charge`, `refund`, `billing`, `subscription`, `bill`
2. **TECHNICAL** — `error`, `bug`, `crash`, `broken`, `not working`, `slow`, `login`, `password`
3. **CONTRACT** — `contract`, `agreement`, `terms`, `renewal`, `cancel`
4. **COMPLAINT** — `complaint`, `unhappy`, `disappointed`, `terrible`, `bad service`, `angry`
5. **GENERAL** — fallback when no other category matches.

### 3.2 Priorities (evaluated in this order, most severe first)

1. **CRITICAL** — `outage`, `down`, `urgent`, `emergency`, `asap`, `critical`, `immediately`
2. **HIGH** — `important`, `blocked`, `blocking`, `escalate`, `high priority`
3. **MEDIUM** — `soon`, `please`, `needed`
4. **LOW** — fallback when no priority keyword matches.

### 3.3 Confidence

`confidence = min(1.0, matched_keywords / EXPECTED_MATCHES)` where `EXPECTED_MATCHES = 3`.
Matched keywords are counted across both the category and priority keyword sets that fired. When no keywords match (i.e. the result is `GENERAL` + `LOW`), `confidence = 0.0`.

## 4. Tie-breaking

- The first category that has at least one matching keyword wins, in the order above.
- The first priority that has at least one matching keyword wins, in the order above (so `CRITICAL` outranks `HIGH`, etc.).
- This ordering is deliberate and must be deterministic across runs.

## 5. Validation and error handling

- A `null` `Ticket` causes the public API to throw `IllegalArgumentException` with a clear message.
- A `Ticket` with `null` or blank `id` or `customerId` causes the public API to throw `IllegalArgumentException`.
- A `null` or blank `message` is **valid** and classifies as `GENERAL` / `LOW` with `reason = "no message provided"` and `confidence = 0.0`.
- A message longer than 10 000 characters causes the public API to throw `IllegalArgumentException`.

## 6. Acceptance criteria

1. **Given** a ticket whose message is `"Please refund my last invoice, it is urgent."`,
   **when** classified, **then** `category = BILLING`, `priority = CRITICAL`, `confidence > 0`.
2. **Given** a ticket whose message is `"My login is broken since this morning."`,
   **when** classified, **then** `category = TECHNICAL` and `priority = LOW`.
3. **Given** a ticket whose message is `"I want to cancel my contract."`,
   **when** classified, **then** `category = CONTRACT` and `priority = LOW`.
4. **Given** a ticket whose message is `"Terrible service, I am very disappointed!"`,
   **when** classified, **then** `category = COMPLAINT` and `priority = LOW`.
5. **Given** a ticket whose message is `"Hello, I have a quick question."`,
   **when** classified, **then** `category = GENERAL` and `priority = LOW` with `confidence = 0.0`.
6. **Given** a ticket whose message is `null` or blank,
   **when** classified, **then** `category = GENERAL`, `priority = LOW`, `reason = "no message provided"`.
7. **Given** a `null` ticket,
   **when** classified, **then** the call throws `IllegalArgumentException`.
8. **Given** a ticket with blank `id` or `customerId`,
   **when** classified, **then** the call throws `IllegalArgumentException`.
9. **Given** a ticket whose message exceeds 10 000 characters,
   **when** classified, **then** the call throws `IllegalArgumentException`.
10. **Given** a message that contains `"Invoice"` (mixed case),
    **when** classified, **then** matching is case-insensitive and `category = BILLING`.
11. **Given** a message that matches both BILLING and TECHNICAL keywords,
    **when** classified, **then** `category = BILLING` (declared order wins).
12. **Given** a message that matches both `urgent` and `important`,
    **when** classified, **then** `priority = CRITICAL` (more severe wins).

## 7. Test scenarios (for the test agent)

- Happy path for each category (one test each).
- Happy path for each priority (one test each).
- Case-insensitive matching.
- Multiple category matches → declared order wins.
- Multiple priority matches → most severe wins.
- Null message → `GENERAL` / `LOW`, reason = `"no message provided"`.
- Blank message → same as null message.
- Null ticket → `IllegalArgumentException`.
- Blank ticket id → `IllegalArgumentException`.
- Blank customer id → `IllegalArgumentException`.
- Message longer than 10 000 chars → `IllegalArgumentException`.
- `confidence` is in `[0, 1]` for every produced result.
- `confidence = 0.0` when no keywords match.
- Result `ticketId` always equals input `ticket.id()`.
