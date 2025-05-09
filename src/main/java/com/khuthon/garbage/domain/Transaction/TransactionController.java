package com.khuthon.garbage.domain.Transaction;

import com.khuthon.garbage.domain.Post.PostService;
import com.khuthon.garbage.domain.User.User;
import com.khuthon.garbage.domain.User.UserRepository;
import com.khuthon.garbage.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // 거래 생성 API
    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(@PathVariable String postId, HttpSession session) {
        return transactionService.createTransaction(postId, session);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMyTransactions(HttpSession session) {
        List<Transaction> transactions = transactionService.getMyTransactions(session);
        return ResponseEntity.ok(new ApiResponse<>(true, "거래 목록 조회 성공", transactions));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<?>> getTransactionDetail(@PathVariable String transactionId) {
        Transaction transaction = transactionService.getTransactionDetail(transactionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "거래 상세 조회 성공", transaction));
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<?>> updateTransactionStatus(
            @PathVariable String transactionId,
            @RequestBody Map<String, String> request
    ) {
        String status = request.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "변경할 상태(status)가 필요합니다.", null)
            );
        }
        Transaction updatedTransaction = transactionService.updateTransactionStatus(transactionId, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "거래 상태 변경 성공", updatedTransaction));
    }
}