package org.example.management;

import org.example.db.PooledDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public RestaurantSalesDTO findTotalSales(Long restaurantId) {

        String sql = """
            SELECT
                r.id AS restaurant_id,
                r.description AS restaurant_name,
                COUNT(o.id) AS total_order_count,
                COALESCE(SUM(o.total_price), 0) AS total_sales_amount
            FROM restaurant r
            LEFT JOIN menu m ON r.id = m.restaurant_id
            LEFT JOIN `order` o ON o.menu_id = m.id
                AND o.status = 'COMPLETED'
            WHERE r.id = ?
            GROUP BY r.id, r.description
        """;

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, restaurantId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return RestaurantSalesDTO.builder()
                        .restaurantId(rs.getLong("restaurant_id"))
                        .restaurantName(rs.getString("restaurant_name"))
                        .overallSales(
                                RestaurantSalesDTO.OverallSales.builder()
                                        .orderCount(rs.getInt("total_order_count"))
                                        .salesAmount(rs.getInt("total_sales_amount"))
                                        .avgOrderPrice(
                                                rs.getInt("total_order_count") == 0
                                                        ? 0
                                                        : rs.getInt("total_sales_amount") / rs.getInt("total_order_count")
                                        )
                                        .build()
                        )
                        .build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<RestaurantSalesDTO.DailySales> findDailySales(Long restaurantId) {

        String sql = """
            SELECT
                DATE(o.created_at) AS order_date,
                COUNT(o.id) AS order_count,
                SUM(o.total_price) AS sales_amount
            FROM `order` o
            JOIN menu m ON o.menu_id = m.id
            WHERE m.restaurant_id = ?
              AND o.status = 'COMPLETED'
            GROUP BY DATE(o.created_at)
            ORDER BY order_date
        """;

        List<RestaurantSalesDTO.DailySales> list = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, restaurantId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(
                        RestaurantSalesDTO.DailySales.builder()
                                .date(rs.getDate("order_date").toLocalDate())
                                .orderCount(rs.getInt("order_count"))
                                .salesAmount(rs.getInt("sales_amount"))
                                .build()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<RestaurantSalesDTO.TimeSales> findTimeSales(Long restaurantId) {

        String sql = """
            SELECT
                HOUR(o.created_at) AS hour,
                COUNT(o.id) AS order_count,
                SUM(o.total_price) AS sales_amount
            FROM `order` o
            JOIN menu m ON o.menu_id = m.id
            WHERE m.restaurant_id = ?
              AND o.status = 'COMPLETED'
            GROUP BY HOUR(o.created_at)
            ORDER BY hour
        """;

        List<RestaurantSalesDTO.TimeSales> list = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, restaurantId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int hour = rs.getInt("hour");
                String timeRange = String.format("%02d:00~%02d:00", hour, hour + 1);

                list.add(
                        RestaurantSalesDTO.TimeSales.builder()
                                .timeRange(timeRange)
                                .orderCount(rs.getInt("order_count"))
                                .salesAmount(rs.getInt("sales_amount"))
                                .build()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
