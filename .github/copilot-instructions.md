# GitHub Copilot Instructions for This Repository

You are working on a Java demo project that demonstrates a structured agentic coding workflow.

The goal is to show how GitHub Copilot can support a controlled software development lifecycle:

1. Requirement clarification
2. Technical specification
3. Architecture proposal
4. Java implementation
5. Independent test generation
6. Code review
7. Improvement loop

This repository should demonstrate controlled AI-assisted engineering, not uncontrolled vibe coding.

---

## General Rules

- Use Java 21.
- Use Maven.
- Use JUnit 5 for tests.
- Keep the project simple and easy to explain.
- Do not add Spring Boot unless explicitly requested.
- Do not add external APIs unless explicitly requested.
- Do not add unnecessary abstractions.
- Prefer readable code over clever code.
- Validate inputs clearly.
- Keep business logic deterministic and testable.
- Write tests that are understandable for business and technical reviewers.
- Keep changes focused on the requested task.
- Avoid unrelated refactoring.
- Do not introduce new dependencies unless explicitly requested.
- Mention assumptions clearly when something is ambiguous.
- Prefer controlled, step-by-step Copilot workflows over one-shot code generation.
- Do not overclaim what AI or agents can do.

---

## Required Validation Commands

Before considering a task complete, the following commands should pass:

```bash
./mvnw clean test
./mvnw checkstyle:check
```

If Maven wrapper is not available, use:

```bash
mvn clean test
mvn checkstyle:check
```

---

## Coding Style

- Use clear class names.
- Use immutable objects where practical.
- Use enums for fixed categories and priorities.
- Use small methods with clear responsibility.
- Avoid hidden side effects.
- Avoid global mutable state.
- Do not swallow exceptions silently.
- Error messages should be understandable.
- Prefer explicit, readable control flow.
- Prefer composition over unnecessary inheritance.
- Keep public APIs small and intentional.
- Do not over-engineer the demo.
- Prefer package-private visibility where public visibility is not required.
- Keep classes focused on one responsibility.

---

## Checkstyle Rules

This project enforces strict Checkstyle via:

```text
config/checkstyle.xml
```

Run:

```bash
./mvnw checkstyle:check
```

The build fails if Checkstyle rules are violated.

---

## Coding Rules Required by Checkstyle

### ParameterAssignment

Never reassign method parameters. Use a local variable instead.

Bad:

```java
void normalize(String value) {
    value = value.trim();
}
```

Good:

```java
void normalize(String value) {
    String normalizedValue = value.trim();
}
```

### DeclarationOrder

Use this order inside classes:

1. Static fields
2. Instance fields
3. Constructors
4. Methods

Within each group, use this visibility order:

1. public
2. protected
3. package-private
4. private

Example:

```java
public final class TicketClassifier {

    public static final String DEFAULT_LANGUAGE = "en";

    private static final Logger LOG = LoggerFactory.getLogger(TicketClassifier.class);
    private static final int MAX_REASON_LENGTH = 500;

    private final RuleSet ruleSet;

    public TicketClassifier(RuleSet ruleSet) {
        this.ruleSet = Objects.requireNonNull(ruleSet, "ruleSet must not be null");
    }

    public TicketClassificationResult classify(Ticket ticket) {
        return ruleSet.classify(ticket);
    }

    private String truncateReason(String reason) {
        if (reason == null || reason.length() <= MAX_REASON_LENGTH) {
            return reason;
        }

        return reason.substring(0, MAX_REASON_LENGTH) + "...";
    }
}
```

### ModifiedControlVariable

Do not modify loop control variables inside the loop body.

Bad:

```java
for (int i = 0; i < values.size(); i++) {
    i++;
}
```

Good:

```java
for (int i = 0; i < values.size(); i++) {
    process(values.get(i));
}
```

### MagicNumber

Do not use magic `float` or `double` literals except:

- `0`
- `0.5`
- `1`
- `100`

Use `private static final` constants for other float or double literals.

Good:

```java
private static final double DEFAULT_CONFIDENCE_THRESHOLD = 0.75;
```

