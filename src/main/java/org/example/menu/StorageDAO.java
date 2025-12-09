package org.example.menu;

import org.example.db.PooledDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

public class StorageDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public int insert(int menuId, InputStream fileInputStream, long fileLength) {
        String sql = "INSERT INTO storage (menu_id, file_data) VALUES (?, ?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, menuId);
            pstmt.setBinaryStream(2, fileInputStream, fileLength);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}