package org.example.management;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class PriceDAO {
    private Connection conn;

    public PriceDAO(Connection conn) {
        this.conn = conn;
    }

    // 식당 가격 등록
    public boolean insertCafeteriaPrice(CafeteriaPrice price) throws SQLException {
        String sql = "INSERT INTO cafeteria_price (cafeteria_id, meal_time, price) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, price.getCafeteriaId());
            pstmt.setString(2, price.getMealTime());
            pstmt.setBigDecimal(3, price.getPrice());

            int affected = pstmt.executeUpdate();

            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        price.setPriceId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // 식당 가격 수정
    public boolean updateCafeteriaPrice(int priceId, BigDecimal newPrice) throws SQLException {
        String sql = "UPDATE cafeteria_price SET price = ? WHERE price_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newPrice);
            pstmt.setInt(2, priceId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // 전체 식당 가격 조회
    public List<CafeteriaPrice> getAllCafeteriaPrices() throws SQLException {
        String sql = "SELECT cp.*, c.cafeteria_name " +
                "FROM cafeteria_price cp " +
                "JOIN cafeteria c ON cp.cafeteria_id = c.cafeteria_id " +
                "ORDER BY cp.cafeteria_id, " +
                "FIELD(cp.meal_time, '아침', '점심', '저녁')";

        List<CafeteriaPrice> prices = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prices.add(mapResultSetToCafeteriaPrice(rs));
            }
        }
        return prices;
    }

    //식당 가격 조회
    public List<CafeteriaPrice> getPricesByCafeteria(int cafeteriaId) throws SQLException {
        String sql = "SELECT cp.*, c.cafeteria_name " +
                "FROM cafeteria_price cp " +
                "JOIN cafeteria c ON cp.cafeteria_id = c.cafeteria_id " +
                "WHERE cp.cafeteria_id = ? " +
                "ORDER BY FIELD(cp.meal_time, '아침', '점심', '저녁')";

        List<CafeteriaPrice> prices = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeteriaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    prices.add(mapResultSetToCafeteriaPrice(rs));
                }
            }
        }
        return prices;
    }

    // 식당-시간 가격 조회
    public CafeteriaPrice getCafeteriaPrice(int cafeteriaId, String mealTime) throws SQLException {
        String sql = "SELECT cp.*, c.cafeteria_name " +
                "FROM cafeteria_price cp " +
                "JOIN cafeteria c ON cp.cafeteria_id = c.cafeteria_id " +
                "WHERE cp.cafeteria_id = ? AND cp.meal_time = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cafeteriaId);
            pstmt.setString(2, mealTime);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCafeteriaPrice(rs);
                }
            }
        }
        return null;
    }

    // 분식당 메뉴 등록
    public boolean insertSnackBarMenu(SnackBarMenu menu) throws SQLException {
        String sql = "INSERT INTO snack_bar_menu (menu_name, price, description, is_available) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, menu.getMenuName());
            pstmt.setBigDecimal(2, menu.getPrice());
            pstmt.setString(3, menu.getDescription());
            pstmt.setBoolean(4, menu.isAvailable());

            int affected = pstmt.executeUpdate();

            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        menu.setMenuId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // 분식당 가격 수정
    public boolean updateSnackBarMenu(int menuId, String menuName, BigDecimal price, String description) throws SQLException {
        String sql = "UPDATE snack_bar_menu SET menu_name = ?, price = ?, description = ? WHERE menu_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, menuName);
            pstmt.setBigDecimal(2, price);
            pstmt.setString(3, description);
            pstmt.setInt(4, menuId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // 품절처리
    public boolean deleteSnackBarMenu(int menuId) throws SQLException {
        String sql = "UPDATE snack_bar_menu SET is_available = FALSE WHERE menu_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, menuId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // 분식당 메뉴 조회
    public List<SnackBarMenu> getAllSnackBarMenus() throws SQLException {
        String sql = "SELECT * FROM snack_bar_menu ORDER BY menu_id";

        List<SnackBarMenu> menus = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                menus.add(mapResultSetToSnackBarMenu(rs));
            }
        }
        return menus;
    }

    // 분식당 이용가능한 메뉴 조회
    public List<SnackBarMenu> getAvailableSnackBarMenus() throws SQLException {
        String sql = "SELECT * FROM snack_bar_menu WHERE is_available = TRUE ORDER BY menu_id";

        List<SnackBarMenu> menus = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                menus.add(mapResultSetToSnackBarMenu(rs));
            }
        }
        return menus;
    }


    public SnackBarMenu getSnackBarMenu(int menuId) throws SQLException {
        String sql = "SELECT * FROM snack_bar_menu WHERE menu_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, menuId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSnackBarMenu(rs);
                }
            }
        }
        return null;
    }

    // 학식-교식 조회
    public Map<Integer, String> getCafeteriaList() throws SQLException {
        String sql = "SELECT cafeteria_id, cafeteria_name FROM cafeteria WHERE cafeteria_type != '분식당'";
        Map<Integer, String> cafeterias = new LinkedHashMap<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cafeterias.put(rs.getInt("cafeteria_id"), rs.getString("cafeteria_name"));
            }
        }
        return cafeterias;
    }

    // 분식-학식-교식
    public Map<Integer, String> getAllCafeteriaList() throws SQLException {
        String sql = "SELECT cafeteria_id, cafeteria_name FROM cafeteria";
        Map<Integer, String> cafeterias = new LinkedHashMap<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cafeterias.put(rs.getInt("cafeteria_id"), rs.getString("cafeteria_name"));
            }
        }
        return cafeterias;
    }

    private CafeteriaPrice mapResultSetToCafeteriaPrice(ResultSet rs) throws SQLException {
        CafeteriaPrice price = new CafeteriaPrice();
        price.setPriceId(rs.getInt("price_id"));
        price.setCafeteriaId(rs.getInt("cafeteria_id"));
        price.setCafeteriaName(rs.getString("cafeteria_name"));
        price.setMealTime(rs.getString("meal_time"));
        price.setPrice(rs.getBigDecimal("price"));
        return price;
    }


    private SnackBarMenu mapResultSetToSnackBarMenu(ResultSet rs) throws SQLException {
        SnackBarMenu menu = new SnackBarMenu();
        menu.setMenuId(rs.getInt("menu_id"));
        menu.setMenuName(rs.getString("menu_name"));
        menu.setPrice(rs.getBigDecimal("price"));
        menu.setDescription(rs.getString("description"));
        menu.setAvailable(rs.getBoolean("is_available"));
        return menu;
    }
}
