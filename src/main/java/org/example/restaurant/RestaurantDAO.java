package org.example.restaurant;

import org.example.db.PooledDataSource;
import org.example.menu.Menu;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

public class RestaurantDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public ArrayList<Restaurant> findAll() {
        String sql = "SELECT * FROM RESTAURANT;";

        try (Connection conn = ds.getConnection();
                                                      PreparedStatement pstmt = conn.prepareStatement(sql)) {

                Menu.builder().restaurantId(pstmt.g)
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
