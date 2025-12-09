package org.example.menu;

import org.example.db.PooledDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class MenuDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public Long insert(Menu menu) {
        String sql = "INSERT INTO Menu (restaurant_id, menu_name, standard_price, student_price, amount, ) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, menu.getRestaurantId());
            pstmt.setString(2, menu.getMenuName());
            pstmt.setInt(3, menu.getStandardPrice());
            pstmt.setInt(4, menu.getStudentPrice());
            pstmt.setInt(5, menu.getEmployeePrice());
            pstmt.setInt(6, menu.getAmount());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys(); //삽입 후 키값 반환
            return rs.next() ? rs.getLong(1) : null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long findMenuTypeIdByName(MenuTypeName type) {
        String sql = "SELECT id FROM MenuType WHERE name = ?";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type.getValue());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertAvailability(MenuAvailability availability) {
        String sql = "INSERT INTO MenuAvailability (menu_id, menu_type_id, sales_at) VALUES (?, ?, ?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, availability.getMenuId());
            pstmt.setLong(2, availability.getMenuTypeId());
            pstmt.setTimestamp(3, Timestamp.valueOf(availability.getSalesAt()));

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}