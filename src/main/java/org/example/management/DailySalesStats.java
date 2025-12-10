package org.example.management;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailySalesStats {
    private int cafeteriaId;
    private String cafeteriaName;
    private LocalDate orderDate;
    private int totalOrders;
    private BigDecimal totalSales;
    private BigDecimal cardSales;
    private BigDecimal couponSales;
    private BigDecimal avgOrderAmount;
    private int studentCount;
    private int staffCount;

    public DailySalesStats() {}

    public int getCafeteriaId() {
        return cafeteriaId;
    }

    public String getCafeteriaName() {
        return cafeteriaName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public BigDecimal getCardSales() {
        return cardSales;
    }

    public BigDecimal getCouponSales() {
        return couponSales;
    }

    public BigDecimal getAvgOrderAmount() {
        return avgOrderAmount;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public int getStaffCount() {
        return staffCount;
    }

    public void setCafeteriaId(int cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }

    public void setCafeteriaName(String cafeteriaName) {
        this.cafeteriaName = cafeteriaName;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public void setCardSales(BigDecimal cardSales) {
        this.cardSales = cardSales;
    }

    public void setCouponSales(BigDecimal couponSales) {
        this.couponSales = couponSales;
    }

    public void setAvgOrderAmount(BigDecimal avgOrderAmount) {
        this.avgOrderAmount = avgOrderAmount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public void setStaffCount(int staffCount) {
        this.staffCount = staffCount;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | 주문: %d건 | 매출: %,d원 | 카드: %,d원 | 쿠폰: %,d원 | 평균: %,d원 | 학생: %d명 | 교직원: %d명",
                orderDate, cafeteriaName, totalOrders,
                totalSales.intValue(), cardSales.intValue(), couponSales.intValue(),
                avgOrderAmount.intValue(), studentCount, staffCount);
    }
}