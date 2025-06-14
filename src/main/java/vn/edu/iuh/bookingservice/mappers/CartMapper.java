package vn.edu.iuh.bookingservice.mappers;

import org.mapstruct.*;
import vn.edu.iuh.bookingservice.dtos.requests.CartRequest;
import vn.edu.iuh.bookingservice.dtos.responses.CartResponse;
import vn.edu.iuh.bookingservice.entities.Cart;
import vn.edu.iuh.bookingservice.enums.CartStatus;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Timestamp.class, Instant.class, CartStatus.class})
public interface CartMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "status", expression = "java(request.getStatus() != null ? request.getStatus() : CartStatus.PENDING)")
    @Mapping(target = "createdAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(request))")
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    Cart toEntity(CartRequest request);

    @Mapping(target = "cartItems", source = "cartItems")
    CartResponse toResponse(Cart cart);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    void updateEntityFromRequest(CartRequest request, @MappingTarget Cart cart);

    default Double calculateTotalPrice(CartRequest request) {
        if (request.getCartItems() == null || request.getCartItems().isEmpty()) {
            return 0.0;
        }
        return request.getCartItems().stream()
                .mapToDouble(item -> item.getPrice() != null ? item.getPrice() : 0.0)
                .sum();
    }
}
