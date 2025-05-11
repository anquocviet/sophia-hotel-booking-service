package vn.edu.iuh.bookingservice.mappers;

import org.springframework.stereotype.Component;
import vn.edu.iuh.bookingservice.dtos.requests.CartItemRequest;
import vn.edu.iuh.bookingservice.dtos.responses.CartItemResponse;
import vn.edu.iuh.bookingservice.entities.Cart;
import vn.edu.iuh.bookingservice.entities.CartItem;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartItemMapper {
    
    public CartItem toEntity(CartItemRequest request, Cart cart) {
        if (request == null) {
            return null;
        }
        
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setRoomId(request.getRoomId());
        cartItem.setHotelId(request.getHotelId());
        cartItem.setCheckinDate(request.getCheckinDate());
        cartItem.setCheckoutDate(request.getCheckoutDate());
        cartItem.setCreatedAt(Timestamp.from(Instant.now()));
        
        return cartItem;
    }
    
    public CartItemResponse toResponse(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setRoomId(cartItem.getRoomId());
        response.setHotelId(cartItem.getHotelId());
        response.setCheckinDate(cartItem.getCheckinDate());
        response.setCheckoutDate(cartItem.getCheckoutDate());
        response.setPrice(cartItem.getPrice());
        response.setCreatedAt(cartItem.getCreatedAt());
        response.setUpdatedAt(cartItem.getUpdatedAt());
        
        return response;
    }
    
    public List<CartItemResponse> toResponseList(List<CartItem> cartItems) {
        if (cartItems == null) {
            return new ArrayList<>();
        }
        
        return cartItems.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
