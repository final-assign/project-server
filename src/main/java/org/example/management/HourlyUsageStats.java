package org.example.management;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HourlyUsageStats {
    private int cafeteriaId;
    private String cafeteriaName;
    private LocalDate orderDate;
    private int hourSlot;
    private int orderCount;
    private BigDecimal hourSales;

    public HourlyUsageStats() {}

    public int getCafeteriaId() {
        return cafeteriaId;
    }

    public String getCafeteriaName() {
        return cafeteriaName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public int getHourSlot() {
        return hourSlot;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public BigDecimal getHourSales() {
        return hourSales;
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

    public void setHourSlot(int hourSlot) {
        this.hourSlot = hourSlot;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public void setHourSales(BigDecimal hourSales) {
        this.hourSales = hourSales;
    }

    public String getTimeSlotString() {
        return String.format("%02d:00-%02d:59", hourSlot, hourSlot);
    }

    @Override
    public String toString() {
        return String.format("%s | %d건 | %,d원",
                getTimeSlotString(), orderCount, hourSales.intValue());
    }
}