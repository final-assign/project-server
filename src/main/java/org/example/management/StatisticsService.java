package org.example.management;


import java.util.List;

public class StatisticsService {

    private final StatisticsDAO statisticsDAO = new StatisticsDAO();

    public RestaurantSalesDTO getRestaurantSales(Long restaurantId) {

        // 전체 매출
        RestaurantSalesDTO total = statisticsDAO.findTotalSales(restaurantId);

        // 일자별 매출
        List<RestaurantSalesDTO.DailySales> daily = statisticsDAO.findDailySales(restaurantId);

        // 시간대별 매출
        List<RestaurantSalesDTO.TimeSales> time = statisticsDAO.findTimeSales(restaurantId);

        total.setDailySalesList(daily);
        total.setTimeSalesList(time);

        return total;
    }
}
