# Java Agentic Coding Demo with GitHub Copilot

You are a senior Java architect, senior backend developer, senior QA engineer, and senior AI coding workflow consultant.

Your task is to create a small but professional Java demo project that demonstrates an agentic coding workflow using GitHub Copilot.

The demo should show how a business requirement can be transformed step-by-step into:

1. Technical specification
2. Architecture proposal
3. Java implementation
4. Unit tests
5. Code review report
6. Final improved implementation

The goal is to demonstrate to a client that GitHub Copilot can be used not only as autocomplete, but as a structured coding assistant with clear roles, quality gates, and repeatable development workflow.

## Background

The client is interested in a multi-agent coding workflow.

They described an idea where different agents work together:

- One agent receives a business description and creates technical specifications.
- One agent proposes the solution or architecture.
- One agent writes the code.
- One independent agent writes tests based on the specification.
- One agent reviews the implementation.
- The code agent fixes the review comments.
- The workflow repeats until the result is good enough.

The client is concerned that normal Copilot autocomplete often creates incorrect or low-quality output. Therefore, this demo should show a controlled and structured way to use Copilot.

## Demo Use Case

Create a small Java application called:

`java-agentic-coding-demo`

The business use case is:

> A customer support system receives customer messages and classifies each message into a category and priority.  
> The system should classify tickets into categories like BILLING, TECHNICAL, CONTRACT, COMPLAINT, or GENERAL.  
> It should also assign priority LOW, MEDIUM, HIGH, or CRITICAL based on simple rule-based keywords.

This use case should be intentionally small, but realistic enough to demonstrate business value.

## Technical Requirements

Use:

- Java 21
- Maven
- JUnit 5
- Clean package structure
- No Spring Boot
- No database
- No external AI API
- No complex framework

The project should be simple and runnable locally.

## Expected Project Structure

Create this structure:

```text
java-agentic-coding-demo/
├── README.md
├── pom.xml
├── docs/
│   ├── 01-business-requirement.md
│   ├── 02-technical-specification.md
│   ├── 03-architecture-proposal.md
│   ├── 04-code-review-report.md
│   └── 05-demo-walkthrough.md
├── agents/
│   ├── 01-requirement-agent.md
│   ├── 02-spec-agent.md
│   ├── 03-architecture-agent.md
│   ├── 04-code-agent.md
│   ├── 05-test-agent.md
│   └── 06-review-agent.md
├── src/main/java/com/demo/support/
│   ├── Ticket.java
│   ├── TicketCategory.java
│   ├── TicketPriority.java
│   ├── TicketClassificationResult.java
│   ├── TicketClassifier.java
│   └── DemoApplication.java
└── src/test/java/com/demo/support/
    └── TicketClassifierTest.java