package vn.edu.iuh.bookingservice.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import vn.edu.iuh.bookingservice.entities.CartItem;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends CrudRepository<CartItem, UUID>, PagingAndSortingRepository<CartItem, UUID> {
    Optional<CartItem> findByIdAndCartIdAndDeletedAtIsNull(UUID id, UUID cartId);
}
