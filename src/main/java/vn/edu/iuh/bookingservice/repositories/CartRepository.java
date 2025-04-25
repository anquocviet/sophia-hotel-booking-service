package vn.edu.iuh.bookingservice.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import vn.edu.iuh.bookingservice.entities.Cart;

public interface CartRepository extends CrudRepository<Cart, UUID>, PagingAndSortingRepository<Cart, UUID> {
//    Cart findByUserId(UUID userId);

    List<Cart> findByUserId(UUID userId);
    
    List<Cart> findByUserIdAndDeletedAtIsNull(UUID userId);
    
    @Query("SELECT c FROM Cart c WHERE c.id = ?1 AND c.deletedAt IS NULL")
    Optional<Cart> findByIdAndDeletedAtIsNull(UUID id);
    
    Page<Cart> findAllByDeletedAtIsNull(Pageable pageable);
}