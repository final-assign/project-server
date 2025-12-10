package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseType;
import org.example.storage.ImageResponseDTO;
import org.example.storage.StorageDAO;
import org.example.db.PooledDataSource;
import org.example.general.Pair;
import java.util.Optional;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

@RequiredArgsConstructor
public class MenuService {
    private final MenuDAO menuDAO;
    private final StorageDAO storageDAO;
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
                    successList.add(new Pair<>((long) (i + 1), generatedMenuId));

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

        return 1L;

    }

    public ImageResponseDTO findImage(long menuId) {
        Optional<Storage> storage = storageDAO.findByMenuID(menuId);

        if (storage.isEmpty()) {
            return ImageResponseDTO.builder()
                    .resType(ResponseType.RESPONSE)
                    .imageData(new byte[0])
                    .build();
        }

        return ImageResponseDTO.builder()
                .resType(ResponseType.RESPONSE)
                .imageData(storage.get().getFileData())
                .build();
    }

}
