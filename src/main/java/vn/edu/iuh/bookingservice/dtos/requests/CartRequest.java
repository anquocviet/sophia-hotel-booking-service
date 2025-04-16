package vn.edu.iuh.bookingservice.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.bookingservice.enums.CartStatus;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    private CartStatus status;
    
    @NotEmpty(message = "Cart must contain at least one item")
    @Valid
    private List<CartItemRequest> cartItems;
}
