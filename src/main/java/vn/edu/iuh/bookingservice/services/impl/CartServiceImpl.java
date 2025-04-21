package vn.edu.iuh.bookingservice.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.iuh.bookingservice.dtos.requests.CartItemRequest;
import vn.edu.iuh.bookingservice.dtos.requests.CartRequest;
import vn.edu.iuh.bookingservice.dtos.responses.CartResponse;
import vn.edu.iuh.bookingservice.entities.Cart;
import vn.edu.iuh.bookingservice.entities.CartItem;
import vn.edu.iuh.bookingservice.exceptions.BadRequestException;
import vn.edu.iuh.bookingservice.exceptions.ResourceNotFoundException;
import vn.edu.iuh.bookingservice.mappers.CartItemMapper;
import vn.edu.iuh.bookingservice.mappers.CartMapper;
import vn.edu.iuh.bookingservice.repositories.CartRepository;
import vn.edu.iuh.bookingservice.services.CartService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public CartResponse createCart(CartRequest request) {
        validateCartRequest(request);
        
        Cart cart = cartMapper.toEntity(request);
        
        // Process cart items
        List<CartItem> cartItems = new ArrayList<>();
        if (request.getCartItems() != null && !request.getCartItems().isEmpty()) {
            for (CartItemRequest itemRequest : request.getCartItems()) {
                CartItem cartItem = cartItemMapper.toEntity(itemRequest, cart);
                cartItems.add(cartItem);
            }
        }
        cart.setCartItems(cartItems);
        
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponse(savedCart);
    }

    @Override
    public CartResponse getCartById(UUID id) {
        Cart cart = findCartById(id);
        return cartMapper.toResponse(cart);
    }

    @Override
    public Page<CartResponse> getAllCarts(Pageable pageable) {
        Page<Cart> carts = cartRepository.findAll(pageable);
        return carts.map(cartMapper::toResponse);
    }

    @Override
    @Transactional
    public CartResponse updateCart(UUID id, CartRequest request) {
        validateCartRequest(request);
        
        Cart cart = findCartById(id);
        cartMapper.updateEntityFromRequest(request, cart);
        
        // Handle cart items if they are being updated
        if (request.getCartItems() != null && !request.getCartItems().isEmpty()) {
            // Remove existing items
            if (cart.getCartItems() != null) {
                cart.getCartItems().clear();
            } else {
                cart.setCartItems(new ArrayList<>());
            }
            
            // Add new items
            for (CartItemRequest itemRequest : request.getCartItems()) {
                CartItem cartItem = cartItemMapper.toEntity(itemRequest, cart);
                cart.getCartItems().add(cartItem);
            }
            
            // Calculate total price
            double totalPrice = request.getCartItems().stream()
                    .mapToDouble(item -> item.getPrice() != null ? item.getPrice() : 0.0)
                    .sum();
            cart.setTotalPrice(totalPrice);
        }
        
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toResponse(updatedCart);
    }

    @Override
    @Transactional
    public void deleteCart(UUID id) {
        Cart cart = findCartById(id);
        cart.setDeletedAt(Timestamp.from(Instant.now()));
        cartRepository.save(cart);
    }

//    @Override
//    public CartResponse getCartByUserId(UUID userId) {
//        Cart cart = cartRepository.findByUserId(userId);
//        if (cart == null) {
//            throw new ResourceNotFoundException("Cart", "userId", userId);
//        }
//        return cartMapper.toResponse(cart);
//    }
//@Override
//public List<CartResponse> getCartsByUserId(UUID userId) {
//    List<Cart> carts = cartRepository.findByUserId(userId);
//    if (carts.isEmpty()) {
//        throw new ResourceNotFoundException("Cart", "userId", userId);
//    }
//    return carts.stream().map(cartMapper::toResponse).collect(Collectors.toList());
//}

    @Override
    public List<UUID> getCartsByUserId(UUID userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        if (carts.isEmpty()) {
            throw new ResourceNotFoundException("Cart", "userId", userId);
        }

        return carts.stream()
                .flatMap(cart -> cart.getCartItems().stream())
                .map(CartItem::getRoomId)
                .collect(Collectors.toList());
    }
    
    private Cart findCartById(UUID id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", id));
    }
    
    private void validateCartRequest(CartRequest request) {
        if (request.getUserId() == null) {
            throw new BadRequestException("User ID is required");
        }
        
        if (request.getCartItems() == null || request.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart must contain at least one item");
        }
        
        for (CartItemRequest item : request.getCartItems()) {
            if (item.getRoomId() == null) {
                throw new BadRequestException("Room ID is required for all cart items");
            }
            
            if (item.getCheckinDate() == null) {
                throw new BadRequestException("Check-in date is required for all cart items");
            }
            
            if (item.getCheckoutDate() == null) {
                throw new BadRequestException("Check-out date is required for all cart items");
            }
            
            if (item.getPrice() == null || item.getPrice() <= 0) {
                throw new BadRequestException("Price must be greater than zero for all cart items");
            }
            
            if (item.getCheckinDate().after(item.getCheckoutDate())) {
                throw new BadRequestException("Check-in date must be before check-out date");
            }
        }
    }
}
