package com.demo.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deterministic, keyword-driven ticket classifier.
 *
 * <p>Rules and ordering are defined in
 * {@code docs/02-technical-specification.md}. This class is intentionally
 * small and stateless so it can be unit-tested without mocks.</p>
 */
public final class TicketClassifier {

    private static final Logger LOG = LoggerFactory.getLogger(TicketClassifier.class);

    private static final int MAX_MESSAGE_LENGTH = 10_000;
    private static final int EXPECTED_MATCHES = 3;
    private static final int MAX_LOG_MESSAGE_LENGTH = 200;
    private static final String NO_MESSAGE_REASON = "no message provided";
    private static final String NO_MATCH_REASON = "no keyword matches";

    private static final List<CategoryRule> CATEGORY_RULES = List.of(
            new CategoryRule(TicketCategory.BILLING, List.of(
                    "invoice", "payment", "charge", "refund", "billing", "subscription", "bill")),
            new CategoryRule(TicketCategory.TECHNICAL, List.of(
                    "error", "bug", "crash", "broken", "not working", "slow", "login", "password")),
            new CategoryRule(TicketCategory.CONTRACT, List.of(
                    "contract", "agreement", "terms", "renewal", "cancel")),
            new CategoryRule(TicketCategory.COMPLAINT, List.of(
                    "complaint", "unhappy", "disappointed", "terrible", "bad service", "angry"))
    );

    private static final List<PriorityRule> PRIORITY_RULES = List.of(
            new PriorityRule(TicketPriority.CRITICAL, List.of(
                    "outage", "down", "urgent", "emergency", "asap", "critical", "immediately")),
            new PriorityRule(TicketPriority.HIGH, List.of(
                    "important", "blocked", "blocking", "escalate", "high priority")),
            new PriorityRule(TicketPriority.MEDIUM, List.of(
                    "soon", "please", "needed"))
    );

    public TicketClassifier() {
    }

    /**
     * Classify the supplied ticket.
     *
     * @param ticket non-null ticket with non-blank id and customerId
     * @return deterministic classification result
     * @throws IllegalArgumentException if the ticket fails boundary validation
     */
    public TicketClassificationResult classify(Ticket ticket) {
        validateTicket(ticket);

        String message = ticket.message();
        if (message == null || message.isBlank()) {
            LOG.debug("Ticket {} has no message; defaulting to GENERAL/LOW", ticket.id());
            return new TicketClassificationResult(
                    ticket.id(),
                    TicketCategory.GENERAL,
                    TicketPriority.LOW,
                    NO_MESSAGE_REASON,
                    0);
        }

        String normalized = message.toLowerCase(Locale.ROOT);
        CategoryMatch categoryMatch = matchCategory(normalized);
        PriorityMatch priorityMatch = matchPriority(normalized);

        int totalMatches = categoryMatch.matched().size() + priorityMatch.matched().size();
        double confidence = computeConfidence(totalMatches);
        String reason = buildReason(categoryMatch, priorityMatch);

        LOG.info("Classified ticket {} as {}/{} (confidence={}, message='{}')",
                ticket.id(),
                categoryMatch.category(),
                priorityMatch.priority(),
                confidence,
                truncateForLog(message));

        return new TicketClassificationResult(
                ticket.id(),
                categoryMatch.category(),
                priorityMatch.priority(),
                reason,
                confidence);
    }

    private void validateTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("ticket must not be null");
        }
        if (ticket.id() == null || ticket.id().isBlank()) {
            throw new IllegalArgumentException("ticket.id must not be blank");
        }
        if (ticket.customerId() == null || ticket.customerId().isBlank()) {
            throw new IllegalArgumentException("ticket.customerId must not be blank");
        }
        if (ticket.message() != null && ticket.message().length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException(
                    "ticket.message must not exceed " + MAX_MESSAGE_LENGTH + " characters");
        }
    }

    private CategoryMatch matchCategory(String normalized) {
        for (CategoryRule rule : CATEGORY_RULES) {
            List<String> matched = findMatches(normalized, rule.keywords());
            if (!matched.isEmpty()) {
                return new CategoryMatch(rule.category(), matched);
            }
        }
        return new CategoryMatch(TicketCategory.GENERAL, List.of());
    }

    private PriorityMatch matchPriority(String normalized) {
        for (PriorityRule rule : PRIORITY_RULES) {
            List<String> matched = findMatches(normalized, rule.keywords());
            if (!matched.isEmpty()) {
                return new PriorityMatch(rule.priority(), matched);
            }
        }
        return new PriorityMatch(TicketPriority.LOW, List.of());
    }

    private List<String> findMatches(String normalized, List<String> keywords) {
        List<String> matched = new ArrayList<>();
        for (String keyword : keywords) {
            if (normalized.contains(keyword)) {
                matched.add(keyword);
            }
        }
        return matched;
    }

    private double computeConfidence(int totalMatches) {
        if (totalMatches <= 0) {
            return 0;
        }
        double ratio = (double) totalMatches / (double) EXPECTED_MATCHES;
        if (ratio > 1) {
            return 1;
        }
        return ratio;
    }

    private String buildReason(CategoryMatch categoryMatch, PriorityMatch priorityMatch) {
        if (categoryMatch.matched().isEmpty() && priorityMatch.matched().isEmpty()) {
            return NO_MATCH_REASON;
        }

        StringBuilder builder = new StringBuilder();
        if (!categoryMatch.matched().isEmpty()) {
            builder.append("matched ")
                    .append(categoryMatch.category().name().toLowerCase(Locale.ROOT))
                    .append(" keyword(s): ")
                    .append(String.join(", ", categoryMatch.matched()));
        }
        if (!priorityMatch.matched().isEmpty()) {
            if (builder.length() > 0) {
                builder.append("; ");
            }
            builder.append("matched ")
                    .append(priorityMatch.priority().name().toLowerCase(Locale.ROOT))
                    .append(" priority keyword(s): ")
                    .append(String.join(", ", priorityMatch.matched()));
        }
        return builder.toString();
    }

    private String truncateForLog(String value) {
        if (value == null || value.length() <= MAX_LOG_MESSAGE_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_LOG_MESSAGE_LENGTH) + "...";
    }

    private record CategoryRule(TicketCategory category, List<String> keywords) {
    }

    private record PriorityRule(TicketPriority priority, List<String> keywords) {
    }

    private record CategoryMatch(TicketCategory category, List<String> matched) {
    }

    private record PriorityMatch(TicketPriority priority, List<String> matched) {
    }
}
