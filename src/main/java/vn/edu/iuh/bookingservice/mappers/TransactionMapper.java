package vn.edu.iuh.bookingservice.mappers;

import org.mapstruct.*;
import vn.edu.iuh.bookingservice.dtos.requests.TransactionRequest;
import vn.edu.iuh.bookingservice.dtos.responses.TransactionResponse;
import vn.edu.iuh.bookingservice.entities.Cart;
import vn.edu.iuh.bookingservice.entities.Transaction;
import vn.edu.iuh.bookingservice.enums.PaymentStatus;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {Timestamp.class, Instant.class, UUID.class, PaymentStatus.class})
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionId", expression = "java(generateTransactionId())")
    @Mapping(target = "cart", source = "cart")
    @Mapping(target = "paymentMethod", source = "request.paymentMethod")
    @Mapping(target = "paymentStatus", expression = "java(request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.PENDING)")
    @Mapping(target = "paidAmount", source = "request.paidAmount")
    @Mapping(target = "createAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "deletedAt", ignore = true)
    Transaction toEntity(TransactionRequest request, Cart cart);
    
    @Mapping(target = "cartId", expression = "java(transaction.getCart() != null ? transaction.getCart().getId() : null)")
    @Mapping(target = "createdAt", source = "createAt")
    TransactionResponse toResponse(Transaction transaction);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    void updateEntityFromRequest(TransactionRequest request, @MappingTarget Transaction transaction);
    
    default String generateTransactionId() {
        return "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
