package com.khuthon.garbage.domain.Transaction;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByTransactionIdIn(List<String> ids);
}