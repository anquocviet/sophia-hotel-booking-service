package vn.edu.iuh.bookingservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CREDIT_CARD("credit_card", "Credit Card Payment"),
    DEBIT_CARD("debit_card", "Debit Card Payment"),
    PAYPAL("paypal", "PayPal Payment"),
    BANK_TRANSFER("bank_transfer", "Bank Transfer"),
    CASH("cash", "Cash Payment"),
    CRYPTO("crypto", "Cryptocurrency Payment"),
    OTHER("other", "Other Payment Method");

    private final String value;
    private final String description;

    PaymentMethod(String value, String description) {
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
    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid payment method: " + value);
    }
    
    public boolean isOnlinePayment() {
        return this == CREDIT_CARD || this == DEBIT_CARD || this == PAYPAL || this == CRYPTO;
    }
    
    public boolean requiresVerification() {
        return this == BANK_TRANSFER;
    }
}
