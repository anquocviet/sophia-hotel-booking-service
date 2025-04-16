package vn.edu.iuh.bookingservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {
    PENDING("pending", "Payment is pending"),
    PROCESSING("processing", "Payment is being processed"),
    COMPLETED("completed", "Payment has been completed successfully"),
    FAILED("failed", "Payment has failed"),
    REFUNDED("refunded", "Payment has been refunded"),
    CANCELLED("cancelled", "Payment has been cancelled");

    private final String value;
    private final String description;

    PaymentStatus(String value, String description) {
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
    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid payment status: " + value);
    }
    
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == REFUNDED || this == CANCELLED;
    }
    
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
    
    public boolean canTransitionTo(PaymentStatus newStatus) {
        if (this.isTerminal()) {
            // Terminal states can only transition to REFUNDED (if COMPLETED)
            return this == COMPLETED && newStatus == REFUNDED;
        }
        
        // Define allowed transitions
        switch (this) {
            case PENDING:
                return newStatus == PROCESSING || newStatus == COMPLETED || 
                       newStatus == FAILED || newStatus == CANCELLED;
            case PROCESSING:
                return newStatus == COMPLETED || newStatus == FAILED || newStatus == CANCELLED;
            default:
                return false;
        }
    }
}
