package org.example.order.order_request;

import org.example.db.PooledDataSource;
import org.example.order.OrderStatus;
import org.example.order.PurchaseType;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO {
    private final DataSource ds = PooledDataSource.getDataSource();

    public List<OrderDetail> findByUserId(long userId, LocalDateTime startAt, LocalDateTime endAt) {
        List<OrderDetail> orders = new ArrayList<>();

        //메뉴명과 쿠폰 검색을 위해 join
        String sql = "SELECT o.*, m.menu_name, c.price, u.school_id, r.name " +
                "FROM ORDERS o " +
                "JOIN USER u ON o.user_id = u.id " +
                "JOIN MENU m ON o.menu_id = m.id " +
                "JOIN RESTAURANT r ON m.restaurant_id = r.id " +
                "LEFT JOIN COUPON c ON o.coupon_id = c.id " +
                "WHERE o.user_id = ? AND o.created_at BETWEEN ? AND ? " +
                "ORDER BY o.created_at DESC";

        try (Connection conn = ds.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setLong(1, userId);
            pstmt.setTimestamp(2, Timestamp.valueOf(startAt));
            pstmt.setTimestamp(3, Timestamp.valueOf(endAt));
            //userid, startAt, endAt 활용 조회

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    //order 추출
                    OrderDetail order = OrderDetail.builder()
                            .id(rs.getLong("id"))
                            .purchaseType(PurchaseType.valueOf(rs.getString("purchase_type")))
                            .status(OrderStatus.valueOf(rs.getString("status")))
                            .purchasePrice(rs.getInt("total_price"))
                            .createdAt(LocalDateTime.parse(rs.getString("created_at")))
                            .menuName(rs.getString("menu_name"))
                            .couponPrice(rs.getInt("price"))
                            .schoolId(rs.getString("school_id"))
                            .restaurantName(rs.getString("name"))
                            .build();

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<OrderDetail> findByRestaurantAndTime(Long restaurantId, LocalDateTime startAt, LocalDateTime endAt) {
        List<OrderDetail> orders = new ArrayList<>();

        // 식당 ID 일치하고, 주문 시간이 범위 내인 데이터 조회
        String sql = "SELECT o.*, m.menu_name, c.coupon_price " +
                "FROM ORDERS o " +
                "JOIN MENU m ON o.menu_id = m.id " +
                "LEFT JOIN COUPON c ON o.coupon_id = c.id " +
                "WHERE m.restaurant_id = ? AND o.created_at BETWEEN ? AND ? " +
                "ORDER BY o.created_at DESC";

        try (Connection conn = ds.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setLong(1, restaurantId);
            pstmt.setTimestamp(2, Timestamp.valueOf(startAt));
            pstmt.setTimestamp(3, Timestamp.valueOf(endAt));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    //order 추출
                    OrderDetail order = OrderDetail.builder()
                            .id(rs.getLong("id"))
                            .purchaseType(PurchaseType.valueOf(rs.getString("purchase_type")))
                            .status(OrderStatus.valueOf(rs.getString("status")))
                            .purchasePrice(rs.getInt("total_price"))
                            .createdAt(LocalDateTime.parse(rs.getString("created_at")))
                            .menuName(rs.getString("menu_name"))
                            .couponPrice(rs.getInt("price"))
                            .schoolId(rs.getString("school_id"))
                            .build();

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
