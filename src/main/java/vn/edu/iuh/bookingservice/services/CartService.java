package vn.edu.iuh.bookingservice.services;

import java.util.List;
import vn.edu.iuh.bookingservice.dtos.requests.CartRequest;
import vn.edu.iuh.bookingservice.dtos.responses.CartResponse;

import java.util.UUID;

public interface CartService {
    CartResponse createCart(CartRequest request);
    CartResponse getCartById(UUID id);
    List<CartResponse> getAllCarts();
    CartResponse updateCart(UUID id, CartRequest request);
    void deleteCart(UUID id);
    CartResponse getCartByUserId(UUID userId);
}

