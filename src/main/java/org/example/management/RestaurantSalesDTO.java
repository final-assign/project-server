package org.example.management;

import lombok.*;
import org.example.general.ResponseDTO;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSalesDTO implements ResponseDTO {

    private Long restaurantId;
    private String restaurantName;

    // 전체 매출 요약
    private OverallSales overallSales;

    // 일자별 매출
    private List<DailySales> dailySalesList;

    // 시간대별 매출
    private List<TimeSales> timeSalesList;

    @Override
    public byte[] toBytes() {
        StringBuilder sb = new StringBuilder();

        sb.append("restaurantId=").append(restaurantId).append(";");
        sb.append("restaurantName=").append(restaurantName).append(";");

        if (overallSales != null) {
            sb.append("overallSales={")
                    .append("orderCount=").append(overallSales.getOrderCount()).append(",")
                    .append("salesAmount=").append(overallSales.getSalesAmount()).append(",")
                    .append("avgOrderPrice=").append(overallSales.getAvgOrderPrice())
                    .append("};");
        }

        if (dailySalesList != null) {
            sb.append("dailySales=[");
            for (DailySales ds : dailySalesList) {
                sb.append("{date=").append(ds.getDate())
                        .append(",orderCount=").append(ds.getOrderCount())
                        .append(",salesAmount=").append(ds.getSalesAmount())
                        .append("},");
            }
            sb.append("];");
        }


        if (timeSalesList != null) {
            sb.append("timeSales=[");
            for (TimeSales ts : timeSalesList) {
                sb.append("{timeRange=").append(ts.getTimeRange())
                        .append(",orderCount=").append(ts.getOrderCount())
                        .append(",salesAmount=").append(ts.getSalesAmount())
                        .append("},");
            }
            sb.append("];");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverallSales {
        private Integer orderCount;       // 총 주문 수
        private Integer salesAmount;      // 총 매출 금액
        private Integer avgOrderPrice;    // 평균 객단가 = 매출/주문수
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySales {
        private LocalDate date;
        private Integer orderCount;
        private Integer salesAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSales {
        private String timeRange; // ex: "12:00~13:00"
        private Integer orderCount;
        private Integer salesAmount;
    }
}
