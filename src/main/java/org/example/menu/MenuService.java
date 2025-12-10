package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.db.PooledDataSource;
import org.example.general.Pair;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

@RequiredArgsConstructor
public class MenuService {

    private final MenuDAO menuDAO;
    // private final RestaurantDAO restaurantDAO; // [삭제] 이제 필요 없음!
    private final DataSource ds = PooledDataSource.getDataSource();

    public MenuBatchResponseDTO registerMenuBatch(MenuBatchRequestDTO req) {

        // 식당 목록 조회 로직 삭제됨 (클라이언트가 ID를 주니까)

        String[] lines = new String(req.getCsvBytes(), StandardCharsets.UTF_8).split("\n");
        ArrayList<Pair<Long, Long>> successList = new ArrayList<>();

        for (int i = 1; i < lines.length; ++i) {
            String line = lines[i].strip();
            if (line.isEmpty()) continue;

            try (Connection conn = ds.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    Long generatedMenuId = processSingleLine(conn, line);
                    conn.commit();
                    successList.add(new Pair<>((long)(i + 1), generatedMenuId));

                } catch (Exception e) {
                    conn.rollback();
                    System.err.println("Line " + (i + 1) + " Error: " + e.getMessage());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return MenuBatchResponseDTO.builder().build();
    }

    private Long processSingleLine(Connection conn, String line) throws Exception {
        String[] parsedLine = line.split(",");
        if (parsedLine.length < 7) throw new IllegalArgumentException("데이터 부족");

        // 1. [변경] 식당 ID 바로 파싱 (String 이름 -> Long ID)
        long restaurantId = Long.parseLong(parsedLine[0].strip());

        // 2. 데이터 파싱
        String menuName = parsedLine[1].strip();
        int standardPrice = Integer.parseInt(parsedLine[2].strip());

        String stuPriceStr = parsedLine[3].strip();
        Integer studentPrice = stuPriceStr.isEmpty() ? null : Integer.parseInt(stuPriceStr);

        int amount = Integer.parseInt(parsedLine[4].strip());

        String dateStr = parsedLine[5].strip();
        LocalDate saleDate = null;
        if (!dateStr.isEmpty()) {
            saleDate = LocalDate.parse(dateStr);
        }

        // 3. [변경] 메뉴 타입 ID 바로 파싱 (String "LUNCH" -> Long ID)
        long menuTypeId = Long.parseLong(parsedLine[6].strip());

        // 더 이상 DB에서 ID를 조회하는 로직(find...)이 필요 없습니다.

        // 4. 메뉴 객체 생성
        Menu menu = new Menu();
        menu.setRestaurantId(restaurantId); // 파싱한 ID 바로 주입
        menu.setMenuName(menuName);
        menu.setStandardPrice(standardPrice);
        menu.setStudentPrice(studentPrice);
        menu.setAmount(amount);

        if (saleDate != null) {
            menu.setStartSalesAt(saleDate);
            menu.setEndSalesAt(saleDate);
        }

        // 5. DB 저장
        Long savedMenuId = menuDAO.insert(conn, menu);
        if (savedMenuId == null) throw new SQLException("메뉴 저장 실패");

        MenuAvailability availability = new MenuAvailability();
        availability.setMenuId(savedMenuId);
        availability.setMenuTypeId(menuTypeId); // 파싱한 ID 바로 주입

        menuDAO.insertAvailability(conn, availability);

        return savedMenuId;
    }

    public MenuRegisterResponseDTO registerMenu(MenuRegisterRequestDTO req) {

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작

            try {
                // 1. Menu 객체 생성 (Builder 패턴 적용)
                Menu.MenuBuilder builder = Menu.builder()
                        .restaurantId(req.getRestId())
                        .menuName(req.getMenuName())
                        .standardPrice(req.getStandardPrice())
                        .studentPrice(req.getStudentPrice())
                        .amount(req.getDefaultAmount());

                if (req.getStartSalesAt() != null) {
                    builder.startSalesAt(req.getStartSalesAt());
                }
                if (req.getEndSalesAt() != null) {
                    builder.endSalesAt(req.getEndSalesAt());
                }

                // 최종 객체 생성
                Menu menu = builder.build();

                // 2. DB 저장 (Menu)
                Long savedMenuId = menuDAO.insert(conn, menu);
                if (savedMenuId == null) {
                    throw new SQLException("메뉴 저장 실패 (ID 반환 없음)");
                }

                // 3. DB 저장 (Availability - 시간대 연결)
                MenuAvailability availability = new MenuAvailability();
                availability.setMenuId(savedMenuId);
                availability.setMenuTypeId(req.getMenuTypeId());

                menuDAO.insertAvailability(conn, availability);

                // 4. 성공 시 커밋
                conn.commit();

                // 5. 응답 DTO 반환
                return new MenuRegisterResponseDTO(savedMenuId);

            } catch (Exception e) {
                conn.rollback(); // 실패 시 롤백
                throw new RuntimeException("메뉴 등록 중 오류 발생", e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB 연결 오류", e);
        }
    }
}