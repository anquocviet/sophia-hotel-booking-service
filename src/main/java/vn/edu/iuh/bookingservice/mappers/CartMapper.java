package vn.edu.iuh.bookingservice.mappers;

import org.springframework.stereotype.Component;
import vn.edu.iuh.bookingservice.dtos.requests.CartRequest;
import vn.edu.iuh.bookingservice.dtos.responses.CartResponse;
import vn.edu.iuh.bookingservice.entities.Cart;
import vn.edu.iuh.bookingservice.entities.CartItem;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    
    private final CartItemMapper cartItemMapper;
    
    public CartMapper(CartItemMapper cartItemMapper) {
        this.cartItemMapper = cartItemMapper;
    }
    
    public Cart toEntity(CartRequest request) {
        if (request == null) {
            return null;
        }
        
        Cart cart = new Cart();
        cart.setUserId(request.getUserId());
        cart.setStatus(request.getStatus());
        cart.setCreatedAt(Timestamp.from(Instant.now()));
        
        return cart;
    }
    
    public CartResponse toResponse(Cart cart) {
        if (cart == null) {
            return null;
        }
        
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setTotalPrice(cart.getTotalPrice());
        response.setStatus(cart.getStatus());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());
        
        // Only map non-deleted cart items
        if (cart.getCartItems() != null) {
            List<CartItem> nonDeletedItems = cart.getCartItems().stream()
                .filter(item -> item.getDeletedAt() == null)
                .collect(Collectors.toList());
                
            response.setCartItems(cartItemMapper.toResponseList(nonDeletedItems));
        }
        
        return response;
    }
    
    public void updateEntityFromRequest(CartRequest request, Cart cart) {
        if (request == null || cart == null) {
            return;
        }
        
        if (request.getStatus() != null) {
            cart.setStatus(request.getStatus());
        }
        
        cart.setUpdatedAt(Timestamp.from(Instant.now()));
    }
}
