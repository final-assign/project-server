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
        String sql = "SELECT c.id, m.menu_name, c.price, ci.quantity " +
                "FROM COUPON_INVENTORY ci " +
                "JOIN COUPON c ON ci.coupon_id = c.id " +
                "JOIN MENU m ON c.menu_id = m.id " +
                "WHERE ci.user_id = ? AND ci.is_used = false " +
                "GROUP BY c.id, m.menu_name, c.price";

        try (Connection conn = ds.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CouponDetail coupon = CouponDetail.builder()
                            .id(rs.getLong("id"))
                            .menuName(rs.getString("menu_name"))
                            .price(rs.getInt("price"))
                            .quantity(rs.getInt("quantity"))
                            .build();

                    coupons.add(coupon);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coupons;
    }
}