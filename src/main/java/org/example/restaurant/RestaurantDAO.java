package org.example.restaurant;

import org.example.db.PooledDataSource;
import org.example.menu.Menu;
import org.example.menu.MenuType;
import org.example.menu.MenuTypeName;
import org.example.user.User;
import org.example.user.UserType;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class RestaurantDAO {

    private final DataSource ds = PooledDataSource.getDataSource();

    public ArrayList<Restaurant> findAll() {

        String sql = """
            SELECT 
                r.id AS r_id, 
                r.name AS r_name, 
                r.description AS r_desc,
                roi.id AS roi_id, 
                roi.restaurant_id, 
                roi.start_at, 
                roi.end_at,
                mt.id AS mt_id,
                mt.name AS mt_name
            FROM RESTAURANT r
            LEFT JOIN RESTAURANT_OPERATING_INFO roi ON r.id = roi.restaurant_id
            LEFT JOIN MENU_TYPE mt ON roi.menu_type_id = mt.id
            ORDER BY r.id ASC
        """;

        ArrayList<Restaurant> res = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            Long currentRestaurantId = -1L;
            Restaurant currentRestaurant = null;

            while (rs.next()) {
                Long rId = rs.getLong("r_id");

                if (!rId.equals(currentRestaurantId)) {
                    currentRestaurant = Restaurant.builder()
                            .id(rId)
                            .description(rs.getString("r_desc"))
                            .name(RestaurantName.valueOf(rs.getString("r_name")))
                            .operatingInfos(new ArrayList<>())
                            .build();

                    res.add(currentRestaurant);
                    currentRestaurantId = rId;
                }

                long roiId = rs.getLong("roi_id");

                if (roiId != 0 && currentRestaurant != null) {
                    MenuType menuTypeObj = MenuType.builder()
                            .id(rs.getLong("mt_id"))
                            .name(MenuTypeName.valueOf(rs.getString("mt_name")))
                            .build();

                    RestaurantOperatingInfo info = RestaurantOperatingInfo.builder()
                            .id(roiId)
                            .restaurantId(rId)
                            .startAt(rs.getTime("start_at").toLocalTime())
                            .endAt(rs.getTime("end_at").toLocalTime())
                            .menuType(menuTypeObj)
                            .build();

                    currentRestaurant.getOperatingInfos().add(info);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }
}