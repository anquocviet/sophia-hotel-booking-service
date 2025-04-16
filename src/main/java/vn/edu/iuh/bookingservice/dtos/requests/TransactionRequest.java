package vn.edu.iuh.bookingservice.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.bookingservice.enums.PaymentMethod;
import vn.edu.iuh.bookingservice.enums.PaymentStatus;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @NotNull(message = "Cart ID is required")
    private UUID cartId;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    private PaymentStatus paymentStatus;
    
    @NotNull(message = "Paid amount is required")
    @Positive(message = "Paid amount must be greater than zero")
    private Double paidAmount;
}
