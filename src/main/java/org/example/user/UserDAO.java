package org.example.user;

import org.example.db.PooledDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class UserDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    Optional<User> findByIdAndPassword(String id, String pw){

        String sql = "SELECT * FROM USER WHERE school_id = ? AND password = ?";
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
}
