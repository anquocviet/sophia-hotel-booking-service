package vn.edu.iuh.bookingservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.bookingservice.dtos.requests.TransactionRequest;
import vn.edu.iuh.bookingservice.dtos.responses.TransactionResponse;
import vn.edu.iuh.bookingservice.enums.PaymentStatus;
import vn.edu.iuh.bookingservice.services.TransactionService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        return new ResponseEntity<>(transactionService.createTransaction(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable UUID id, @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByPaymentStatus(@PathVariable PaymentStatus status) {
        return ResponseEntity.ok(transactionService.getTransactionsByPaymentStatus(status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<TransactionResponse> getTransactionByCartId(@PathVariable UUID cartId) {
        return ResponseEntity.ok(transactionService.getTransactionByCartId(cartId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> countAllTransactions() {
        // lay size getAllTransactions
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok((long) transactions.size());
    }


  
    /**
     * b. Thống kê doanh thu (Revenue Statistics)
     * API: GET /api/statistics/revenue?from=YYYY-MM-DD&to=YYYY-MM-DD
     * Dữ liệu trả về: Doanh thu theo thời gian/khách sạn.
     * Mục đích: Theo dõi hiệu quả kinh doanh, dự báo doanh thu.
     * @param from
     * @param to
     * @return
     */
    @GetMapping("/statistics/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(transactionService.getRevenueStatistics(from, to));
    }


    /**
     *    /**
     *      * d. Thống kê booking (Booking Statistics)
     *      * API: GET /api/statistics/bookings?from=YYYY-MM-DD&to=YYYY-MM-DD
     *      * Dữ liệu trả về: Số lượng booking mới, booking hủy, booking hoàn thành, theo thời gian/khách sạn.
     *      * Mục đích: Theo dõi hành vi khách hàng, dự báo nhu cầu.
     *      * @param fromDateStr
     *      * @param toDateStr
     *      * @return
     * @param from
     * @param to
     * @return
     */
    @GetMapping("/statistics/bookings")
    public ResponseEntity<Map<String, Object>> getBookingStatistics(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(transactionService.getBookingStatistics(from, to));
    }
}
