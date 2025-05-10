package vn.edu.iuh.bookingservice.dtos.responses;

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
public class CartItemResponse {
    private UUID id;
    private UUID roomId;
    private UUID hotelId;
    private Timestamp checkinDate;
    private Timestamp checkoutDate;
    private Double price;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
