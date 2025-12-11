package org.example.coupon;

import org.example.db.PooledDataSource;
import org.example.user.UserType;

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

    public void insert(Coupon coupon) {
        String sql = "INSERT INTO COUPON (menu_id, user_type, coupon_price) VALUES (?, ?, ?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, coupon.getMenuId());
            pstmt.setString(2, coupon.getUserType().name());
            pstmt.setInt(3, coupon.getCouponPrice());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("쿠폰 저장에 실패했습니다.", e);
        }
    }

    public List<Coupon> findByMenuId(Connection conn, long menuId) throws SQLException {

        String sql = "SELECT id, menu_id, user_type, coupon_price FROM COUPON WHERE menu_id = ?";

        List<Coupon> list = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, menuId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new Coupon(
                        rs.getLong("id"),
                        rs.getLong("menu_id"),
                        UserType.valueOf(rs.getString("user_type")),
                        rs.getInt("coupon_price")
                ));
            }
        }

        return list;
    }

    public int getUserCouponQuantity(Connection conn, long couponId, long userId) throws SQLException {

        String sql = "SELECT quantity FROM COUPON_INVENTORY WHERE coupon_id = ? AND user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, couponId);
            pstmt.setLong(2, userId);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("quantity") : 0;
        }
    }

    public void decreaseUserCoupon(Connection conn, long couponId, long userId) throws SQLException {
        String sql = "UPDATE COUPON_INVENTORY SET quantity = quantity - 1 WHERE coupon_id = ? AND user_id = ? AND quantity > 0";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, couponId);
            pstmt.setLong(2, userId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new SQLException("쿠폰 차감 실패");
            }
        }
    }
}