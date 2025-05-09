package com.khuthon.garbage.domain.Fertilizer.Dto;

import com.khuthon.garbage.domain.Fertilizer.ENUM.Grade;
import lombok.Getter;

@Getter
public class FertilizerCreateRequestDto {
    private Float weightKg;
    private Grade grade;
}