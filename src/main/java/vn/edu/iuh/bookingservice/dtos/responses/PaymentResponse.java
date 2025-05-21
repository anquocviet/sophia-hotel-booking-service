package vn.edu.iuh.bookingservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.bookingservice.enums.PaymentMethod;
import vn.edu.iuh.bookingservice.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID userId;
    private String currency;
    private Double amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private LocalDateTime createdAt;
}
