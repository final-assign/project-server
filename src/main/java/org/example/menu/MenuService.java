package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.general.GeneralException;
import org.example.general.ResponseCode;
import org.example.general.ResponseType;
import org.example.restaurant.RestaurantDAO;
import org.example.storage.ImageResponseDTO;
import org.example.menu.StorageDAO;
import org.example.db.PooledDataSource;
import org.example.general.Pair;
import org.example.user.UserDAO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

@RequiredArgsConstructor
public class MenuService {

    private final UserDAO userDAO;
    private final MenuDAO menuDAO;
    private final StorageDAO storageDAO;
    private final RestaurantDAO restaurantDAO;
    private final DailyMenuDAO dailyMenuDAO;
    // private final RestaurantDAO restaurantDAO; // [삭제] 이제 필요 없음!
    private final DataSource ds = PooledDataSource.getDataSource();

    public Menu findById(long menuId) {
        try (Connection conn = ds.getConnection()) {
            // 1. 기본 메뉴 정보 조회 (부모 테이블)
            // 여기서 menuDAO.findById(menuId, conn)을 호출해야 합니다.
            // (사용자님 코드의 findById(menuId, conn)이 DAO 호출이라고 가정)
            Menu menu = menuDAO.findById(menuId, conn);

            if (menu == null) return null;

            // 2. [핵심 로직] 오늘의 메뉴(isDailyMenu)인 경우 데이터 덮어쓰기
            if (menu.isDailyMenu()) {

                // 오늘 날짜에 해당하는 상세 메뉴 정보 조회
                DailyMenu dailyMenu = dailyMenuDAO.findByMenuIdAndDate(conn, menuId, LocalDate.now());

                // 오늘의 메뉴가 등록되어 있다면 정보를 교체
                if (dailyMenu != null) {

                    // Q. 데일리 메뉴 가격이 NULL이면? -> A. 기존 메뉴 가격(menu.getStandardPrice) 사용
                    int effectivePrice = (dailyMenu.getStandardPrice() != null)
                            ? dailyMenu.getStandardPrice()
                            : menu.getStandardPrice();

                    // 메뉴 이름도 "오늘의 메뉴" 껍데기 이름 대신, 실제 메인 반찬 이름(예: "돈까스")으로 교체
                    String effectiveName = dailyMenu.getMainDish();

                    // 3. 기존 menu 객체의 정보를 바탕으로 가격/이름만 바뀐 새로운 Menu 객체 생성 (불변 객체 가정)
                    return new Menu(
                            menu.getId(),
                            menu.getRestaurantId(),
                            effectiveName,          // [교체됨] 실제 반찬 이름
                            effectivePrice,         // [교체됨] NULL 체크된 가격
                            menu.getStudentPrice(), // 학생 가격 (유지)
                            menu.getAmount(),       // 수량 (유지)
                            menu.getStartSalesAt(),
                            menu.getEndSalesAt(),
                            menu.isDailyMenu()      // true 유지
                    );
                }
            }

            // 상시 메뉴이거나, 오늘의 메뉴인데 아직 식단이 등록 안 된 경우 원본 반환
            return menu;

        } catch (SQLException e) {
            throw new RuntimeException("DB 연결 및 메뉴 조회 실패", e);
        }
    }

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
            System.out.println("설마 여기니?");
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

