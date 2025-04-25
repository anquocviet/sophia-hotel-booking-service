package vn.edu.iuh.bookingservice.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    @NotNull(message = "Room ID is required")
    private UUID roomId;
    
    @NotNull(message = "Hotel ID is required")
    private UUID hotelId;
    
    @NotNull(message = "Check-in date is required")
    private Timestamp checkinDate;
    
    @NotNull(message = "Check-out date is required")
    private Timestamp checkoutDate;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;
}
