package com.demo.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Behavior-level tests derived from {@code docs/02-technical-specification.md}.
 *
 * <p>Each test maps to one acceptance criterion or test scenario in the
 * specification. The internals of {@link TicketClassifier} are not asserted
 * directly — only the public contract.</p>
 */
class TicketClassifierTest {

    private static final String VALID_ID = "T-1";
    private static final String VALID_CUSTOMER = "C-1";

    private final TicketClassifier classifier = new TicketClassifier();

    @Test
    void shouldClassifyAsBillingCriticalWhenMessageMentionsRefundAndUrgent() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER,
                "Please refund my last invoice, it is urgent.");

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals(VALID_ID, result.ticketId());
        assertEquals(TicketCategory.BILLING, result.category());
        assertEquals(TicketPriority.CRITICAL, result.priority());
        assertTrue(result.confidence() > 0, "confidence must be greater than zero");
    }

    @Test
    void shouldClassifyAsTechnicalLowWhenMessageMentionsLoginBroken() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER,
                "My login is broken since this morning.");

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals(TicketCategory.TECHNICAL, result.category());
        assertEquals(TicketPriority.LOW, result.priority());
    }

    @Test
    void shouldClassifyAsContractLowWhenMessageMentionsCancelContract() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER,
                "I want to cancel my contract.");

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals(TicketCategory.CONTRACT, result.category());
        assertEquals(TicketPriority.LOW, result.priority());
    }

    @Test
    void shouldClassifyAsComplaintLowWhenMessageMentionsTerribleAndDisappointed() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER,
                "Terrible service, I am very disappointed!");

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals(TicketCategory.COMPLAINT, result.category());
        assertEquals(TicketPriority.LOW, result.priority());
    }

    @Test
    void shouldClassifyAsGeneralLowWithZeroConfidenceWhenNoKeywordsMatch() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER,
                "Hello, I have a quick question.");

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals(TicketCategory.GENERAL, result.category());
        assertEquals(TicketPriority.LOW, result.priority());
        assertEquals(0.0, result.confidence());
    }

    @Test
    void shouldDefaultToGeneralLowWhenMessageIsNull() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER, null);

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals(TicketCategory.GENERAL, result.category());
        assertEquals(TicketPriority.LOW, result.priority());
        assertEquals("no message provided", result.reason());
        assertEquals(0.0, result.confidence());
    }

    @Test
    void shouldDefaultToGeneralLowWhenMessageIsBlank() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER, "   ");

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals(TicketCategory.GENERAL, result.category());
        assertEquals(TicketPriority.LOW, result.priority());
        assertEquals("no message provided", result.reason());
    }

    @Test
    void shouldThrowWhenTicketIsNull() {
        assertThrows(IllegalArgumentException.class, () -> classifier.classify(null));
    }

    @Test
    void shouldThrowWhenTicketIdIsBlank() {
        Ticket ticket = new Ticket(" ", VALID_CUSTOMER, "any message");

        assertThrows(IllegalArgumentException.class, () -> classifier.classify(ticket));
    }

    @Test
    void shouldThrowWhenCustomerIdIsNull() {
        Ticket ticket = new Ticket(VALID_ID, null, "any message");

        assertThrows(IllegalArgumentException.class, () -> classifier.classify(ticket));
    }

    @Test
    void shouldThrowWhenMessageExceedsMaxLength() {
        String tooLong = "a".repeat(10_001);
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER, tooLong);

        assertThrows(IllegalArgumentException.class, () -> classifier.classify(ticket));
    }

    @Test
    void shouldMatchKeywordsCaseInsensitively() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER, "I need a copy of my INVOICE.");

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals(TicketCategory.BILLING, result.category());
    }

    @Test
    void shouldPreferBillingOverTechnicalWhenBothMatch() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER,
                "Login error after a payment was charged twice.");

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals(TicketCategory.BILLING, result.category());
    }

    @Test
    void shouldPreferCriticalOverHighWhenBothMatch() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER,
                "This is urgent and very important.");

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals(TicketPriority.CRITICAL, result.priority());
    }

    @Test
    void shouldProduceConfidenceWithinValidRangeForEveryResult() {
        Ticket ticket = new Ticket(VALID_ID, VALID_CUSTOMER,
                "Outage urgent emergency: payment refund invoice down asap critical.");

        TicketClassificationResult result = classifier.classify(ticket);

        assertNotNull(result);
        assertTrue(result.confidence() >= 0, "confidence must be >= 0");
        assertTrue(result.confidence() <= 1, "confidence must be <= 1");
    }

    @Test
    void shouldReturnTicketIdEqualToInputId() {
        Ticket ticket = new Ticket("T-42", VALID_CUSTOMER, "Hello there.");

        TicketClassificationResult result = classifier.classify(ticket);

        assertEquals("T-42", result.ticketId());
    }
}