### MissingSwitchDefault

Every `switch` statement or switch expression must have a `default` case.

Good:

```java
private String describePriority(TicketPriority priority) {
    return switch (priority) {
        case LOW -> "Low priority";
        case MEDIUM -> "Medium priority";
        case HIGH -> "High priority";
        case CRITICAL -> "Critical priority";
        default -> "Unknown priority";
    };
}
```

### SimplifyBooleanExpression

Do not compare booleans with `true` or `false`.

Bad:

```java
if (enabled == true) {
    run();
}
```

Good:

```java
if (enabled) {
    run();
}
```

### StringLiteralEquality

Never compare strings with `==`.

Bad:

```java
if (status == "ACTIVE") {
    activate();
}
```

Good:

```java
if ("ACTIVE".equals(status)) {
    activate();
}
```

### NestedIfDepth

Maximum nested `if` depth is 3.

Refactor deeply nested logic into helper methods.

Bad:

```java
if (ticket != null) {
    if (ticket.message() != null) {
        if (!ticket.message().isBlank()) {
            if (ticket.message().contains("urgent")) {
                handleUrgentTicket(ticket);
            }
        }
    }
}
```

Good:

```java
if (isUrgentTicket(ticket)) {
    handleUrgentTicket(ticket);
}

private boolean isUrgentTicket(Ticket ticket) {
    if (ticket == null || ticket.message() == null) {
        return false;
    }

    return ticket.message().contains("urgent");
}
```

### NestedForDepth

Maximum nested `for` depth is 3.

### NestedTryDepth

Maximum nested `try` depth is 2.

### IllegalCatch

Never catch `java.lang.Throwable`.

Prefer specific exception types.

Bad:

```java
catch (Throwable throwable) {
    LOG.error("Unexpected failure", throwable);
}
```

Good:

```java
catch (IOException exception) {
    LOG.error("Failed to read file {}", path, exception);
}
```

### ExplicitInitialization

Do not explicitly initialize fields to Java default values.

Bad:

```java
private String value = null;
private int count = 0;
private boolean active = false;
```

Good:

```java
private String value;
private int count;
private boolean active;
```

### MultipleVariableDeclarations

Declare one variable per line.

Bad:

```java
String firstName, lastName;
```

Good:

```java
String firstName;
String lastName;
```

### OneStatementPerLine

Use one statement per line.

Bad:

```java
start(); stop();
```

Good:

```java
start();
stop();
```

---

## Import Rules

### AvoidStarImport

Never use wildcard imports.

Bad:

```java
import java.util.*;
```

Good:

```java
import java.util.List;
import java.util.Map;
```

### ImportOrder

Use this import group order:

1. `java`
2. `javax` / `jakarta`
3. `net`
4. `org`
5. `com`
6. `io`
7. `software`

Sort imports alphabetically inside each group.

Use one blank line between groups.

Example:

```java
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.MyService;
```

### ImportControl

Only use packages allowed by:

```text
config/checkstyle-import-control.xml
```

Do not add imports from non-whitelisted packages.

### UnusedImports

Remove all unused imports.

### RedundantImport

Remove duplicate and redundant imports.

---

## Naming Rules

### ConstantName

Static final constants must use `UPPER_SNAKE_CASE`.

Good:

```java
private static final int MAX_RETRY_COUNT = 3;
```

### LocalVariableName

Local variables must use `camelCase`.

### MemberName

Instance fields must use `camelCase`.

### MethodName

Methods must use `camelCase`.

### AbstractClassName

Abstract classes must start with `Abstract`.

Good:

```java
public abstract class AbstractTicketProcessor {
}
```

---

## Formatting Rules

### FileTabCharacter

Do not use tabs.

Use spaces only.

Use 4-space indentation.

### NewlineAtEndOfFile

Every file must end with a newline character.

### RegexpSingleline

Do not leave trailing whitespace on any line.

### NeedBraces

Always use braces for:

- `if`
- `else`
- `for`
- `while`
- `do`

Bad:

```java
if (enabled)
    run();
```

