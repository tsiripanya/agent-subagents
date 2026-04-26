package com.demo.support;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local demo entry point.
 *
 * <p>Runs the classifier against a small set of sample tickets and logs the
 * results via SLF4J. Intended for manual demonstration only — not part of
 * the public API.</p>
 */
public final class DemoApplication {

    private static final Logger LOG = LoggerFactory.getLogger(DemoApplication.class);

    private DemoApplication() {
    }

    /**
     * Local demo entry point used only for manual demonstration.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        TicketClassifier classifier = new TicketClassifier();

        List<Ticket> samples = List.of(
                new Ticket("T-1", "C-100", "Please refund my last invoice, it is urgent."),
                new Ticket("T-2", "C-101", "My login is broken since this morning."),
                new Ticket("T-3", "C-102", "I want to cancel my contract."),
                new Ticket("T-4", "C-103", "Terrible service, I am very disappointed!"),
                new Ticket("T-5", "C-104", "Hello, I have a quick question.")
        );

        for (Ticket ticket : samples) {
            TicketClassificationResult result = classifier.classify(ticket);
            LOG.info("Ticket {} -> category={}, priority={}, confidence={}, reason='{}'",
                    result.ticketId(),
                    result.category(),
                    result.priority(),
                    result.confidence(),
                    result.reason());
        }
    }
}
