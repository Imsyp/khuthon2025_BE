package com.khuthon.garbage.domain.Fertilizer;

import com.khuthon.garbage.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fertilizers")
public class FertilizerController {

    private final FertilizerService fertilizerService;

    @PostMapping
    public ResponseEntity<ApiResponse<Fertilizer>> createFertilizer(@RequestBody Fertilizer fertilizer) {
        Fertilizer saved = fertilizerService.save(fertilizer);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fertilizer created successfully", saved));
    }

    @GetMapping("/{fertId}")
    public ResponseEntity<ApiResponse<Fertilizer>> getFertilizerById(@PathVariable String fertId) {
        return fertilizerService.findById(fertId)
                .map(f -> ResponseEntity.ok(new ApiResponse<>(true, "Fertilizer fetched successfully", f)))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse<>(false, "Fertilizer not found", null)));
    }
}