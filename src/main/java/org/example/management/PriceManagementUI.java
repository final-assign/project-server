package org.example.management;

import java.sql.Connection;
import java.util.Scanner;
import java.util.Map;
import java.math.BigDecimal;

public class PriceManagementUI {
    private Scanner scanner;
    private PriceService service;
    private PriceDAO dao;

    public PriceManagementUI(Connection conn) {
        this.scanner = new Scanner(System.in);
        this.dao = new PriceDAO(conn);
        this.service = new PriceService(dao);
    }

    public void start() {
        while (true) {
            displayMainMenu();
            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:
                    manageCafeteriaPrice();
                    break;
                case 2:
                    manageSnackBarMenu();
                    break;
                case 0:
                    System.out.println("프로그램을 종료합니다.");
                    scanner.close();
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
            }
        }
    }


    private void displayMainMenu() {
        System.out.println("\n========== 식당 가격 관리 시스템 ==========");
        System.out.println("1. 학생식당/교직원식당 가격 관리");
        System.out.println("2. 분식당 메뉴 가격 관리");
        System.out.println("0. 종료");
        System.out.println("==========================================");
    }

    private void manageCafeteriaPrice() {
        while (true) {
            displayCafeteriaPriceMenu();
            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:
                    registerCafeteriaPrice();
                    break;
                case 2:
                    modifyCafeteriaPrice();
                    break;
                case 3:
                    service.viewAllCafeteriaPrices();
                    break;
                case 4:
                    viewCafeteriaPriceByCafeteria();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
            }
        }
    }

    private void displayCafeteriaPriceMenu() {
        System.out.println("\n----- 학생식당/교직원식당 가격 관리 -----");
        System.out.println("1. 가격 등록");
        System.out.println("2. 가격 수정");
        System.out.println("3. 전체 가격 조회");
        System.out.println("4. 식당별 가격 조회");
        System.out.println("0. 뒤로 가기");
        System.out.println("------------------------------------------");
    }

    private void registerCafeteriaPrice() {
        try {
            System.out.println("\n----- 가격 등록 -----");

            // 식당 선택
            Map<Integer, String> cafeterias = dao.getCafeteriaList();
            System.out.println("\n[식당 목록]");
            for (Map.Entry<Integer, String> entry : cafeterias.entrySet()) {
                System.out.printf("%d. %s\n", entry.getKey(), entry.getValue());
            }
            int cafeteriaId = getIntInput("식당 선택: ");

            // 식사 시간 선택
            System.out.println("\n[식사 시간]");
            System.out.println("1. 아침");
            System.out.println("2. 점심");
            System.out.println("3. 저녁");
            int mealChoice = getIntInput("식사 시간 선택: ");
            String mealTime = getMealTimeString(mealChoice);

            // 가격 입력
            int price = getIntInput("가격 입력 (원): ");

            // 등록 실행
            service.registerCafeteriaPrice(cafeteriaId, mealTime, new BigDecimal(price));

        } catch (Exception e) {
            System.out.println("등록 중 오류 발생: " + e.getMessage());
        }
    }

    private void modifyCafeteriaPrice() {
        try {
            System.out.println("\n----- 가격 수정 -----");

            // 먼저 전체 가격 조회
            service.viewAllCafeteriaPrices();

            int priceId = getIntInput("수정할 가격 ID: ");
            int newPrice = getIntInput("새로운 가격 (원): ");

            service.modifyCafeteriaPrice(priceId, new BigDecimal(newPrice));

        } catch (Exception e) {
            System.out.println("수정 중 오류 발생: " + e.getMessage());
        }
    }

    private void viewCafeteriaPriceByCafeteria() {
        try {
            System.out.println("\n----- 식당별 가격 조회 -----");

            Map<Integer, String> cafeterias = dao.getCafeteriaList();
            System.out.println("\n[식당 목록]");
            for (Map.Entry<Integer, String> entry : cafeterias.entrySet()) {
                System.out.printf("%d. %s\n", entry.getKey(), entry.getValue());
            }
            int cafeteriaId = getIntInput("식당 선택: ");

            service.viewPricesByCafeteria(cafeteriaId);

        } catch (Exception e) {
            System.out.println("조회 중 오류 발생: " + e.getMessage());
        }
    }

    private void manageSnackBarMenu() {
        while (true) {
            displaySnackBarMenuMenu();
            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:
                    registerSnackBarMenu();
                    break;
                case 2:
                    modifySnackBarMenu();
                    break;
                case 3:
                    deleteSnackBarMenu();
                    break;
                case 4:
                    service.viewAllSnackBarMenus();
                    break;
                case 5:
                    service.viewAvailableSnackBarMenus();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
            }
        }
    }


    private void displaySnackBarMenuMenu() {
        System.out.println("\n----- 분식당 메뉴 가격 관리 -----");
        System.out.println("1. 메뉴 등록");
        System.out.println("2. 메뉴 수정");
        System.out.println("3. 메뉴 삭제(품절)");
        System.out.println("4. 전체 메뉴 조회");
        System.out.println("5. 이용 가능 메뉴 조회");
        System.out.println("0. 뒤로 가기");
        System.out.println("----------------------------------");
    }

    private void registerSnackBarMenu() {
        try {
            System.out.println("\n----- 메뉴 등록 -----");

            System.out.print("메뉴 이름: ");
            String menuName = scanner.nextLine().trim();

            int price = getIntInput("가격 (원): ");

            System.out.print("메뉴 설명: ");
            String description = scanner.nextLine().trim();

            service.registerSnackBarMenu(menuName, new BigDecimal(price), description);

        } catch (Exception e) {
            System.out.println("등록 중 오류 발생: " + e.getMessage());
        }
    }

    private void modifySnackBarMenu() {
        try {
            System.out.println("\n----- 메뉴 수정 -----");

            // 먼저 전체 메뉴 조회
            service.viewAllSnackBarMenus();

            int menuId = getIntInput("수정할 메뉴 ID: ");

            System.out.print("새 메뉴 이름: ");
            String menuName = scanner.nextLine().trim();

            int price = getIntInput("새 가격 (원): ");

            System.out.print("새 메뉴 설명: ");
            String description = scanner.nextLine().trim();

            service.modifySnackBarMenu(menuId, menuName, new BigDecimal(price), description);

        } catch (Exception e) {
            System.out.println("수정 중 오류 발생: " + e.getMessage());
        }
    }

    private void deleteSnackBarMenu() {
        try {
            System.out.println("\n----- 메뉴 삭제 -----");

            // 먼저 전체 메뉴 조회
            service.viewAllSnackBarMenus();

            int menuId = getIntInput("삭제할 메뉴 ID: ");

            System.out.print("정말 삭제하시겠습니까? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes")) {
                service.deleteSnackBarMenu(menuId);
            } else {
                System.out.println("삭제가 취소되었습니다.");
            }

        } catch (Exception e) {
            System.out.println("삭제 중 오류 발생: " + e.getMessage());
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }

    private String getMealTimeString(int choice) {
        switch (choice) {
            case 1: return "아침";
            case 2: return "점심";
            case 3: return "저녁";
            default: return "점심";
        }
    }
}