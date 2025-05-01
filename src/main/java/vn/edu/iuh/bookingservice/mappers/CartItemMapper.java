package vn.edu.iuh.bookingservice.mappers;

import org.mapstruct.*;
import vn.edu.iuh.bookingservice.dtos.requests.CartItemRequest;
import vn.edu.iuh.bookingservice.dtos.responses.CartItemResponse;
import vn.edu.iuh.bookingservice.entities.Cart;
import vn.edu.iuh.bookingservice.entities.CartItem;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Timestamp.class, Instant.class})
public interface CartItemMapper {

    @Mapping(target = "price", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", source = "cart")
    @Mapping(target = "roomId", source = "request.roomId")
    @Mapping(target = "checkinDate", source = "request.checkinDate")
    @Mapping(target = "checkoutDate", source = "request.checkoutDate")
    @Mapping(target = "createdAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "deletedAt", ignore = true)
    CartItem toEntity(CartItemRequest request, Cart cart);

    CartItemResponse toResponse(CartItem cartItem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "deletedAt", expression = "java(null)")
    @Mapping(target = "price", ignore = true)
    void updateEntityFromRequest(CartItemRequest request, @MappingTarget CartItem cartItem);
}
