package com.khuthon.garbage.domain.Fertilizer.ENUM;

public enum Grade {
    A(1000),
    B(800),
    C(500),
    F(200);

    private final int pricePerKg;

    Grade(int pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public int getPricePerKg() {
        return pricePerKg;
    }
}