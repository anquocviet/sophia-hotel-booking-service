package vn.edu.iuh.bookingservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.bookingservice.enums.PaymentMethod;
import vn.edu.iuh.bookingservice.enums.PaymentStatus;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private UUID id;
    private String transactionId;
    private CartResponse cart;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private Double paidAmount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
