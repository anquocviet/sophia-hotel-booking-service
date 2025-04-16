package vn.edu.iuh.bookingservice.repositories;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import vn.edu.iuh.bookingservice.entities.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, UUID>, PagingAndSortingRepository<Transaction, UUID> {
    Transaction findByCart_Id(UUID cartId);
}