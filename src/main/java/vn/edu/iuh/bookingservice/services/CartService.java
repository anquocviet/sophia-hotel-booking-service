package vn.edu.iuh.bookingservice.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.iuh.bookingservice.dtos.requests.CartRequest;
import vn.edu.iuh.bookingservice.dtos.responses.CartResponse;

import java.util.List;
import java.util.UUID;

public interface CartService {
    CartResponse createCart(CartRequest request);
    CartResponse getCartById(UUID id);
    Page<CartResponse> getAllCarts(Pageable pageable);
    CartResponse updateCart(UUID id, CartRequest request);
    void deleteCart(UUID id);
//    CartResponse getCartByUserId(UUID userId);
//    List<CartResponse> getCartsByUserId(UUID userId);

    List<UUID> getCartsByUserId(UUID userId);

}

