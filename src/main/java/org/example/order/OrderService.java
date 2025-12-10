package org.example.order;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseType;
import org.example.order.order_request.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class OrderService {
    private final OrderDetailDAO orderDetailDAO;

    public OrderDetailResponseDTO getOrder(Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        List<OrderDetail> orders = orderDetailDAO.findByUserId(userId, startAt, endAt);

        if (orders.isEmpty()) {
            return OrderDetailResponseDTO.builder()
                    .responseType(ResponseType.RESPONSE)
                    .orders(Collections.emptyList())
                    .build();
        }

        return OrderDetailResponseDTO.builder()
                .responseType(ResponseType.RESPONSE)
                .orders(orders)
                .build();
    }

//    public OrderByRestaurantResponseDTO getOrderByRestaurant(Long restaurantId) {
//        List<OrderDetail> orders = orderDetailDAO.findByRestaurant(restaurantId);
//
//        if (orders.isEmpty()) {
//            return OrderByRestaurantResponseDTO.builder()
//                    .responseType(ResponseType.RESPONSE)
//                    .dailySalesList(Collections.emptyList())
//                    .build();
//        }
//
//        List<OrderByRestaurantResponseDTO.DailySales> salesList = new ArrayList<>();
//
//        //최신 주문 날짜
//        LocalDateTime currentDate = orders.get(0).getCreatedAt();
//        int currentTotalSales = 0;
//
//        for (OrderDetail order : orders) {
//            LocalDateTime orderDate = order.getCreatedAt();
//
//            // DAO에서 정렬 했으므로 바뀌면 날짜 변경됐다는 뜻
//            if (!orderDate.equals(currentDate)) {
//                //날짜가 바뀌면, 지금까지 합산한 내용을 저장
//                salesList.add(OrderByRestaurantResponseDTO.DailySales.builder()
//                        .date(currentDate)
//                        .totalSales(currentTotalSales)
//                        .build());
//
//                //새로운 날짜로 갱신 및 합계 초기화
//                currentDate = orderDate;
//                currentTotalSales = 0;
//            }
//
//            // 현재 주문 금액 누적
//            currentTotalSales += order.getPurchasePrice();
//        }
//
//        salesList.add(OrderByRestaurantResponseDTO.DailySales.builder()
//                .date(currentDate)
//                .totalSales(currentTotalSales)
//                .build());
//
//        return OrderByRestaurantResponseDTO.builder()
//                .responseType(ResponseType.RESPONSE)
//                .dailySalesList(salesList)
//                .build();
//    }

    public OrderDetailAdminResponseDTO getOrderHistory(Long restaurantId, LocalDateTime startAt, LocalDateTime endAt) {
        List<OrderDetail> orders = orderDetailDAO.findByRestaurantAndTime(restaurantId, startAt, endAt);

        //결과가 없으면 빈 리스트
        if (orders.isEmpty()) {
             return OrderDetailAdminResponseDTO.builder()
                    .responseType(ResponseType.RESPONSE)
                    .orders(Collections.emptyList())
                    .build();
        }

        return OrderDetailAdminResponseDTO.builder()
                .responseType(ResponseType.RESPONSE)
                .orders(orders)
                .build();
    }
//
//    public OrderResultResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
//            // 1. 상태 업데이트 실행
//            boolean isUpdated = orderDetailDAO.updateStatus(orderId, newStatus);
//
//            if (isUpdated) {
//                // [트랜잭션 처리] 주문 취소 시 재고/쿠폰 복구 로직
//                if (newStatus == OrderStatus.CANCELED) {
//                    // TODO: 여기에 복구 로직 메서드 호출 (예: restoreStock(orderId))
//                    // 1. Order ID로 주문 상세 조회
//                    // 2. Menu ID, Amount 가져와서 Menu 테이블 재고 증가
//                    // 3. Coupon ID가 있다면 Coupon_Inventory 반환
//                    System.out.println("[System] 주문(" + orderId + ")이 취소되어 재고를 복구합니다.");
//                }
//                return new OrderResultResponseDTO(true, ""); // 성공 (0x01)
//            } else {
//                return new OrderResultResponseDTO(false, "Order ID not found or DB Error"); // 실패 (0x02)
//            }
//        }
/*
    public HourlyStatsResponseDTO getHourlyStatsByRestaurant(Long restaurantId, LocalDateTime startAt, LocalDateTime endAt) {
            //startAt endAt로 범위 설정
            List<OrderDetail> orders = orderDetailDAO.findByRestaurantAndTime(restaurantId, startAt, endAt);

            if (orders.isEmpty()) {
                return HourlyStatsResponseDTO.builder()
                        .responseType(ResponseType.RESPONSE)
                        .stats(Collections.emptyList())
                        .build();
            }

            //0시부터 23시 까지..
            int[] hourlyCounts = new int[24];
            for (OrderDetail order : orders) {
                int hour = order.getCreatedAt().getHour();
                hourlyCounts[hour]++;
            }

            List<HourlyStatsResponseDTO.HourlyStat> statsList = new ArrayList<>();

            for (int i = 0; i < 24; i++) {
                statsList.add(HourlyStatsResponseDTO.HourlyStat.builder()
                        .hour(i)
                        .count(hourlyCounts[i])
                        .build());
            }

            return HourlyStatsResponseDTO.builder()
                    .responseType(ResponseType.RESPONSE)
                    .stats(statsList)
                    .build();
        }
        */
}
