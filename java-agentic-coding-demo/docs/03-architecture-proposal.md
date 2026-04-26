# 03 — Architecture Proposal

> Produced by the architecture agent from `02-technical-specification.md`.

## Package structure

```text
com.demo.support
├── Ticket.java                         (record)
├── TicketCategory.java                 (enum)
├── TicketPriority.java                 (enum)
├── TicketClassificationResult.java     (record)
├── TicketClassifier.java               (rule-based classifier)
└── DemoApplication.java                (local demo entry point)
```

One package, six types. No sub-packages — the domain is small and splitting it would obscure the demo.

## Classes and responsibilities

- **`Ticket`** — immutable record holding `id`, `customerId`, and `message`. Carries no behavior beyond access.
- **`TicketCategory`** — fixed enum of business categories. Order in the enum is *not* the matching order; matching order lives in the classifier.
- **`TicketPriority`** — fixed enum of urgency levels.
- **`TicketClassificationResult`** — immutable record returned by the classifier with `ticketId`, `category`, `priority`, `reason`, `confidence`.
- **`TicketClassifier`** — the only class with logic. Holds keyword lists as `private static final` constants and exposes one public method, `classify(Ticket)`. Validates inputs at the boundary and produces a deterministic result.
- **`DemoApplication`** — minimal `main` for manual demonstration only. Logs a handful of sample classifications via SLF4J. Not part of the production API.

## Data model

- Records (`Ticket`, `TicketClassificationResult`) — the domain values are immutable and deserve no equality / setter boilerplate.
- Enums (`TicketCategory`, `TicketPriority`) — closed set of categories and priorities, easy to extend, exhaustive in `switch`.

No interfaces are introduced. There is only one classifier; an interface would be premature abstraction.

## Public API

The smallest surface that callers depend on:

```java
TicketClassifier classifier = new TicketClassifier();
TicketClassificationResult result = classifier.classify(ticket);
```

Everything else (keyword tables, scoring helpers) is package-private or `private`.

## Determinism notes

- All keyword tables are declared as `private static final` ordered structures.
- Matching is pure: it depends only on the input `Ticket`, not on time, randomness, or external state.
- The `confidence` formula is a fixed ratio with a constant denominator, so it is reproducible.
- Logging happens on a separate side channel and never influences the returned result.

## Trade-offs

- **No machine learning.** Keyword rules are intentionally chosen for explainability and unit-testability. The cost is recall on paraphrased messages.
- **No persistence / no API layer.** Out of scope for this iteration; the classifier is a pure function and can later be wrapped by any transport.
- **English-only keywords.** Documented as an open assumption; localization would require keyword tables per language.
- **Single-label output.** Real systems often want top-k categories with scores; for this demo, one label per axis keeps the contract simple.
- **`String.contains` matching.** Cheap and predictable but does not handle word boundaries (e.g. `"billion"` would match `"bill"`). Accepted for the demo and called out in the review.
