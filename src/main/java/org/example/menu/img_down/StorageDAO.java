package org.example.menu.img_down;

import org.example.db.PooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StorageDAO {
    private final DataSource ds = PooledDataSource.getDataSource();

    public byte[] findImageByMenuID(long menuID) {
        //조회 sql문
        String sql = "SELECT file_data FROM Storage WHERE menu_id = ?";
        PreparedStatement pstmt;

        try (Connection conn = ds.getConnection()) {
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, menuID);

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                   return rs.getBytes("file_data");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
