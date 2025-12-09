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
        String sql = "SELECT o.*, m.menu_name, c.coupon_price " +
                             "FROM orders o " +
                             "JOIN menu m ON o.menu_id = m.id " +
                             "LEFT JOIN coupon c ON o.coupon_id = c.id " +
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
                            .userId(rs.getLong("user_id"))
                            .menuId(rs.getLong("menu_id"))
                            .couponId(rs.getLong("coupon_id"))
                            .purchaseType(PurchaseType.valueOf(rs.getString("purchase_type")))
                            .status(OrderStatus.valueOf(rs.getString("status")))
                            .amount(rs.getInt("amount"))
                            .purchasePrice(rs.getInt("purchase_price"))
                            .createdAt(LocalDateTime.parse(rs.getString("created_at")))
                            .menuName(rs.getString("menu_name"))
                            .couponPrice(rs.getInt("coupon_price"))
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
