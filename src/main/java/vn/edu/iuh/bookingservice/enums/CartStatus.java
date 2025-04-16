package vn.edu.iuh.bookingservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CartStatus {
    AVAILABLE("available", "Cart is available for booking"),
    PENDING("pending", "Waiting for payment"),
    PAID("paid", "Payment completed"),
    CANCELLED("cancelled", "Booking has been cancelled"),
    EXPIRED("expired", "Booking has expired");

    private final String value;
    private final String description;

    CartStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static CartStatus fromValue(String value) {
        for (CartStatus status : CartStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid cart status: " + value);
    }

    public boolean isTerminal() {
        return this == PAID || this == CANCELLED || this == EXPIRED;
    }

    public boolean canTransitionTo(CartStatus newStatus) {
        if (this.isTerminal()) {
            return false; // Terminal states cannot transition
        }
        
        // Define allowed transitions
        switch (this) {
            case AVAILABLE:
                return newStatus == PENDING;
            case PENDING:
                return newStatus == PAID || newStatus == CANCELLED || newStatus == EXPIRED;
            default:
                return false;
        }
    }
}
