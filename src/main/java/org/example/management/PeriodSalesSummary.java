package org.example.management;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PeriodSalesSummary {
    private int cafeteriaId;
    private String cafeteriaName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalOrders;
    private BigDecimal totalSales;
    private BigDecimal cardSales;
    private BigDecimal couponSales;
    private BigDecimal avgDailySales;
    private BigDecimal avgOrderAmount;
    private int totalStudents;
    private int totalStaff;

    public PeriodSalesSummary() {}

    public int getCafeteriaId() {
        return cafeteriaId;
    }

    public String getCafeteriaName() {
        return cafeteriaName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
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

    public BigDecimal getAvgDailySales() {
        return avgDailySales;
    }

    public BigDecimal getAvgOrderAmount() {
        return avgOrderAmount;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public int getTotalStaff() {
        return totalStaff;
    }

    public void setCafeteriaId(int cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }

    public void setCafeteriaName(String cafeteriaName) {
        this.cafeteriaName = cafeteriaName;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public void setAvgDailySales(BigDecimal avgDailySales) {
        this.avgDailySales = avgDailySales;
    }

    public void setAvgOrderAmount(BigDecimal avgOrderAmount) {
        this.avgOrderAmount = avgOrderAmount;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public void setTotalStaff(int totalStaff) {
        this.totalStaff = totalStaff;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s ~ %s\n" +
                        "  총 주문: %d건 | 총 매출: %,d원\n" +
                        "  카드: %,d원 | 쿠폰: %,d원\n" +
                        "  일평균 매출: %,d원 | 건당 평균: %,d원\n" +
                        "  학생: %d명 | 교직원: %d명",
                cafeteriaName, startDate, endDate,
                totalOrders, totalSales.intValue(),
                cardSales.intValue(), couponSales.intValue(),
                avgDailySales.intValue(), avgOrderAmount.intValue(),
                totalStudents, totalStaff);
    }
}
