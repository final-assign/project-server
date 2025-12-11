package org.example.menu;

import org.example.db.PooledDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;

public class StorageDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public int insert(Long menuId, InputStream fileInputStream, long fileLength) {
        String sql = "INSERT INTO STORAGE (menu_id, file_data) VALUES (?, ?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, menuId);
            pstmt.setBinaryStream(2, fileInputStream, fileLength);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Optional<Storage> findByMenuID(long menuID) {
        Storage storage = null;
        String sql = "SELECT * FROM STORAGE WHERE menu_id = ?";

        try (Connection conn = ds.getConnection()) {

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, menuID);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    storage = Storage.builder()
                            .id(rs.getLong("id"))
                            .menuId(rs.getLong("menu_id"))
                            .fileData(rs.getBytes("file_data"))
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(storage);
    }
}