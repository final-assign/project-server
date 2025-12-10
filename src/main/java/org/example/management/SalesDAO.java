package org.example.management;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.time.LocalDate;
import java.math.BigDecimal;


public class SalesDAO {
    private Connection conn;

    public SalesDAO(Connection conn) {
        this.conn = conn;
    }


    public List<DailySalesStats> getDailySalesByCafeteria(int cafeteriaId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT dss.*, c.cafeteria_name " +
                "FROM daily_sales_stats dss " +
                "JOIN cafeteria c ON dss.cafeteria_id = c.cafeteria_id " +
                "WHERE dss.cafeteria_id = ? AND dss.order_date BETWEEN ? AND ? " +
                "ORDER BY dss.order_date DESC";

        List<DailySalesStats> statsList = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeteriaId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    statsList.add(mapResultSetToDailySalesStats(rs));
                }
            }
        }
        return statsList;
    }

    public List<DailySalesStats> getAllDailySales(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT dss.*, c.cafeteria_name " +
                "FROM daily_sales_stats dss " +
                "JOIN cafeteria c ON dss.cafeteria_id = c.cafeteria_id " +
                "WHERE dss.order_date BETWEEN ? AND ? " +
                "ORDER BY dss.order_date DESC, dss.cafeteria_id";

        List<DailySalesStats> statsList = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    statsList.add(mapResultSetToDailySalesStats(rs));
                }
            }
        }
        return statsList;
    }

    public List<DailySalesStats> getDailySalesByDate(LocalDate date) throws SQLException {
        String sql = "SELECT dss.*, c.cafeteria_name " +
                "FROM daily_sales_stats dss " +
                "JOIN cafeteria c ON dss.cafeteria_id = c.cafeteria_id " +
                "WHERE dss.order_date = ? " +
                "ORDER BY dss.total_sales DESC";

        List<DailySalesStats> statsList = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    statsList.add(mapResultSetToDailySalesStats(rs));
                }
            }
        }
        return statsList;
    }

    public PeriodSalesSummary getPeriodSalesSummary(int cafeteriaId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT " +
                "c.cafeteria_id, c.cafeteria_name, " +
                "COUNT(*) as total_orders, " +
                "SUM(o.total_amount) as total_sales, " +
                "SUM(o.card_amount) as card_sales, " +
                "SUM(o.coupon_amount) as coupon_sales, " +
                "AVG(o.total_amount) as avg_order_amount, " +
                "COUNT(CASE WHEN o.user_type = '학생' THEN 1 END) as total_students, " +
                "COUNT(CASE WHEN o.user_type = '교직원' THEN 1 END) as total_staff " +
                "FROM orders o " +
                "JOIN cafeteria c ON o.cafeteria_id = c.cafeteria_id " +
                "WHERE o.cafeteria_id = ? AND o.order_date BETWEEN ? AND ? AND o.status = '완료' " +
                "GROUP BY c.cafeteria_id, c.cafeteria_name";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeteriaId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    PeriodSalesSummary summary = new PeriodSalesSummary();
                    summary.setCafeteriaId(rs.getInt("cafeteria_id"));
                    summary.setCafeteriaName(rs.getString("cafeteria_name"));
                    summary.setStartDate(startDate);
                    summary.setEndDate(endDate);
                    summary.setTotalOrders(rs.getInt("total_orders"));
                    summary.setTotalSales(rs.getBigDecimal("total_sales"));
                    summary.setCardSales(rs.getBigDecimal("card_sales"));
                    summary.setCouponSales(rs.getBigDecimal("coupon_sales"));
                    summary.setAvgOrderAmount(rs.getBigDecimal("avg_order_amount"));
                    summary.setTotalStudents(rs.getInt("total_students"));
                    summary.setTotalStaff(rs.getInt("total_staff"));

                    // 일평균 매출 계산
                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
                    BigDecimal avgDailySales = summary.getTotalSales().divide(
                            new BigDecimal(daysBetween), 2, BigDecimal.ROUND_HALF_UP);
                    summary.setAvgDailySales(avgDailySales);

                    return summary;
                }
            }
        }
        return null;
    }

    public List<PeriodSalesSummary> getAllPeriodSalesSummary(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT " +
                "c.cafeteria_id, c.cafeteria_name, " +
                "COUNT(*) as total_orders, " +
                "SUM(o.total_amount) as total_sales, " +
                "SUM(o.card_amount) as card_sales, " +
                "SUM(o.coupon_amount) as coupon_sales, " +
                "AVG(o.total_amount) as avg_order_amount, " +
                "COUNT(CASE WHEN o.user_type = '학생' THEN 1 END) as total_students, " +
                "COUNT(CASE WHEN o.user_type = '교직원' THEN 1 END) as total_staff " +
                "FROM orders o " +
                "JOIN cafeteria c ON o.cafeteria_id = c.cafeteria_id " +
                "WHERE o.order_date BETWEEN ? AND ? AND o.status = '완료' " +
                "GROUP BY c.cafeteria_id, c.cafeteria_name " +
                "ORDER BY total_sales DESC";

        List<PeriodSalesSummary> summaries = new ArrayList<>();
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PeriodSalesSummary summary = new PeriodSalesSummary();
                    summary.setCafeteriaId(rs.getInt("cafeteria_id"));
                    summary.setCafeteriaName(rs.getString("cafeteria_name"));
                    summary.setStartDate(startDate);
                    summary.setEndDate(endDate);
                    summary.setTotalOrders(rs.getInt("total_orders"));
                    summary.setTotalSales(rs.getBigDecimal("total_sales"));
                    summary.setCardSales(rs.getBigDecimal("card_sales"));
                    summary.setCouponSales(rs.getBigDecimal("coupon_sales"));
                    summary.setAvgOrderAmount(rs.getBigDecimal("avg_order_amount"));
                    summary.setTotalStudents(rs.getInt("total_students"));
                    summary.setTotalStaff(rs.getInt("total_staff"));

                    // 일평균 매출 계산
                    BigDecimal avgDailySales = summary.getTotalSales().divide(
                            new BigDecimal(daysBetween), 2, BigDecimal.ROUND_HALF_UP);
                    summary.setAvgDailySales(avgDailySales);

                    summaries.add(summary);
                }
            }
        }
        return summaries;
    }

    public List<HourlyUsageStats> getHourlyUsageStats(int cafeteriaId, LocalDate date) throws SQLException {
        String sql = "SELECT hus.*, c.cafeteria_name " +
                "FROM hourly_usage_stats hus " +
                "JOIN cafeteria c ON hus.cafeteria_id = c.cafeteria_id " +
                "WHERE hus.cafeteria_id = ? AND hus.order_date = ? " +
                "ORDER BY hus.hour_slot";

        List<HourlyUsageStats> statsList = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeteriaId);
            pstmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    statsList.add(mapResultSetToHourlyUsageStats(rs));
                }
            }
        }
        return statsList;
    }

    public List<HourlyUsageStats> getAvgHourlyUsageStats(int cafeteriaId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT " +
                "c.cafeteria_id, c.cafeteria_name, " +
                "HOUR(o.order_time) as hour_slot, " +
                "COUNT(*) as total_orders, " +
                "SUM(o.total_amount) as total_sales " +
                "FROM orders o " +
                "JOIN cafeteria c ON o.cafeteria_id = c.cafeteria_id " +
                "WHERE o.cafeteria_id = ? AND o.order_date BETWEEN ? AND ? AND o.status = '완료' " +
                "GROUP BY c.cafeteria_id, c.cafeteria_name, HOUR(o.order_time) " +
                "ORDER BY hour_slot";

        List<HourlyUsageStats> statsList = new ArrayList<>();
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeteriaId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HourlyUsageStats stats = new HourlyUsageStats();
                    stats.setCafeteriaId(rs.getInt("cafeteria_id"));
                    stats.setCafeteriaName(rs.getString("cafeteria_name"));
                    stats.setHourSlot(rs.getInt("hour_slot"));

                    // 평균 계산
                    int avgOrders = (int) Math.round(rs.getInt("total_orders") / (double) daysBetween);
                    BigDecimal avgSales = rs.getBigDecimal("total_sales").divide(
                            new BigDecimal(daysBetween), 2, BigDecimal.ROUND_HALF_UP);

                    stats.setOrderCount(avgOrders);
                    stats.setHourSales(avgSales);

                    statsList.add(stats);
                }
            }
        }
        return statsList;
    }


    public Map<Integer, String> getCafeteriaList() throws SQLException {
        String sql = "SELECT cafeteria_id, cafeteria_name FROM cafeteria ORDER BY cafeteria_id";
        Map<Integer, String> cafeterias = new LinkedHashMap<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cafeterias.put(rs.getInt("cafeteria_id"), rs.getString("cafeteria_name"));
            }
        }
        return cafeterias;
    }
    
    private DailySalesStats mapResultSetToDailySalesStats(ResultSet rs) throws SQLException {
        DailySalesStats stats = new DailySalesStats();
        stats.setCafeteriaId(rs.getInt("cafeteria_id"));
        stats.setCafeteriaName(rs.getString("cafeteria_name"));
        stats.setOrderDate(rs.getDate("order_date").toLocalDate());
        stats.setTotalOrders(rs.getInt("total_orders"));
        stats.setTotalSales(rs.getBigDecimal("total_sales"));
        stats.setCardSales(rs.getBigDecimal("card_sales"));
        stats.setCouponSales(rs.getBigDecimal("coupon_sales"));
        stats.setAvgOrderAmount(rs.getBigDecimal("avg_order_amount"));
        stats.setStudentCount(rs.getInt("student_count"));
        stats.setStaffCount(rs.getInt("staff_count"));
        return stats;
    }

    private HourlyUsageStats mapResultSetToHourlyUsageStats(ResultSet rs) throws SQLException {
        HourlyUsageStats stats = new HourlyUsageStats();
        stats.setCafeteriaId(rs.getInt("cafeteria_id"));
        stats.setCafeteriaName(rs.getString("cafeteria_name"));
        stats.setOrderDate(rs.getDate("order_date").toLocalDate());
        stats.setHourSlot(rs.getInt("hour_slot"));
        stats.setOrderCount(rs.getInt("order_count"));
        stats.setHourSales(rs.getBigDecimal("hour_sales"));
        return stats;
    }
}
