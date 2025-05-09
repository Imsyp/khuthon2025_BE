package com.khuthon.garbage.domain.Transaction;

import com.khuthon.garbage.domain.Transaction.ENUM.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/*
    구매자(user)가 post에 거래요청을 보내면
    판매자(user)의 point를 Fertilizer의 가격만큼 증가
    Post의 isSold = Ture로 변경
    구매자(user)의 transactionIds에 해당 transaction 추가
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Transactions")
public class Transaction {
    @Id
    private String transactionId;
    private String postId;
    private Status status;
    private LocalDateTime createdAt;

    public Transaction(String postId) {
        this.postId = postId;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
