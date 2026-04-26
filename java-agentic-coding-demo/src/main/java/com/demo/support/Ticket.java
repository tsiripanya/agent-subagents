package com.demo.support;

/**
 * Immutable customer support ticket.
 *
 * <p>The classifier validates these fields at the API boundary; this record
 * itself stays a plain data carrier.</p>
 *
 * @param id         opaque, non-blank ticket identifier supplied by the caller
 * @param customerId opaque, non-blank customer identifier supplied by the caller
 * @param message    free-text customer message; may be {@code null} or blank
 */
public record Ticket(String id, String customerId, String message) {
}
