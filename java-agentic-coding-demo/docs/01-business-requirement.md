# 01 — Business Requirement

> Produced by the requirement agent from the client's informal request.

## Business goal

The customer support team wants to triage incoming customer messages faster by automatically labelling each message with a topic category and an urgency level, so that high-impact issues reach the right team first.

## User story

> As a customer support team lead,
> I want incoming customer messages to be automatically classified by category and priority,
> so that my team can respond to the most important issues first without manual triage.

## In scope

- Accept a single customer message (a "ticket") with an id, a free-text message, and a customer id.
- Assign exactly one category per ticket: `BILLING`, `TECHNICAL`, `CONTRACT`, `COMPLAINT`, or `GENERAL`.
- Assign exactly one priority per ticket: `LOW`, `MEDIUM`, `HIGH`, or `CRITICAL`.
- Use simple, deterministic, keyword-based rules — no machine learning.
- Be runnable locally with a small demo entry point.

## Out of scope

- Persisting tickets to a database.
- Exposing an HTTP / REST API.
- Authentication, authorization, multi-tenancy.
- Internationalization beyond English keywords.
- Learning from past tickets or feedback loops.

## Constraints

- Java 21, Maven, JUnit 5.
- No Spring Boot, no external AI service, no third-party network calls.
- Classification must be deterministic and unit-testable.
- Code must pass the project's Checkstyle rules.

## Open assumptions

- Messages are in English.
- Customer ids are opaque strings supplied by the caller and are not validated against a directory.
- "Priority" is derived from the message content only — there is no SLA model or customer tier yet.
- A single category and a single priority per ticket is sufficient for the first iteration.
