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
    @Mapping(target = "createdAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "paidAmount", ignore = true)
    @Mapping(target = "paymentStatus", expression = "java(PaymentStatus.PENDING)")
    Transaction toEntity(TransactionRequest request, Cart cart);
    
    TransactionResponse toResponse(Transaction transaction);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(Timestamp.from(Instant.now()))")
    @Mapping(target = "deletedAt", expression = "java(null)")
    void updateEntityFromRequest(TransactionRequest request, @MappingTarget Transaction transaction);
    
    default String generateTransactionId() {
        return "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
