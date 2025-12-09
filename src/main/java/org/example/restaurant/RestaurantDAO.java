package org.example.restaurant;

import org.example.db.PooledDataSource;
import org.example.menu.Menu;
import org.example.user.User;
import org.example.user.UserType;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

public class RestaurantDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public ArrayList<Restaurant> findAll() {

        String sql = "SELECT * FROM Restaurant;";
        ArrayList<Restaurant> res = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){

                Restaurant rss = Restaurant.builder()
                                    .id(rs.getLong("id"))
                        .description(rs.getString("description"))
                        .name(RestaurantName.valueOf(rs.getString("name"))).build();

                res.add(rss);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }
}
