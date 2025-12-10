package org.example.management;

import java.sql.SQLException;
import java.util.List;
import java.math.BigDecimal;

public class PriceService {
    private PriceDAO dao;

    public PriceService(PriceDAO dao) {
        this.dao = dao;
    }

    public boolean registerCafeteriaPrice(int cafeteriaId, String mealTime, BigDecimal price) {
        try {
            // 중복 체크
            CafeteriaPrice existing = dao.getCafeteriaPrice(cafeteriaId, mealTime);
            if (existing != null) {
                System.out.println("이미 등록된 가격입니다. 수정 기능을 이용해주세요.");
                return false;
            }

            CafeteriaPrice cafeteriaPrice = new CafeteriaPrice(cafeteriaId, mealTime, price);
            boolean result = dao.insertCafeteriaPrice(cafeteriaPrice);

            if (result) {
                System.out.println("가격이 등록되었습니다.");
                System.out.println(cafeteriaPrice);
            }
            return result;

        } catch (SQLException e) {
            System.out.println("가격 등록 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean modifyCafeteriaPrice(int priceId, BigDecimal newPrice) {
        try {
            boolean result = dao.updateCafeteriaPrice(priceId, newPrice);

            if (result) {
                System.out.println("가격이 수정되었습니다.");
            } else {
                System.out.println("해당 가격 정보를 찾을 수 없습니다.");
            }
            return result;

        } catch (SQLException e) {
            System.out.println("가격 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public void viewAllCafeteriaPrices() {
        try {
            List<CafeteriaPrice> prices = dao.getAllCafeteriaPrices();

            if (prices.isEmpty()) {
                System.out.println("등록된 가격이 없습니다.");
                return;
            }

            System.out.println("\n========== 학생식당/교직원식당 가격 ==========");
            for (CafeteriaPrice price : prices) {
                System.out.printf("ID: %d | %s\n", price.getPriceId(), price);
            }
            System.out.println("============================================\n");

        } catch (SQLException e) {
            System.out.println("가격 조회 실패: " + e.getMessage());
        }
    }

    public void viewPricesByCafeteria(int cafeteriaId) {
        try {
            List<CafeteriaPrice> prices = dao.getPricesByCafeteria(cafeteriaId);

            if (prices.isEmpty()) {
                System.out.println("등록된 가격이 없습니다.");
                return;
            }

            System.out.println("\n========== 식당별 가격 ==========");
            for (CafeteriaPrice price : prices) {
                System.out.printf("ID: %d | %s\n", price.getPriceId(), price);
            }
            System.out.println("================================\n");

        } catch (SQLException e) {
            System.out.println("가격 조회 실패: " + e.getMessage());
        }
    }

    boolean registerSnackBarMenu(String menuName, BigDecimal price, String description) {
        try {
            SnackBarMenu menu = new SnackBarMenu(menuName, price, description);
            boolean result = dao.insertSnackBarMenu(menu);

            if (result) {
                System.out.println("메뉴가 등록되었습니다.");
                System.out.println(menu);
            }
            return result;

        } catch (SQLException e) {
            System.out.println("메뉴 등록 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean modifySnackBarMenu(int menuId, String menuName, BigDecimal price, String description) {
        try {
            boolean result = dao.updateSnackBarMenu(menuId, menuName, price, description);

            if (result) {
                System.out.println("메뉴가 수정되었습니다.");
            } else {
                System.out.println("해당 메뉴를 찾을 수 없습니다.");
            }
            return result;

        } catch (SQLException e) {
            System.out.println("메뉴 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSnackBarMenu(int menuId) {
        try {
            boolean result = dao.deleteSnackBarMenu(menuId);

            if (result) {
                System.out.println("메뉴가 삭제(품절 처리)되었습니다.");
            } else {
                System.out.println("해당 메뉴를 찾을 수 없습니다.");
            }
            return result;

        } catch (SQLException e) {
            System.out.println("메뉴 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    public void viewAllSnackBarMenus() {
        try {
            List<SnackBarMenu> menus = dao.getAllSnackBarMenus();

            if (menus.isEmpty()) {
                System.out.println("등록된 메뉴가 없습니다.");
                return;
            }

            System.out.println("\n========== 분식당 전체 메뉴 ==========");
            for (SnackBarMenu menu : menus) {
                System.out.println(menu);
            }
            System.out.println("====================================\n");

        } catch (SQLException e) {
            System.out.println("메뉴 조회 실패: " + e.getMessage());
        }
    }

    public void viewAvailableSnackBarMenus() {
        try {
            List<SnackBarMenu> menus = dao.getAvailableSnackBarMenus();

            if (menus.isEmpty()) {
                System.out.println("현재 이용 가능한 메뉴가 없습니다.");
                return;
            }

            System.out.println("\n========== 분식당 이용 가능 메뉴 ==========");
            for (SnackBarMenu menu : menus) {
                System.out.println(menu);
            }
            System.out.println("=========================================\n");

        } catch (SQLException e) {
            System.out.println("메뉴 조회 실패: " + e.getMessage());
        }
    }
}
