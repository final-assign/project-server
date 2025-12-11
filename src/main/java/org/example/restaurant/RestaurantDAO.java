package org.example.restaurant;

import org.example.db.PooledDataSource;
import org.example.general.GeneralException;
import org.example.general.ResponseCode;
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
import java.util.List;

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

    public Long findIdByName(Connection conn, String name) throws SQLException {
        // ENUM 이름과 DB의 name 컬럼이 일치한다고 가정
        String sql = "SELECT id FROM RESTAURANT WHERE name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }
        return null;
    }

    public List<AvailableRestaurantDTO> findAvailableRestaurant(Long userId) {
        String sql =
                "SELECT DISTINCT r.id, r.name, r.description " +
                        "FROM USER u " +
                        "JOIN USER_TYPE_RESTAURANT utr ON u.type = utr.user_type " +
                        "JOIN RESTAURANT r ON utr.restaurant_id = r.id " +
                        "JOIN RESTAURANT_OPERATING_INFO roi ON r.id = roi.restaurant_id " +
                        "WHERE u.id = ? " +
                        "AND CURRENT_TIME BETWEEN roi.start_at AND roi.end_at";

        List<AvailableRestaurantDTO> list = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");

                    AvailableRestaurantDTO dto = new AvailableRestaurantDTO(id, name, description);
                    list.add(dto);
                }
            }
        } catch (SQLException e){

            throw new GeneralException(ResponseCode.FORBIDDEN, e.getMessage());
        }

        return list;
    }

    public List<Integer> getOperatingMenuTypes(Connection conn, long restaurantId) throws SQLException {

        String sql =
                "SELECT menu_type_id " +
                        "FROM RESTAURANT_OPERATING_INFO " +
                        "WHERE restaurant_id = ? " +
                        "AND CURRENT_TIME() BETWEEN start_at AND end_at";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, restaurantId);

            ResultSet rs = pstmt.executeQuery();

            List<Integer> list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getInt("menu_type_id"));
            }
            return list;

        } catch (SQLException e) {
            System.out.println("[ROI.getOperatingMenuTypes] SQL ERROR: " + e.getMessage());
            throw new SQLException(e);
        }
    }
}