Good:

```java
if (enabled) {
    run();
}
```

### WhitespaceAround

Use spaces around operators, keywords, and braces.

Good:

```java
if (count > 0) {
    total = total + count;
}
```

### LeftCurly / RightCurly

Opening braces stay on the same line.

Closing braces stay on their own line.

Good:

```java
public void run() {
    execute();
}
```

---

## Forbidden Patterns

### No System.out or System.err

Do not use:

```java
System.out.println(...)
System.err.println(...)
```

Use SLF4J logging instead.

Good:

```java
private static final Logger LOG = LoggerFactory.getLogger(MyClass.class);

LOG.info("Processed ticket with id {}", ticketId);
```

### No System.exit

Never call:

```java
System.exit(...)
```

### No System.gc

Never call:

```java
System.gc()
```

### UncommentedMain

Do not create `public static void main()` methods unless explicitly requested and clearly justified.

If a main method is necessary for a local demo, document it clearly:

```java
/**
 * Local demo entry point used only for manual demonstration.
 */
public static void main(String[] args) {
    DemoRunner runner = new DemoRunner();
    runner.run();
}
```

### HideUtilityClassConstructor

Utility classes containing only static methods must have a private constructor.

Good:

```java
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
```

---

## Class Design Rules

### InnerTypeLast

Declare inner classes and inner interfaces at the end of the class.

### InterfaceIsType

Interfaces must declare methods.

Do not create interfaces that only contain constants.

### ModifierOrder

Use standard Java modifier order:

```text
public protected private abstract default static final transient volatile synchronized native strictfp
```

Good:

```java
private static final Logger LOG = LoggerFactory.getLogger(MyClass.class);
```

---

## Error Handling Conventions

Use SLF4J logging.

Every class that logs should define:

```java
private static final Logger LOG = LoggerFactory.getLogger(ClassName.class);
```

Use `LOG.error(...)` for failures that need investigation.

Use `LOG.warn(...)` for recoverable or expected error paths.

Rules:

- Put exceptions as the last logging argument.
- Include useful context in log messages.
- Do not log sensitive data.
- Truncate large payloads before logging to prevent log flooding.
- Do not swallow exceptions silently.
- Prefer specific exception types.
- Use try-with-resources for closeable resources.
- Record metrics in `finally` blocks when timing or failure tracking is required.
- Always record timing metrics even on failure if metrics exist.
- Guard against `NullPointerException` when parsing nested JSON.
- When using Jackson `JsonNode`, check `isMissingNode()` and `isEmpty()` at each level where needed.

Example:

```java
long startNanos = System.nanoTime();

try {
    service.process(request);
} catch (ProcessingException exception) {
    LOG.error("Failed to process request with id {}", request.id(), exception);
    throw exception;
} finally {
    metrics.recordProcessingTime(System.nanoTime() - startNanos);
}
```

---

## Logging Style

Use placeholder-based logging.

Good:

```java
LOG.info("Classified ticket id {} as category {} with priority {}", ticketId, category, priority);
```

Bad:

```java
LOG.info("Classified ticket " + ticketId + " as " + category);
```

Do not log large payloads directly.

Use helper methods to truncate large values.

Example:

```java
private static final int MAX_LOG_PAYLOAD_LENGTH = 500;

private String truncateForLog(String value) {
    if (value == null || value.length() <= MAX_LOG_PAYLOAD_LENGTH) {
        return value;
    }

    return value.substring(0, MAX_LOG_PAYLOAD_LENGTH) + "...";
}
```

---

## Testing Rules

- Use JUnit 5 for tests.
- Tests should cover both happy paths and edge cases.
- Tests should be independent from execution order.
- Tests should describe behavior, not implementation details.
- Prefer business-readable test names.
- Include tests for null and blank inputs.
- Avoid unnecessary mocking.
- Follow the same Checkstyle rules in test code.
- Keep tests deterministic.
- Do not rely on current time unless time is injected or controlled.
- Prefer one behavior per test.
- Avoid testing private methods directly.
- Test public behavior through public APIs.

