package vn.edu.iuh.bookingservice.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.bookingservice.dtos.requests.PaymentRequest;
import vn.edu.iuh.bookingservice.dtos.requests.TransactionRequest;
import vn.edu.iuh.bookingservice.dtos.requests.UpdateRoomStatusRequest;
import vn.edu.iuh.bookingservice.dtos.responses.PaymentResponse;
import vn.edu.iuh.bookingservice.dtos.responses.TransactionResponse;
import vn.edu.iuh.bookingservice.entities.Cart;
import vn.edu.iuh.bookingservice.entities.Transaction;
import vn.edu.iuh.bookingservice.enums.PaymentStatus;
import vn.edu.iuh.bookingservice.exceptions.BadRequestException;
import vn.edu.iuh.bookingservice.exceptions.ResourceNotFoundException;
import vn.edu.iuh.bookingservice.mappers.TransactionMapper;
import vn.edu.iuh.bookingservice.repositories.CartRepository;
import vn.edu.iuh.bookingservice.repositories.TransactionRepository;
import vn.edu.iuh.bookingservice.services.TransactionService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CartRepository cartRepository;
    private final TransactionMapper transactionMapper;
    private static final String PAYMENT_SERVICE_URL = "http://payment-service:8084/api/v1";
    private static final String HOTEL_SERVICE_URL = "http://hotel-service:8082/api/v1";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        validateTransactionRequest(request);
        
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", request.getCartId()));
        
        Transaction transaction = transactionMapper.toEntity(request, cart);
        transaction.setPaidAmount(cart.getTotalPrice());
        Transaction savedTransaction = transactionRepository.save(transaction);
        cart.setTransaction(savedTransaction);
        cart.setDeletedAt(Timestamp.from(Instant.now()));
        cartRepository.save(cart);
        // Call payment service to process the payment
        PaymentResponse paymentResponse = createPayment(savedTransaction, request.getCardId());
        if (paymentResponse.getStatus() != PaymentStatus.COMPLETED) {
            throw new BadRequestException("Payment failed: " + paymentResponse.getStatus());
        }
        transaction.setPaymentStatus(paymentResponse.getStatus());
        updateRoomStatus(cart);
        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    public TransactionResponse getTransactionById(UUID id) {
        Transaction transaction = findTransactionById(id);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        List<Transaction> transactions = (List<Transaction>) transactionRepository.findAll();
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(UUID id, TransactionRequest request) {
        validateTransactionRequest(request);
        
        Transaction transaction = findTransactionById(id);
        
        // Check if the cart is changing
        if (transaction.getCart() == null || !transaction.getCart().getId().equals(request.getCartId())) {
            Cart cart = cartRepository.findById(request.getCartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", request.getCartId()));
            transaction.setCart(cart);
        }
        
        // Apply other updates
        transactionMapper.updateEntityFromRequest(request, transaction);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        
        return transactionMapper.toResponse(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(UUID id) {
        Transaction transaction = findTransactionById(id);
        transaction.setDeletedAt(Timestamp.from(Instant.now()));
        transactionRepository.save(transaction);
    }

    @Override
    public TransactionResponse getTransactionByCartId(UUID cartId) {
        Transaction transaction = transactionRepository.findByCart_Id(cartId);
        if (transaction == null) {
            throw new ResourceNotFoundException("Transaction", "cartId", cartId);
        }
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getTransactionsByUserId(UUID userId) {
        List<Transaction> transactions = transactionRepository.findAllByCart_UserId(userId);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactionsByCartId(UUID cartId) {
        List<Transaction> transactions = transactionRepository.findAllByCart_Id(cartId);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactionsByPaymentStatus(PaymentStatus status) {
        List<Transaction> transactions = transactionRepository.findAllByPaymentStatus(status);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    private Transaction findTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
    }
    
    private void validateTransactionRequest(TransactionRequest request) {
        if (request.getCartId() == null) {
            throw new BadRequestException("Cart ID is required");
        }
        
        if (request.getPaymentMethod() == null) {
            throw new BadRequestException("Payment method is required");
        }
    }

    private PaymentResponse createPayment(Transaction transaction, UUID cardId) {
        try {
            String url = PAYMENT_SERVICE_URL + "/payments";
            PaymentRequest paymentRequest = new PaymentRequest(
                    transaction.getCart().getUserId(),
                    "VND",
                    transaction.getPaidAmount(),
                    transaction.getPaymentMethod(),
                    cardId
            );

            // For debugging: log raw response
            ResponseEntity<String> rawResponse = restTemplate.exchange(
                  url,
                  HttpMethod.POST,
                  new HttpEntity<>(paymentRequest),
                  String.class
            );
            log.debug("Raw response: {}", rawResponse.getBody());

            // Then try to convert to UserDto
            PaymentResponse paymentDto = objectMapper.readValue(rawResponse.getBody(), PaymentResponse.class);
            return paymentDto;
        } catch (Exception e) {
            log.error("Error create payment", e);
            throw new RuntimeException("Failed to create payment data: " + e.getMessage());
        }
    }

    private boolean updateRoomStatus(Cart cart) {
        try {
            String url = HOTEL_SERVICE_URL + "/rooms/updateRoomStatus";
            cart.getCartItems().forEach(item -> {
                UpdateRoomStatusRequest updateRoomStatusRequest = new UpdateRoomStatusRequest(
                        item.getHotelId().toString(),
                        item.getRoomId().toString(),
                        "BOOKED"
                );

                // For debugging: log raw response
                ResponseEntity<String> rawResponse = restTemplate.exchange(
                        url,
                        HttpMethod.PUT,
                        new HttpEntity<>(updateRoomStatusRequest),
                        String.class
                );
                log.debug("Raw response: {}", rawResponse.getBody());
            });

        } catch (Exception e) {
            log.error("Error update room status", e);
            throw new RuntimeException("Failed to update room status: " + e.getMessage());
        }
        return true;
    }
}
