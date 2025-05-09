package com.khuthon.garbage.domain.Fertilizer;

import com.khuthon.garbage.domain.Fertilizer.ENUM.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "Fertilizers")
public class Fertilizer {
    @Id
    private String fertId;
    private Float weightKg;
    private Grade grade;
    private Integer price; // kg * 단가

    public Fertilizer(String fertId, Float weightKg, Grade grade) {
        this.fertId = fertId;
        this.weightKg = weightKg;
        this.grade = grade;
        this.price = calculatePrice(weightKg, grade);
    }

    private int calculatePrice(float weightKg, Grade grade) {
        return Math.round(weightKg * grade.getPricePerKg());
    }
}