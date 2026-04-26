# 05 — Demo Walkthrough

This walkthrough shows how the agentic workflow was applied end-to-end on a small but realistic Java use case.

## The use case in one paragraph

A customer support team wants tickets to be auto-labelled with a **category** (`BILLING`, `TECHNICAL`, `CONTRACT`, `COMPLAINT`, `GENERAL`) and a **priority** (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) based on simple, deterministic keyword rules.

## The pipeline

```text
Client request
   │
   ▼
[1] Requirement Agent ──▶ docs/01-business-requirement.md
   │
   ▼
[2] Specification Agent ──▶ docs/02-technical-specification.md
   │
   ▼
[3] Architecture Agent ──▶ docs/03-architecture-proposal.md
   │
   ▼
[4] Code Agent ──▶ src/main/java/com/demo/support/*.java
   │
   ▼
[5] Test Agent ──▶ src/test/java/com/demo/support/TicketClassifierTest.java
   │
   ▼
[6] Review Agent ──▶ docs/04-code-review-report.md
   │
   ▼
[7] Improvement loop (code agent applies feedback)
```

Each agent has a role prompt under `agents/`. Each prompt names its inputs, its outputs, and the rules it must respect. None of the agents are allowed to invent scope.

## Why this is different from "vibe coding"

| Aspect | Vibe coding with autocomplete | This controlled workflow |
|--------|-------------------------------|--------------------------|
| Source of truth | Whatever the model last produced | Versioned spec (`docs/02-...`) |
| Tests | Often skipped or generated from the code | Written from the **spec**, not the code |
| Code review | Implicit / human-only | Explicit `06-review-agent.md` with a written report |
| Standards | Implicit | Enforced by Checkstyle (`config/checkstyle.xml`) |
| Reproducibility | Low — depends on model state | High — every artifact is a file in git |

## Running it locally

```bash
cd java-agentic-coding-demo
mvn -q -DskipTests=false test
mvn -q checkstyle:check
mvn -q exec:java -Dexec.mainClass="com.demo.support.DemoApplication"
```

The first two commands are the **quality gates** referenced by the agents. The third one runs the local demo and logs five sample classifications via SLF4J.

## What to demo to the client (5 minutes)

1. Show `agents/01-requirement-agent.md` and `docs/01-business-requirement.md` side by side — same wording style, no scope creep.
2. Show `docs/02-technical-specification.md` — every rule is explicit, every acceptance criterion is testable.
3. Show `TicketClassifier.java` — small, deterministic, no surprises.
4. Show `TicketClassifierTest.java` — names read like requirements, not like implementation details.
5. Show `docs/04-code-review-report.md` — explicit alignment with the spec and a list of trade-offs.
6. Run the validation commands. Both pass.

## Honest limitations

- This is a **controlled demo**, not a fully autonomous platform. The "agents" here are role prompts plus quality gates — not running services.
- Keyword rules are intentionally simple. Real triage typically needs ML, multilingual handling, and SLA-aware priority.
- All seven steps are still driven by a human operator. The value is in the structure, not in autonomy.
