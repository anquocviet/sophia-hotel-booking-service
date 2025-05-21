package vn.edu.iuh.bookingservice.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.bookingservice.dtos.requests.CartItemRequest;
import vn.edu.iuh.bookingservice.dtos.requests.CartRequest;
import vn.edu.iuh.bookingservice.dtos.responses.CartResponse;
import vn.edu.iuh.bookingservice.dtos.responses.RoomResponse;
import vn.edu.iuh.bookingservice.entities.Cart;
import vn.edu.iuh.bookingservice.entities.CartItem;
import vn.edu.iuh.bookingservice.exceptions.BadRequestException;
import vn.edu.iuh.bookingservice.exceptions.ResourceNotFoundException;
import vn.edu.iuh.bookingservice.mappers.CartItemMapper;
import vn.edu.iuh.bookingservice.mappers.CartMapper;
import vn.edu.iuh.bookingservice.repositories.CartItemRepository;
import vn.edu.iuh.bookingservice.repositories.CartRepository;
import vn.edu.iuh.bookingservice.services.CartService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final RestTemplate restTemplate;
    
    private static final String HOTEL_SERVICE_URL = "http://hotel-service:8082/api/v1/hotels/{hotelId}/rooms/{roomId}";
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public CartResponse createCart(CartRequest request) {
        validateCartRequest(request);
        
        Cart cart = cartMapper.toEntity(request);
        
        // Process cart items and calculate total prices
        processCartItems(cart, request.getCartItems());
        
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponse(savedCart);
    }

    @Override
    public CartResponse getCartById(UUID id) {
        Cart cart = findCartById(id);
        return cartMapper.toResponse(cart);
    }

    @Override
    public List<CartResponse> getAllCarts() {
        List<Cart> carts = (List<Cart>) cartRepository.findAll();
        return carts.stream()
                .map(cartMapper::toResponse)
                .toList();
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
            
            // Process cart items and calculate total prices
            processCartItems(cart, request.getCartItems());
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

    @Override
    public CartResponse getCartByUserId(UUID userId) {
        if (userId == null) {
            throw new BadRequestException("User ID cannot be null");
        }
        
        List<Cart> userCarts = cartRepository.findByUserId(userId);
        if (userCarts.isEmpty()) {
            throw new ResourceNotFoundException("Cart", "userId", userId);
        }
        
        // Find the most recent active cart
        Optional<Cart> cartActive = userCarts.stream()
                .filter(cart -> cart.getStatus() != null &&
                        !cart.getStatus().isTerminal() &&
                        cart.getDeletedAt() == null
                )
                .findFirst();

        return cartActive.map(cartMapper::toResponse).orElse(null);
    }

    private Cart findCartById(UUID id) {
        if (id == null) {
            throw new BadRequestException("Cart ID cannot be null");
        }
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", id));
    }
    
    /**
     * Process cart items, fetch room information, and calculate total prices
     * 
     * @param cart the cart to process
     * @param cartItemRequests the cart item requests
     */
    private void processCartItems(Cart cart, List<CartItemRequest> cartItemRequests) {
        if (cartItemRequests == null || cartItemRequests.isEmpty()) {
            return;
        }
        
        // Prepare carts items list if not present
        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }
        
        // Process each item request
        for (CartItemRequest itemRequest : cartItemRequests) {
            CartItem cartItem = cartItemMapper.toEntity(itemRequest, cart);
            
            // Fetch room from hotel service
            RoomResponse roomResponse = fetchRoom(itemRequest.getHotelId(), itemRequest.getRoomId());
            if (roomResponse.getPrice() != null) {
                cartItem.setPrice(roomResponse.getPrice());
            }
            
            cart.getCartItems().add(cartItem);
        }
        
        // Calculate total price
        calculateAndSetTotalPrice(cart);
    }
    
    /**
     * Calculate and set the total price for a cart
     * 
     * @param cart the cart to calculate price for
     */
    private void calculateAndSetTotalPrice(Cart cart) {
        double totalPrice = cart.getCartItems().stream()
                .filter(item -> item.getDeletedAt() == null) // Only consider non-deleted items
                .mapToDouble(item -> {
                    if (item.getPrice() == null) return 0.0;
                    // Calculate numbers of days between checkin and checkout
                    long days = calculateDaysBetween(item.getCheckinDate(), item.getCheckoutDate());
                    return item.getPrice() * days;
                })
                .sum();
        cart.setTotalPrice(totalPrice);
    }
    
    private RoomResponse fetchRoom(UUID hotelId, UUID roomId) {
        try {
            if (hotelId == null || roomId == null) {
                throw new BadRequestException("Hotel ID and Room ID must not be null");
            }
            
            RoomResponse roomResponse = restTemplate.getForObject(
                HOTEL_SERVICE_URL, 
                RoomResponse.class, 
                hotelId.toString(), 
                roomId.toString()
            );
            
            if (roomResponse == null) {
                throw new BadRequestException("Room information not found");
            }
            
            if (roomResponse.getPrice() == null) {
                throw new BadRequestException("Room price information is missing");
            }
            
            return roomResponse;
        } catch (Exception e) {
            throw new BadRequestException("Error fetching room information: " + e.getMessage());
        }
    }
    
    /**
     * Calculates the number of days between two timestamps.
     * @param start the checkin date
     * @param end the checkout date
     * @return the number of days (minimum 1)
     */
    private long calculateDaysBetween(Timestamp start, Timestamp end) {
        if (start == null || end == null) return 1;
        
        long diffInMillis = end.getTime() - start.getTime();
        long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
        
        // Ensure at least 1 day is charged even for same-day stays
        return Math.max(diffInDays, 1);
    }
    
    /**
     * Validates the cart request for mandatory fields and business rules
     * 
     * @param request the cart request to validate
     * @throws BadRequestException if validation fails
     */
    private void validateCartRequest(CartRequest request) {
        if (request == null) {
            throw new BadRequestException("Cart request cannot be null");
        }
        
        if (request.getUserId() == null) {
            throw new BadRequestException("User ID is required");
        }
        
        validateCartItems(request.getCartItems());
    }
    
    /**
     * Validates the cart items collection
     * 
     * @param cartItems the cart items to validate
     * @throws BadRequestException if validation fails
     */
    private void validateCartItems(List<CartItemRequest> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BadRequestException("Cart must contain at least one item");
        }
        
        for (CartItemRequest item : cartItems) {
            validateCartItem(item);
        }
    }
    
    /**
     * Validates a single cart item
     * 
     * @param item the cart item to validate
     * @throws BadRequestException if validation fails
     */
    private void validateCartItem(CartItemRequest item) {
        if (item == null) {
            throw new BadRequestException("Cart item cannot be null");
        }
        
        if (item.getRoomId() == null) {
            throw new BadRequestException("Room ID is required for all cart items");
        }
        
        if (item.getHotelId() == null) {
            throw new BadRequestException("Hotel ID is required for all cart items");
        }
        
        validateCartItemDates(item);
    }
    
    /**
     * Validates the dates in a cart item
     * 
     * @param item the cart item with dates to validate
     * @throws BadRequestException if validation fails
     */
    private void validateCartItemDates(CartItemRequest item) {
        if (item.getCheckinDate() == null) {
            throw new BadRequestException("Check-in date is required for all cart items");
        }
        
        if (item.getCheckoutDate() == null) {
            throw new BadRequestException("Check-out date is required for all cart items");
        }
        
        if (item.getCheckinDate().after(item.getCheckoutDate())) {
            throw new BadRequestException("Check-in date must be before check-out date");
        }
    }
    
    @Override
    @Transactional
    public CartResponse removeCartItem(UUID cartId, UUID cartItemId) {
        if (cartId == null) {
            throw new BadRequestException("Cart ID cannot be null");
        }
        
        if (cartItemId == null) {
            throw new BadRequestException("Cart item ID cannot be null");
        }
        
        // Find the cart
        Cart cart = findCartById(cartId);
        
        // Find the cart item
        CartItem cartItem = cartItemRepository.findByIdAndCartIdAndDeletedAtIsNull(cartItemId, cartId)
            .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));
        
        // Soft delete the cart item
        cartItem.setDeletedAt(Timestamp.from(Instant.now()));
        cartItemRepository.save(cartItem);
        
        // No need to manually remove from collection as mapper will filter
        
        // Recalculate total price considering only non-deleted items
        calculateAndSetTotalPrice(cart);
        
        // Save the updated cart
        Cart updatedCart = cartRepository.save(cart);
        
        return cartMapper.toResponse(updatedCart);
    }
}
