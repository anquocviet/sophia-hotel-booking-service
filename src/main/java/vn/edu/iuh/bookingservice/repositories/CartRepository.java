package vn.edu.iuh.bookingservice.repositories;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import vn.edu.iuh.bookingservice.entities.Cart;

public interface CartRepository extends CrudRepository<Cart, UUID>, PagingAndSortingRepository<Cart, UUID> {
    Cart findByUserId(UUID userId);
}