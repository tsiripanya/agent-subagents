# java-agentic-coding-demo

A small Java 21 / Maven / JUnit 5 demo project that shows how to use GitHub Copilot inside a **controlled, agentic coding workflow** — not as freeform autocomplete.

The business use case is intentionally small: a rule-based **customer support ticket classifier** that assigns a category and a priority to each incoming message.

## What this demo proves

> GitHub Copilot becomes more useful when we do not use it only as autocomplete, but guide it with clear instructions, role-based prompts, acceptance criteria, tests, coding standards, and review loops.

Every step in the workflow leaves a written artifact in git. Nothing is "in the model's head".

## The agentic workflow

```text
Client request
   ▼
[1] Requirement Agent  → docs/01-business-requirement.md
[2] Specification Agent→ docs/02-technical-specification.md
[3] Architecture Agent → docs/03-architecture-proposal.md
[4] Code Agent         → src/main/java/com/demo/support/
[5] Test Agent         → src/test/java/com/demo/support/
[6] Review Agent       → docs/04-code-review-report.md
[7] Improvement loop   → code agent applies the review
```

The role prompts live under [`agents/`](agents/). Each one names its inputs, its outputs, and the rules it must respect.

## Project layout

```text
java-agentic-coding-demo/
├── README.md
├── pom.xml
├── config/
│   └── checkstyle.xml
├── agents/                     # role prompts for the agentic workflow
├── docs/                       # versioned artifacts produced by each agent
└── src/
    ├── main/java/com/demo/support/
    │   ├── Ticket.java
    │   ├── TicketCategory.java
    │   ├── TicketPriority.java
    │   ├── TicketClassificationResult.java
    │   ├── TicketClassifier.java
    │   └── DemoApplication.java
    └── test/java/com/demo/support/
        └── TicketClassifierTest.java
```

## Quality gates

Before any change is considered done:

```bash
mvn clean test
mvn checkstyle:check
```

Checkstyle is configured in [`config/checkstyle.xml`](config/checkstyle.xml) and enforces the rules listed in the repository's `.github/copilot-instructions.md` (no wildcard imports, no `System.out`, no parameter reassignment, capped nesting, etc.).

## Run the demo

### Run the tests

```bash
mvn clean test
```

### Run Checkstyle

```bash
mvn checkstyle:check
```

### Run the application

```bash
mvn exec:java -Dexec.mainClass="com.demo.support.DemoApplication"
```

Or, to keep Maven output quiet while still seeing application logs:

```bash
mvn -q compile dependency:build-classpath -Dmdep.outputFile=cp.txt && java -cp "target/classes:$(cat cp.txt)" com.demo.support.DemoApplication
```

`DemoApplication` runs the classifier against five sample tickets and logs the results through SLF4J.

> **Note:** Avoid `mvn -q exec:java` — the `-q` flag can suppress SLF4J output, making it appear as if the application produced no results.

## Honest limitations

- This is a **controlled demo**, not an autonomous platform. The "agents" are role prompts plus quality gates, executed by a human-driven Copilot session.
- Keyword rules are intentionally simple — they exist to be explainable and unit-testable, not to be state-of-the-art triage.
- English-only matching, single-label output, no persistence, no API layer. Documented under "Trade-offs" in [`docs/03-architecture-proposal.md`](docs/03-architecture-proposal.md).

## Further reading

- [`docs/05-demo-walkthrough.md`](docs/05-demo-walkthrough.md) — the 5-minute client walkthrough.
- [`agents/`](agents/) — the role prompts used in each step.
- [`docs/02-technical-specification.md`](docs/02-technical-specification.md) — the contract that both the implementation and the tests are built from.
