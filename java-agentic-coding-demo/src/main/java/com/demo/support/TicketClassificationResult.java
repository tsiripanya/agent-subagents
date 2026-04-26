package com.demo.support;

/**
 * Result of classifying a {@link Ticket}.
 *
 * @param ticketId   echoes the input ticket id
 * @param category   exactly one assigned business category
 * @param priority   exactly one assigned urgency level
 * @param reason     short human-readable explanation referring to matched keywords
 * @param confidence deterministic score in {@code [0.0, 1.0]}
 */
public record TicketClassificationResult(
        String ticketId,
        TicketCategory category,
        TicketPriority priority,
        String reason,
        double confidence) {
}