    public MenuRegisterResponseDTO registerMenu(MenuRegisterRequestDTO req) {
        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. [핵심] 식당 이름으로 식당 ID 조회 (Validation)
                Long realRestId = restaurantDAO.findIdByName(conn, req.getRestaurantName());

                if (realRestId == null) {
                    throw new SQLException("존재하지 않는 식당 이름입니다: " + req.getRestaurantName());
                }

                // 이제 realRestId를 사용하여 등록 진행
                Long resultId;

                if (Boolean.TRUE.equals(req.getIsDailyMenu())) {
                    // 오늘의 메뉴 등록 (realRestId 전달)
                    resultId = registerDailyMenu(conn, realRestId, req);
                } else {
                    // 상시 메뉴 등록 (realRestId 전달)
                    resultId = registerStandardMenu(conn, realRestId, req);
                }

                conn.commit();
                return new MenuRegisterResponseDTO(resultId);

            } catch (Exception e) {
                conn.rollback();

                throw new GeneralException(ResponseCode.FORBIDDEN, e.getMessage());
            }
        } catch (SQLException e) {
            throw new GeneralException(ResponseCode.FORBIDDEN, e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // [신규] 오늘의 메뉴 처리 서비스 로직
    // -------------------------------------------------------------
    private Long registerDailyMenu(Connection conn, Long restId, MenuRegisterRequestDTO req) throws SQLException {

        Long parentMenuId;

        if (req.getMenuTypeId() == 0) {
            parentMenuId = menuDAO.findDailyMenuIdWithoutType(conn, restId);
        } else {
            parentMenuId = menuDAO.findDailyMenuId(conn, restId, req.getMenuTypeId());
        }

        if (parentMenuId == null) {
            throw new SQLException(String.format(
                    "해당 식당(%s)에 '오늘의 메뉴' 설정이 없거나 메뉴 타입(ID:%d)에 맞는 메뉴가 없습니다.",
                    req.getRestaurantName(), req.getMenuTypeId()
            ));
        }

        DailyMenu dailyMenu = DailyMenu.builder()
                .menuId(parentMenuId)
                .servedDate(req.getStartSalesAt())
                .mainDish(req.getMenuName())
                .subDish(null)
                .standardPrice(req.getStandardPrice())
                .studentPrice(req.getStudentPrice())
                .build();

        dailyMenuDAO.insert(conn, dailyMenu);

        return parentMenuId;
    }


    // -------------------------------------------------------------
    // [기존] 상시 메뉴 처리 서비스 로직
    // -------------------------------------------------------------
    private Long registerStandardMenu(Connection conn, Long restId, MenuRegisterRequestDTO req) throws SQLException {
        // 1. Menu 객체 생성
        Menu menu = Menu.builder()
                .restaurantId(restId)
                .menuName(req.getMenuName())
                .standardPrice(req.getStandardPrice())
                .studentPrice(req.getStudentPrice())
                .amount(req.getDefaultAmount())
                .isDailyMenu(false)
                .build();

        // 2. MENU 테이블 저장
        Long newMenuId = menuDAO.insert(conn, menu);

        // 3. MENU_AVAILABILITY 저장 (직관적 처리)
        if (req.getMenuTypeId() == 0) {
            // 0이 들어오면 -> 2번(중식) 저장
            MenuAvailability av1 = new MenuAvailability();
            av1.setMenuId(newMenuId);
            av1.setMenuTypeId(2L);
            menuDAO.insertAvailability(conn, av1);

            // 이어서 -> 3번(석식)도 저장
            MenuAvailability av2 = new MenuAvailability();
            av2.setMenuId(newMenuId);
            av2.setMenuTypeId(3L);
            menuDAO.insertAvailability(conn, av2);

        } else {
            // 0이 아니면 -> 입력받은 값 그대로 하나만 저장
            MenuAvailability av = new MenuAvailability();
            av.setMenuId(newMenuId);
            av.setMenuTypeId(req.getMenuTypeId());
            menuDAO.insertAvailability(conn, av);
        }

        return newMenuId;
    }

    public MenuListResponseDTO getCouponsTargetMenuList(MenuListRequestDTO req) {

        try (Connection conn = ds.getConnection()) {

            // 1. 식당 이름으로 식당 ID 조회 (Validation)
            String restName = req.getRestaurantName();
            Long restId = restaurantDAO.findIdByName(conn, restName);

            if (restId == null) {
                throw new IllegalArgumentException("존재하지 않는 식당입니다: " + restName);
            }

            // 2. 식당 종류에 따라 조회할 메뉴 타입(상시 vs 데일리) 결정
            // 요구사항: 분식당은 상시 메뉴(0), 나머지는 데일리 메뉴(1)만 불러옴
            boolean targetIsDaily;

            if ("SNACK_CAFETERIA".equals(restName)) {
                targetIsDaily = false; // 분식당 -> 상시 메뉴 조회
            } else {
                targetIsDaily = true;  // 학생/교직원 -> 데일리 메뉴(식권) 조회
            }

            // 3. DAO를 통해 조건에 맞는 메뉴 리스트 조회
            List<Menu> menuList = menuDAO.findByRestaurantIdAndIsDaily(conn, restId, targetIsDaily);

            // 4. Menu(VO) -> MenuInfoDTO(Data Transfer Object) 변환
            List<MenuInfoDTO> dtoList = new ArrayList<>();

            for (Menu menu : menuList) {
                // 날짜 정보 처리: 상시 메뉴는 null, 데일리 메뉴는 설정된 날짜가 있다면 문자열로 변환
                String dateStr = null;
                if (menu.getStartSalesAt() != null) {
                    dateStr = menu.getStartSalesAt().toString();
                }

                dtoList.add(MenuInfoDTO.builder()
                        .menuId(menu.getId())
                        .menuName(menu.getMenuName())
                        .price(menu.getStandardPrice()) // 쿠폰 기준 가격 (표준가)
                        .isDailyMenu(menu.isDailyMenu())
                        .servedDate(dateStr)
                        .build());
            }

            // 5. 응답 DTO 생성 및 반환
            return new MenuListResponseDTO(dtoList);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("메뉴 목록 조회 중 DB 오류 발생", e);
        }
    }

    public List<MenuDTO> getRestaurantMenu(long userId, long restaurantId) {

        try (Connection conn = ds.getConnection()) {
            // --- 네가 이미 만든 로직 그대로 ---
            String userType = userDAO.getUserType(conn, userId);

            List<Integer> menuTypeIds = restaurantDAO.getOperatingMenuTypes(conn, restaurantId);
            if (menuTypeIds.isEmpty()) return Collections.emptyList();

            List<Long> menuIds = menuDAO.getMenuIdsByMenuTypes(conn, menuTypeIds);
            if (menuIds.isEmpty()) return Collections.emptyList();

            List<MenuRow> menus = menuDAO.getMenus(conn, menuIds, restaurantId);

            List<MenuDTO> result = new ArrayList<>();

            for (MenuRow m : menus) {

                DailyMenuRow dm = null;
                int finalPrice;

                if (m.getIsDailyMenu() == 1) {
                    dm = menuDAO.getTodayDailyMenu(conn, m.getId());
                }

                if (dm != null) {
                    finalPrice = userType.equals("STUDENT")
                            ? dm.getStudentPrice()
                            : dm.getStandardPrice();
                } else {
                    finalPrice = userType.equals("STUDENT")
                            ? m.getStudentPrice()
                            : m.getStandardPrice();
                }

                result.add(new MenuDTO(
                        m.getId(),
                        m.getMenuName(),
                        finalPrice,
                        m.getAmount(),
                        m.getIsDailyMenu()
                ));
            }

            return result;

        }catch (SQLException e){

            System.out.println("379번 에러");
        }

        return null;
    }
}
