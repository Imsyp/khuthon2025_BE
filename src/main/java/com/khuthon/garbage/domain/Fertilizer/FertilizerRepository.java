package com.khuthon.garbage.domain.Fertilizer;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FertilizerRepository extends MongoRepository<Fertilizer, String> {
}