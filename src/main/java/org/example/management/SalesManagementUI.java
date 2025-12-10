package org.example.management;

import java.sql.Connection;
import java.util.Scanner;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class SalesManagementUI {
    private Scanner scanner;
    private SalesService service;
    private SalesDAO dao;

    public SalesManagementUI(Connection conn) {
        this.scanner = new Scanner(System.in);
        this.dao = new SalesDAO(conn);
        this.service = new SalesService(dao);
    }

    public void start() {
        while (true) {
            displayMainMenu();
            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:
                    quickDashboard();
                    break;
                case 2:
                    manageDailySales();
                    break;
                case 3:
                    managePeriodSales();
                    break;
                case 4:
                    manageHourlyUsage();
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
        System.out.println("\n========== 매출 현황 및 이용 현황 관리 ==========");
        System.out.println("1. 빠른 대시보드 (오늘/이번주/이번달)");
        System.out.println("2. 일별 매출 조회");
        System.out.println("3. 기간별 매출 요약");
        System.out.println("4. 시간대별 이용 현황");
        System.out.println("0. 종료");
        System.out.println("=================================================");
    }

    private void quickDashboard() {
        while (true) {
            System.out.println("\n----- 빠른 대시보드 -----");
            System.out.println("1. 오늘의 매출 현황");
            System.out.println("2. 이번 주 매출 요약");
            System.out.println("3. 이번 달 매출 요약");
            System.out.println("0. 뒤로 가기");
            System.out.println("-------------------------");

            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:
                    service.viewTodayDashboard();
                    break;
                case 2:
                    service.viewWeeklySummary();
                    break;
                case 3:
                    service.viewMonthlySummary();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
            }
        }
    }

    private void manageDailySales() {
        while (true) {
            System.out.println("\n----- 일별 매출 조회 -----");
            System.out.println("1. 특정 식당 일별 매출");
            System.out.println("2. 전체 식당 일별 매출");
            System.out.println("3. 특정 날짜 식당별 비교");
            System.out.println("0. 뒤로 가기");
            System.out.println("---------------------------");

            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:
                    viewDailySalesByCafeteria();
                    break;
                case 2:
                    viewAllDailySales();
                    break;
                case 3:
                    viewDailySalesByDate();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
            }
        }
    }

    private void viewDailySalesByCafeteria() {
        try {
            System.out.println("\n----- 특정 식당 일별 매출 -----");

            // 식당 선택
            Map<Integer, String> cafeterias = dao.getCafeteriaList();
            System.out.println("\n[식당 목록]");
            for (Map.Entry<Integer, String> entry : cafeterias.entrySet()) {
                System.out.printf("%d. %s\n", entry.getKey(), entry.getValue());
            }
            int cafeteriaId = getIntInput("식당 선택: ");

            // 기간 입력
            LocalDate startDate = getDateInput("시작 날짜 (YYYY-MM-DD): ");
            LocalDate endDate = getDateInput("종료 날짜 (YYYY-MM-DD): ");

            service.viewDailySalesByCafeteria(cafeteriaId, startDate, endDate);

        } catch (Exception e) {
            System.out.println("조회 중 오류 발생: " + e.getMessage());
        }
    }

    private void viewAllDailySales() {
        try {
            System.out.println("\n----- 전체 식당 일별 매출 -----");

            LocalDate startDate = getDateInput("시작 날짜 (YYYY-MM-DD): ");
            LocalDate endDate = getDateInput("종료 날짜 (YYYY-MM-DD): ");

            service.viewAllDailySales(startDate, endDate);

        } catch (Exception e) {
            System.out.println("조회 중 오류 발생: " + e.getMessage());
        }
    }

    private void viewDailySalesByDate() {
        try {
            System.out.println("\n----- 특정 날짜 식당별 비교 -----");

            LocalDate date = getDateInput("조회 날짜 (YYYY-MM-DD, 엔터시 오늘): ");

            service.viewDailySalesByDate(date);

        } catch (Exception e) {
            System.out.println("조회 중 오류 발생: " + e.getMessage());
        }
    }

    private void managePeriodSales() {
        while (true) {
            System.out.println("\n----- 기간별 매출 요약 -----");
            System.out.println("1. 특정 식당 기간별 요약");
            System.out.println("2. 전체 식당 기간별 요약");
            System.out.println("0. 뒤로 가기");
            System.out.println("----------------------------");

            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:
                    viewPeriodSalesSummary();
                    break;
                case 2:
                    viewAllPeriodSalesSummary();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
            }
        }
    }

    private void viewPeriodSalesSummary() {
        try {
            System.out.println("\n----- 특정 식당 기간별 요약 -----");

            // 식당 선택
            Map<Integer, String> cafeterias = dao.getCafeteriaList();
            System.out.println("\n[식당 목록]");
            for (Map.Entry<Integer, String> entry : cafeterias.entrySet()) {
                System.out.printf("%d. %s\n", entry.getKey(), entry.getValue());
            }
            int cafeteriaId = getIntInput("식당 선택: ");

            // 기간 입력
            LocalDate startDate = getDateInput("시작 날짜 (YYYY-MM-DD): ");
            LocalDate endDate = getDateInput("종료 날짜 (YYYY-MM-DD): ");

            service.viewPeriodSalesSummary(cafeteriaId, startDate, endDate);

        } catch (Exception e) {
            System.out.println("조회 중 오류 발생: " + e.getMessage());
        }
    }

    private void viewAllPeriodSalesSummary() {
        try {
            System.out.println("\n----- 전체 식당 기간별 요약 -----");

            LocalDate startDate = getDateInput("시작 날짜 (YYYY-MM-DD): ");
            LocalDate endDate = getDateInput("종료 날짜 (YYYY-MM-DD): ");

            service.viewAllPeriodSalesSummary(startDate, endDate);

        } catch (Exception e) {
            System.out.println("조회 중 오류 발생: " + e.getMessage());
        }
    }

   void manageHourlyUsage() {
        while (true) {
            System.out.println("\n----- 시간대별 이용 현황 -----");
            System.out.println("1. 특정 날짜 시간대별 현황");
            System.out.println("2. 기간별 시간대 평균 현황");
            System.out.println("0. 뒤로 가기");
            System.out.println("------------------------------");

            int choice = getIntInput("선택: ");

            switch (choice) {
                case 1:
                    viewHourlyUsageStats();
                    break;
                case 2:
                    viewAvgHourlyUsageStats();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 선택입니다.");
            }
        }
    }

    /**
     * 특정 날짜 시간대별 현황
     */
    private void viewHourlyUsageStats() {
        try {
            System.out.println("\n----- 특정 날짜 시간대별 현황 -----");

            // 식당 선택
            Map<Integer, String> cafeterias = dao.getCafeteriaList();
            System.out.println("\n[식당 목록]");
            for (Map.Entry<Integer, String> entry : cafeterias.entrySet()) {
                System.out.printf("%d. %s\n", entry.getKey(), entry.getValue());
            }
            int cafeteriaId = getIntInput("식당 선택: ");

            LocalDate date = getDateInput("조회 날짜 (YYYY-MM-DD, 엔터시 오늘): ");

            service.viewHourlyUsageStats(cafeteriaId, date);

        } catch (Exception e) {
            System.out.println("조회 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 기간별 시간대 평균 현황
     */
    private void viewAvgHourlyUsageStats() {
        try {
            System.out.println("\n----- 기간별 시간대 평균 현황 -----");

            // 식당 선택
            Map<Integer, String> cafeterias = dao.getCafeteriaList();
            System.out.println("\n[식당 목록]");
            for (Map.Entry<Integer, String> entry : cafeterias.entrySet()) {
                System.out.printf("%d. %s\n", entry.getKey(), entry.getValue());
            }
            int cafeteriaId = getIntInput("식당 선택: ");

            LocalDate startDate = getDateInput("시작 날짜 (YYYY-MM-DD): ");
            LocalDate endDate = getDateInput("종료 날짜 (YYYY-MM-DD): ");

            service.viewAvgHourlyUsageStats(cafeteriaId, startDate, endDate);

        } catch (Exception e) {
            System.out.println("조회 중 오류 발생: " + e.getMessage());
        }
    }

    // ========== 유틸리티 메서드 ==========

    /**
     * 정수 입력받기
     */
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

    /**
     * 날짜 입력받기
     */
    private LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    return LocalDate.now();
                }

                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("올바른 날짜 형식이 아닙니다. (YYYY-MM-DD)");
            }
        }
    }
}
