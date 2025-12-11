package org.example.user;

import org.example.db.PooledDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class UserDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public User findById(long userId, Connection conn) throws SQLException {
        String sql = "SELECT id, type, balance FROM USER WHERE id = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setLong(1, userId);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return User.builder()
                    .id(rs.getLong("id"))
                    .type(UserType.valueOf(rs.getString("type")))
                    .balance(rs.getInt("balance"))
                    .build();
        }

        return null;
    }

    public void updateBalance(long userId, int newBalance, Connection conn) throws SQLException {
        String sql = "UPDATE USER SET balance = ? WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, newBalance);
        pstmt.setLong(2, userId);
        pstmt.executeUpdate();
        pstmt.close();
    }

    Optional<User> findByIdAndPassword(String id, String pw){

        String sql = "SELECT * FROM USER WHERE school_id = ? AND password = ?";

        System.out.println(id);
        System.out.println(pw);
        PreparedStatement pstmt;

        User res = null;

        try (Connection conn = ds.getConnection()) {

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, pw);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    res = User.builder()
                                .id(rs.getLong("id"))
                                .type(UserType.valueOf(rs.getString("type")))
                                .name(rs.getString("name")).build();
                }
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return Optional.ofNullable(res);
    }

    UserType findTypeById(Long id){

        String sql = "SELECT type FROM USER WHERE id = ?";
        PreparedStatement pstmt;
        UserType res = null;

        try (Connection conn = ds.getConnection()) {

            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    res = UserType.valueOf(rs.getString("type"));
                }
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return res;
    }

    public String getUserType(Connection conn, long userId) {
        String sql = "SELECT type FROM USER WHERE id = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("type");
            }

        } catch (SQLException e) {

            System.out.println("[UserDAO.getUserType] SQL ERROR: " + e.getMessage());
        }

        return null;
    }
}
