package org.example.menu;

import org.example.db.PooledDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class MenuDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public Menu findById(long menuId, Connection conn) {
        String sql = "SELECT * FROM Menu WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, menuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Menu(
                            rs.getLong("id"),
                            rs.getLong("restaurant_id"),
                            rs.getString("menu_name"),
                            rs.getInt("standard_price"),
                            rs.getInt("student_price"),
                            rs.getInt("amount"),
                            rs.getDate("start_sales_at") != null ? rs.getDate("start_sales_at").toLocalDate() : null,
                            rs.getDate("end_sales_at") != null ? rs.getDate("end_sales_at").toLocalDate() : null
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    // Overloaded method for non-transactional reads
    public Menu findById(long menuId) {
        try (Connection conn = ds.getConnection()) {
            return findById(menuId, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 1. 메뉴 정보 + 판매 날짜 저장 (MENU 테이블)
    public Long insert(Connection conn, Menu menu) {
        // 사진에 있는 스키마대로 start_sales_at, end_sales_at 컬럼 추가
        String sql = "INSERT INTO Menu " +
                "(restaurant_id, menu_name, standard_price, student_price, amount, start_sales_at, end_sales_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, menu.getRestaurantId());
            pstmt.setString(2, menu.getMenuName());
            pstmt.setInt(3, menu.getStandardPrice());

            // studentPrice는 Integer(Nullable)이므로 null 체크 필요
            if (menu.getStudentPrice() != null) {
                pstmt.setInt(4, menu.getStudentPrice());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setInt(5, menu.getAmount());

            if (menu.getStartSalesAt() != null) {
                pstmt.setDate(6, Date.valueOf(menu.getStartSalesAt()));
                pstmt.setDate(7, Date.valueOf(menu.getEndSalesAt()));
            } else {
                pstmt.setNull(6, Types.DATE);
                pstmt.setNull(7, Types.DATE);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) return null;

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getLong(1) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long findMenuTypeIdByName(Connection conn, MenuTypeName type) {
        String sql = "SELECT id FROM MenuType WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type.getValue());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getLong("id") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 2. 메뉴 - 타입 연결 (MENU_AVAILABILITY 테이블)
    // 사진 스키마에 따라 날짜(sales_at) 제거함
    public void insertAvailability(Connection conn, MenuAvailability availability) {
        String sql = "INSERT INTO MenuAvailability (menu_id, menu_type_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, availability.getMenuId());
            pstmt.setLong(2, availability.getMenuTypeId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}