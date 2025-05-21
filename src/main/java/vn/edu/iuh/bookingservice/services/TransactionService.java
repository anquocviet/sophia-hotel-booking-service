package vn.edu.iuh.bookingservice.services;

import java.util.List;
import vn.edu.iuh.bookingservice.dtos.requests.TransactionRequest;
import vn.edu.iuh.bookingservice.dtos.responses.TransactionResponse;

import java.util.Map;
import java.util.UUID;
import vn.edu.iuh.bookingservice.enums.PaymentStatus;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);
    TransactionResponse getTransactionById(UUID id);
    List<TransactionResponse> getAllTransactions();
    TransactionResponse updateTransaction(UUID id, TransactionRequest request);
    void deleteTransaction(UUID id);
    TransactionResponse getTransactionByCartId(UUID cartId);

    List<TransactionResponse> getTransactionsByCartId(UUID cartId);

    List<TransactionResponse> getTransactionsByPaymentStatus(PaymentStatus status);
    
    List<TransactionResponse> getTransactionsByUserId(UUID userId);
    long countAllTransactions();
    Map<String, Object> getRevenueStatistics(String fromDate, String toDate);
    Map<String, Object> getBookingStatistics(String fromDate, String toDate);
}