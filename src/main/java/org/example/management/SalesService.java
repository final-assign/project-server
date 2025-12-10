package org.example.management;

import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;

public class SalesService {
    private SalesDAO dao;

    public SalesService(SalesDAO dao) {
        this.dao = dao;
    }

    public void viewDailySalesByCafeteria(int cafeteriaId, LocalDate startDate, LocalDate endDate) {
        try {
            List<DailySalesStats> statsList = dao.getDailySalesByCafeteria(cafeteriaId, startDate, endDate);

            if (statsList.isEmpty()) {
                System.out.println("해당 기간의 매출 데이터가 없습니다.");
                return;
            }

            System.out.println("\n========== 일별 매출 통계 ==========");
            System.out.printf("기간: %s ~ %s\n\n", startDate, endDate);

            for (DailySalesStats stats : statsList) {
                System.out.println(stats);
            }
            System.out.println("====================================\n");

        } catch (SQLException e) {
            System.out.println("매출 조회 실패: " + e.getMessage());
        }
    }

    public void viewAllDailySales(LocalDate startDate, LocalDate endDate) {
        try {
            List<DailySalesStats> statsList = dao.getAllDailySales(startDate, endDate);

            if (statsList.isEmpty()) {
                System.out.println("해당 기간의 매출 데이터가 없습니다.");
                return;
            }

            System.out.println("\n========== 전체 식당 일별 매출 ==========");
            System.out.printf("기간: %s ~ %s\n\n", startDate, endDate);

            for (DailySalesStats stats : statsList) {
                System.out.println(stats);
            }
            System.out.println("=========================================\n");

        } catch (SQLException e) {
            System.out.println("매출 조회 실패: " + e.getMessage());
        }
    }

    public void viewDailySalesByDate(LocalDate date) {
        try {
            List<DailySalesStats> statsList = dao.getDailySalesByDate(date);

            if (statsList.isEmpty()) {
                System.out.println("해당 날짜의 매출 데이터가 없습니다.");
                return;
            }

            System.out.println("\n========== 식당별 매출 비교 ==========");
            System.out.printf("날짜: %s\n\n", date);

            for (DailySalesStats stats : statsList) {
                System.out.println(stats);
            }
            System.out.println("======================================\n");

        } catch (SQLException e) {
            System.out.println("매출 조회 실패: " + e.getMessage());
        }
    }

    public void viewPeriodSalesSummary(int cafeteriaId, LocalDate startDate, LocalDate endDate) {
        try {
            PeriodSalesSummary summary = dao.getPeriodSalesSummary(cafeteriaId, startDate, endDate);

            if (summary == null) {
                System.out.println("해당 기간의 매출 데이터가 없습니다.");
                return;
            }

            System.out.println("\n========== 기간별 매출 요약 ==========");
            System.out.println(summary);
            System.out.println("====================================\n");

        } catch (SQLException e) {
            System.out.println("매출 요약 조회 실패: " + e.getMessage());
        }
    }

    public void viewAllPeriodSalesSummary(LocalDate startDate, LocalDate endDate) {
        try {
            List<PeriodSalesSummary> summaries = dao.getAllPeriodSalesSummary(startDate, endDate);

            if (summaries.isEmpty()) {
                System.out.println("해당 기간의 매출 데이터가 없습니다.");
                return;
            }

            System.out.println("\n========== 전체 식당 기간별 매출 요약 ==========");
            for (PeriodSalesSummary summary : summaries) {
                System.out.println(summary);
                System.out.println("-------------------------------------------");
            }
            System.out.println("==============================================\n");

        } catch (SQLException e) {
            System.out.println("매출 요약 조회 실패: " + e.getMessage());
        }
    }


    public void viewHourlyUsageStats(int cafeteriaId, LocalDate date) {
        try {
            List<HourlyUsageStats> statsList = dao.getHourlyUsageStats(cafeteriaId, date);

            if (statsList.isEmpty()) {
                System.out.println("해당 날짜의 이용 데이터가 없습니다.");
                return;
            }

            System.out.println("\n========== 시간대별 이용 현황 ==========");
            System.out.printf("[%s] %s\n\n", statsList.get(0).getCafeteriaName(), date);

            for (HourlyUsageStats stats : statsList) {
                System.out.println(stats);
            }
            System.out.println("=======================================\n");

        } catch (SQLException e) {
            System.out.println("이용 현황 조회 실패: " + e.getMessage());
        }
    }

    public void viewAvgHourlyUsageStats(int cafeteriaId, LocalDate startDate, LocalDate endDate) {
        try {
            List<HourlyUsageStats> statsList = dao.getAvgHourlyUsageStats(cafeteriaId, startDate, endDate);

            if (statsList.isEmpty()) {
                System.out.println("해당 기간의 이용 데이터가 없습니다.");
                return;
            }

            System.out.println("\n========== 시간대별 평균 이용 현황 ==========");
            System.out.printf("[%s] %s ~ %s\n\n", statsList.get(0).getCafeteriaName(), startDate, endDate);

            // 피크 시간대 찾기
            HourlyUsageStats peakHour = statsList.stream()
                    .max((s1, s2) -> s1.getOrderCount() - s2.getOrderCount())
                    .orElse(null);

            for (HourlyUsageStats stats : statsList) {
                String indicator = (peakHour != null && stats.getHourSlot() == peakHour.getHourSlot())
                        ? " [피크타임]" : "";
                System.out.println(stats + indicator);
            }

            if (peakHour != null) {
                System.out.printf("\n※ 가장 혼잡한 시간: %s (평균 %d건)\n",
                        peakHour.getTimeSlotString(), peakHour.getOrderCount());
            }

            System.out.println("===========================================\n");

        } catch (SQLException e) {
            System.out.println("이용 현황 조회 실패: " + e.getMessage());
        }
    }


    public void viewTodayDashboard() {
        LocalDate today = LocalDate.now();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        오늘의 식당 운영 현황          ║");
        System.out.println("╚════════════════════════════════════════╝");

        viewDailySalesByDate(today);
    }

    public void viewWeeklySummary() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         이번 주 매출 요약             ║");
        System.out.println("╚════════════════════════════════════════╝");

        viewAllPeriodSalesSummary(weekAgo, today);
    }

    public void viewMonthlySummary() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         이번 달 매출 요약             ║");
        System.out.println("╚════════════════════════════════════════╝");

        viewAllPeriodSalesSummary(monthStart, today);
    }
}