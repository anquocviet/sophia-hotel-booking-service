package vn.edu.iuh.bookingservice.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import vn.edu.iuh.bookingservice.entities.Transaction;
import vn.edu.iuh.bookingservice.enums.PaymentStatus;

public interface TransactionRepository extends CrudRepository<Transaction, UUID>, PagingAndSortingRepository<Transaction, UUID> {
    Transaction findByCart_Id(UUID cartId);

    List<Transaction> findAllByPaymentStatus(PaymentStatus paymentStatus);

    List<Transaction> findAllByCart_Id(UUID cartId);
    
    List<Transaction> findAllByCart_UserId(UUID userId);
    
    long count();
}