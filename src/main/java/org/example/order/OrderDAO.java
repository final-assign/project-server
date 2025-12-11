package org.example.order;

import org.example.db.PooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public int countTodaysOrdersByMenuId(long menuId, Connection conn) {
        // 오늘 날짜에 COOKING 또는 COMPLETED 상태인 주문 수를 계산
        String sql = "SELECT COUNT(*) FROM `ORDER` WHERE menu_id = ? AND DATE(created_at) = CURDATE() AND status IN ('COOKING', 'COMPLETED')";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, menuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("오늘의 주문 수량 조회에 실패했습니다.", e);
        }
    }

    public void insert(Order order, Connection conn) {

        String sql = "INSERT INTO `ORDER` (menu_id, coupon_id, user_id, status, total_price, created_at) VALUES (?, ?, ?, ?, ?, NOW())";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, order.getMenuId());
            if (order.getCouponId() != null) {
                pstmt.setLong(2, order.getCouponId());
            } else {
                pstmt.setNull(2, java.sql.Types.BIGINT);
            }
            pstmt.setLong(3, order.getUserId());
            pstmt.setString(4, order.getStatus().name());
            pstmt.setInt(5, order.getPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("주문 생성에 실패했습니다.", e);
        }
    }
}
