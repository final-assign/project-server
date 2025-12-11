package org.example.coupon;

import org.example.db.PooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CouponDAO {
    private final DataSource ds = PooledDataSource.getDataSource();

    public List<CouponDetail> findByUserId(long userId) {
        List<CouponDetail> coupons = new ArrayList<>();

        //안 쓴거만 가져오기
        String sql = "SELECT c.id, m.menu_name, c.coupon_price, ci.quanity " +
                "FROM COUPON_INVENTORY ci " +
                "JOIN COUPON c ON ci.coupon_id = c.id " +
                "JOIN MENU m ON c.menu_id = m.id " +
                "WHERE ci.user_id = ? AND ci.quanity > 0 " +
                "ORDER BY c.id DESC";

        try (Connection conn = ds.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CouponDetail coupon = CouponDetail.builder()
                            .id(rs.getLong("id"))
                            .menuName(rs.getString("menu_name"))
                            .price(rs.getInt("coupon_price"))
                            .quantity(rs.getInt("quanity"))
                            .build();

                    coupons.add(coupon);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coupons;
    }

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