package vn.edu.iuh.bookingservice.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.iuh.bookingservice.dtos.requests.TransactionRequest;
import vn.edu.iuh.bookingservice.dtos.responses.TransactionResponse;
import vn.edu.iuh.bookingservice.entities.Cart;
import vn.edu.iuh.bookingservice.entities.Transaction;
import vn.edu.iuh.bookingservice.exceptions.BadRequestException;
import vn.edu.iuh.bookingservice.exceptions.ResourceNotFoundException;
import vn.edu.iuh.bookingservice.mappers.TransactionMapper;
import vn.edu.iuh.bookingservice.repositories.CartRepository;
import vn.edu.iuh.bookingservice.repositories.TransactionRepository;
import vn.edu.iuh.bookingservice.services.TransactionService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CartRepository cartRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        validateTransactionRequest(request);
        
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", request.getCartId()));
        
        Transaction transaction = transactionMapper.toEntity(request, cart);
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    public TransactionResponse getTransactionById(UUID id) {
        Transaction transaction = findTransactionById(id);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public Page<TransactionResponse> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(transactionMapper::toResponse);
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
        
        if (request.getPaidAmount() == null || request.getPaidAmount() <= 0) {
            throw new BadRequestException("Paid amount must be greater than zero");
        }
    }
}
