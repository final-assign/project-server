package org.example.management;

import java.math.BigDecimal;

// ============ CafeteriaPrice DTO ============
public class CafeteriaPrice {
    private int priceId;
    private int cafeteriaId;
    private String cafeteriaName;
    private String mealTime;
    private BigDecimal price;

    // 기본 생성자
    public CafeteriaPrice() {}

    // 매개변수 생성자
    public CafeteriaPrice(int cafeteriaId, String mealTime, BigDecimal price) {
        this.cafeteriaId = cafeteriaId;
        this.mealTime = mealTime;
        this.price = price;
    }

    // Getters
    public int getPriceId() {
        return priceId;
    }

    public int getCafeteriaId() {
        return cafeteriaId;
    }

    public String getCafeteriaName() {
        return cafeteriaName;
    }

    public String getMealTime() {
        return mealTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    // Setters
    public void setPriceId(int priceId) {
        this.priceId = priceId;
    }

    public void setCafeteriaId(int cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }

    public void setCafeteriaName(String cafeteriaName) {
        this.cafeteriaName = cafeteriaName;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %,d원",
                cafeteriaName, mealTime, price.intValue());
    }
}
