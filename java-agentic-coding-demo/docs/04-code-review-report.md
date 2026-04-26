# 04 — Code Review Report

> Produced by the review agent against the spec, the implementation, and the tests.

## 1. Specification alignment

All 12 acceptance criteria in `02-technical-specification.md` are mapped to passing tests in `TicketClassifierTest`:

| Criterion | Covered by test |
|-----------|-----------------|
| 1 (BILLING + CRITICAL on refund/urgent) | `shouldClassifyAsBillingCriticalWhenMessageMentionsRefundAndUrgent` |
| 2 (TECHNICAL + LOW on login broken) | `shouldClassifyAsTechnicalLowWhenMessageMentionsLoginBroken` |
| 3 (CONTRACT + LOW on cancel contract) | `shouldClassifyAsContractLowWhenMessageMentionsCancelContract` |
| 4 (COMPLAINT + LOW on terrible/disappointed) | `shouldClassifyAsComplaintLowWhenMessageMentionsTerribleAndDisappointed` |
| 5 (GENERAL + LOW + confidence 0) | `shouldClassifyAsGeneralLowWithZeroConfidenceWhenNoKeywordsMatch` |
| 6 (null/blank message defaults) | `shouldDefaultToGeneralLowWhenMessageIsNull`, `shouldDefaultToGeneralLowWhenMessageIsBlank` |
| 7 (null ticket → IAE) | `shouldThrowWhenTicketIsNull` |
| 8 (blank id/customerId → IAE) | `shouldThrowWhenTicketIdIsBlank`, `shouldThrowWhenCustomerIdIsNull` |
| 9 (message > 10 000 chars → IAE) | `shouldThrowWhenMessageExceedsMaxLength` |
| 10 (case-insensitive matching) | `shouldMatchKeywordsCaseInsensitively` |
| 11 (BILLING wins over TECHNICAL) | `shouldPreferBillingOverTechnicalWhenBothMatch` |
| 12 (CRITICAL wins over HIGH) | `shouldPreferCriticalOverHighWhenBothMatch` |

No specification gaps were found.

## 2. Test coverage gaps

- The spec calls for `confidence ∈ [0, 1]` on every result; covered by `shouldProduceConfidenceWithinValidRangeForEveryResult`.
- `ticketId` echoing is covered by `shouldReturnTicketIdEqualToInputId`.
- One nice-to-have not yet covered: a test asserting that the `reason` string contains the actually matched keyword (currently only the `"no message provided"` reason is asserted by string equality). Marked as non-blocking.

## 3. Checkstyle and standards

- All production code uses SLF4J, no `System.out` / `System.err`.
- Imports follow the configured group order.
- No magic `double` / `float` literals other than the allow-list (`0`, `0.5`, `1`, `100`).
- No reassigned parameters, no nested-if depth violations.
- `DemoApplication` declares a private constructor and a documented `main`, exempted from `UncommentedMain` via the Checkstyle excluded-classes pattern.

`mvn checkstyle:check` is expected to pass.

## 4. Maintainability

- `TicketClassifier` is the only class with logic and is under ~180 lines including helper records.
- Inner records (`CategoryRule`, `PriorityRule`, `CategoryMatch`, `PriorityMatch`) are private — they keep keyword tables type-safe without leaking into the public API.
- The keyword tables are declared in the same order as the specification, so a reader can compare line-by-line.

## 5. Risk assessment

- **Substring matching** (`String.contains`) does not respect word boundaries — `"billion"` would match the keyword `"bill"`. Acceptable for the demo, called out in the architecture trade-offs.
- **English-only keywords.** Multilingual content will fall back to `GENERAL` / `LOW`. Acceptable for the demo scope.
- **No persistence / replay.** A failing classification cannot be reproduced from logs alone unless the original message is captured upstream.

None of these are blocking for the demo.

## 6. Recommended changes

1. *(nice-to-have)* Add a test asserting the `reason` includes the matched keyword, e.g. that classifying `"please refund"` produces a reason containing `refund`.
2. *(nice-to-have)* Replace `String.contains` with a small word-boundary helper if the demo evolves toward production use.

Both items are deferred — the current implementation satisfies the specification.