Good test naming style:

```java
@Test
void shouldClassifyBillingTicketWhenMessageContainsInvoiceKeyword() {
}
```

Avoid generic names:

```java
@Test
void test1() {
}
```

---

## Documentation Rules

- Documentation should be client-friendly.
- Explain why the demo exists.
- Explain how the agentic workflow works.
- Avoid overclaiming.
- Clearly state that this is a controlled demo, not a fully autonomous production platform.
- Explain trade-offs and limitations honestly.
- Prefer practical examples over abstract explanations.
- Keep Markdown files easy to scan.
- Use short sections and concrete examples.

---

## Review Rules

When reviewing code, check:

- Does it match the specification?
- Is it simple enough?
- Is it testable?
- Are edge cases covered?
- Are names clear?
- Is the code easy to explain to the client?
- Are there any unnecessary dependencies?
- Does it pass Checkstyle?
- Are imports ordered correctly?
- Are there wildcard imports?
- Are there unused imports?
- Are there duplicate or redundant imports?
- Are method parameters reassigned?
- Are loop control variables modified?
- Are magic float or double literals extracted into constants?
- Are all switch statements covered by a `default` case?
- Are strings compared with `.equals()`?
- Are boolean expressions simplified?
- Is nesting within the allowed limits?
- Are exceptions handled specifically?
- Is SLF4J used instead of `System.out` or `System.err`?
- Are resources closed with try-with-resources?
- Are tests meaningful and readable?
- Are edge cases covered by tests?
- Is the implementation deterministic?
- Is the implementation easy to maintain?

---

## GitHub Copilot Behavior

When generating, editing, or reviewing code:

1. First understand the existing project structure.
2. Follow these repository instructions.
3. Follow Checkstyle strictly.
4. Make the smallest useful change.
5. Add or update tests when behavior changes.
6. Avoid unrelated refactoring.
7. Avoid new dependencies unless explicitly requested.
8. Do not use wildcard imports.
9. Do not use `System.out`, `System.err`, `System.exit`, or `System.gc`.
10. Do not create public `main` methods unless explicitly requested.
11. Do not reassign method parameters.
12. Do not modify loop control variables inside loop bodies.
13. Do not create deeply nested code.
14. Prefer helper methods for readability.
15. Explain assumptions in the final summary.
16. Suggest running validation commands.

---

## Agentic Coding Workflow for This Demo

Use the following controlled workflow when asked to implement a feature:

### 1. Requirement Agent

Clarify the business requirement.

Output:

- Business goal
- User story
- Expected behavior
- Constraints
- Open assumptions

### 2. Specification Agent

Convert the requirement into technical specification.

Output:

- Acceptance criteria
- Input and output rules
- Validation rules
- Error handling rules
- Test scenarios

### 3. Architecture Agent

Design a small Java solution.

Output:

- Proposed classes
- Responsibilities
- Package structure
- Dependencies
- Trade-offs

### 4. Code Agent

Implement the code according to the specification and architecture.

Rules:

- Follow Checkstyle strictly.
- Keep code simple.
- Avoid unnecessary abstractions.
- Add validation.
- Add logging only where useful.
- Do not use forbidden patterns.

### 5. Test Agent

Create independent tests based on the specification.

Rules:

- Do not only test the implementation.
- Test the expected behavior.
- Cover happy paths and edge cases.
- Use readable test names.
- Keep tests deterministic.

### 6. Review Agent

Review implementation and tests.

Check:

- Correctness
- Specification alignment
- Checkstyle compliance
- Error handling
- Test coverage
- Simplicity
- Maintainability

### 7. Improvement Loop

Apply review feedback with minimal changes.

Then run or suggest:

```bash
./mvnw clean test
./mvnw checkstyle:check
```

---

## Important Demo Message

This demo should communicate:

> GitHub Copilot becomes more useful when we do not use it only as autocomplete, but guide it with clear instructions, role-based prompts, acceptance criteria, tests, coding standards, and review loops.

The goal is controlled AI-assisted engineering, not uncontrolled vibe coding.
