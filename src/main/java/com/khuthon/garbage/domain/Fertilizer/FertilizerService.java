package com.khuthon.garbage.domain.Fertilizer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FertilizerService {

    private final FertilizerRepository fertilizerRepository;

    public Fertilizer save(Fertilizer fertilizer) {
        return fertilizerRepository.save(fertilizer);
    }

    public Optional<Fertilizer> findById(String fertId) {
        return fertilizerRepository.findById(fertId);
    }
}