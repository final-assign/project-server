package org.example.coupon;

import org.example.db.PooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CouponDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public void insert(Coupon coupon) {
        String sql = "INSERT INTO coupons (rest_id, menu_id, user_type, coupon_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, coupon.getRestId());
            pstmt.setLong(2, coupon.getMenuId());
            pstmt.setString(3, coupon.getUserType().name());
            pstmt.setInt(4, coupon.getCouponPrice());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("쿠폰 저장에 실패했습니다.", e);
        }
    }
}
