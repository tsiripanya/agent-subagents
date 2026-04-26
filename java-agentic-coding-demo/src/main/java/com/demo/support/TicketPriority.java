package com.demo.support;

/**
 * Urgency level assigned to a customer ticket.
 *
 * <p>The order of the constants is for readability only; the matching order
 * used by {@link TicketClassifier} is defined inside that class.</p>
 */
public enum TicketPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
