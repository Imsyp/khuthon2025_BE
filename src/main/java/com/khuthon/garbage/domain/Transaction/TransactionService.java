package com.khuthon.garbage.domain.Transaction;

import com.khuthon.garbage.domain.Post.Post;
import com.khuthon.garbage.domain.Post.PostRepository;
import com.khuthon.garbage.domain.Transaction.ENUM.Status;
import com.khuthon.garbage.domain.User.User;
import com.khuthon.garbage.domain.User.UserRepository;
import com.khuthon.garbage.domain.Fertilizer.Fertilizer;
import com.khuthon.garbage.domain.Fertilizer.FertilizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;

import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FertilizerRepository fertilizerRepository;

    private final MongoTemplate mongoTemplate;

    public Transaction createTransaction(String postId, String userId, HttpSession session) {
        User buyer = userRepository.findById(userId).get();

        // Post 정보 가져오기
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("포스트를 찾을 수 없습니다."));
        if (post.getIsSold()) {
            throw new RuntimeException("이 포스트는 이미 판매되었습니다.");
        }

        // Fertilizer 정보 가져오기
        Fertilizer fertilizer = fertilizerRepository.findById(post.getFertId()).orElseThrow(() -> new RuntimeException("비료를 찾을 수 없습니다."));

        // 거래 생성
        Transaction transaction = new Transaction(postId);
        transactionRepository.save(transaction);

        // 업데이트 쿼리 직접 사용
        Query query = new Query(Criteria.where("_id").is(post.getSellerId()));
        Update update = new Update().inc("point", fertilizer.getPrice());
        User updatedSeller = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                User.class
        );
        System.out.println("Updated seller's point via query: " + updatedSeller.getPoint());

        // 구매자의 거래 기록에 추가
        buyer.setTransactionIds(appendToArray(buyer.getTransactionIds(), transaction.getTransactionId()));
        userRepository.save(buyer);

        // 포스트의 판매 상태 업데이트
        post.setIsSold(true);
        postRepository.save(post);

        return transaction;
    }

    private String[] appendToArray(String[] array, String newValue) {
        if (array == null) {
            return new String[] { newValue };
        }
        String[] newArray = new String[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = newValue;
        return newArray;
    }

    public List<Transaction> getMyTransactions(HttpSession session, String userId) {
        User user = userRepository.findById(userId).get();

        return transactionRepository.findByTransactionIdIn(
                user.getTransactionIds() != null ? Arrays.asList(user.getTransactionIds()) : Collections.emptyList()
        );
    }

    public Transaction getTransactionDetail(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("해당 거래를 찾을 수 없습니다."));
    }

    public Transaction updateTransactionStatus(String transactionId, String newStatus) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("해당 거래를 찾을 수 없습니다."));

        try {
            // String을 Status enum으로 변환
            Status status = Status.valueOf(newStatus.toUpperCase());
            transaction.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 상태 값입니다.");
        }

        return transactionRepository.save(transaction);
    }
}