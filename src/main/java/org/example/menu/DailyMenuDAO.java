package org.example.menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DailyMenuDAO {

    // 오늘의 메뉴 상세 내역 추가
    public int insert(Connection conn, DailyMenu dailyMenu) throws SQLException {

        String sql = "INSERT INTO DAILY_MENU (menu_id, served_date, main_dish, sub_dish, standard_price, student_price) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, dailyMenu.getMenuId());
            pstmt.setDate(2, java.sql.Date.valueOf(dailyMenu.getServedDate()));
            pstmt.setString(3, dailyMenu.getMainDish());

            // sub_dish는 null 가능
            if (dailyMenu.getSubDish() != null) {
                pstmt.setString(4, dailyMenu.getSubDish());
            } else {
                pstmt.setNull(4, java.sql.Types.VARCHAR);
            }

            pstmt.setInt(5, dailyMenu.getStandardPrice());
            pstmt.setInt(6, dailyMenu.getStandardPrice());
            return pstmt.executeUpdate();
        }
    }

    public DailyMenu findByMenuIdAndDate(Connection conn, Long menuId, LocalDate date) {
        // 1. 해당 메뉴 ID와 날짜가 일치하는 행을 조회하는 쿼리
        String sql = "SELECT * FROM DAILY_MENU WHERE menu_id = ? AND served_date = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, menuId);
            pstmt.setDate(2, java.sql.Date.valueOf(date)); // LocalDate -> sql.Date 변환

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 2. 가격(price) 컬럼이 NULL인지 확인하는 로직 (중요!)
                    int priceVal = rs.getInt("price");
                    Integer price = rs.wasNull() ? null : priceVal;
                    // rs.wasNull()은 방금 읽은 컬럼(price)이 SQL NULL이었으면 true를 반환함

                    // 3. DailyMenu 객체 생성 및 반환
                    return DailyMenu.builder()
                            .dailyMenuId(((java.sql.ResultSet) rs).getLong("id"))
                            .menuId(rs.getLong("menu_id"))
                            .servedDate(rs.getDate("served_date").toLocalDate())
                            .mainDish(rs.getString("main_dish"))
                            .subDish(rs.getString("sub_dish")) // sub_dish가 있으면 가져옴
                            .standardPrice(rs.getInt("standard_price")) // NULL이면 null로 들어감 -> 서비스에서 처리
                            .studentPrice(rs.getInt("student_price"))
                            .build();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("오늘의 메뉴 조회 중 오류 발생", e);
        }

        return null; // 해당 날짜에 등록된 식단이 없으면 null 반환
    }
}