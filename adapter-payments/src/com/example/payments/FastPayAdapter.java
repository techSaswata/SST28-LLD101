package com.example.payments;

import java.util.Objects;

/**
 * Adapter that maps the FastPayClient SDK to the PaymentGateway interface.
 */
public class FastPayAdapter implements PaymentGateway {

    private final FastPayClient client;

    public FastPayAdapter(FastPayClient client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    @Override
    public String charge(String customerId, int amountCents) {
        return client.payNow(customerId, amountCents);
    }
}
