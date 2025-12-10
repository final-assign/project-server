package org.example.order;

import lombok.RequiredArgsConstructor;
import org.example.order.order_request.*;

@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    //얘도 임시
    public OrderDetailResponseDTO getOrder(OrderDetailRequestDTO orderDetailRequestDTO, long userId) {
        //user
        return orderService.getOrder(userId, orderDetailRequestDTO.getStartAt(), orderDetailRequestDTO.getEndAt());
    }

//    public OrderByRestaurantResponseDTO getOrderByRestaurant(OrderByRestaurantRequestDTO orderByRestaurantRequestDTO) {
//        return orderService.getOrderByRestaurant(orderByRestaurantRequestDTO.getRestaurantId());
//    }

    public OrderDetailResponseDTO getOrderHistory(OrderDetailRequestDTO requestDTO) {
        //admin
        return orderService.getOrderHistory(
                requestDTO.getRestaurantId(),
                requestDTO.getStartAt(),
                requestDTO.getEndAt()
        );
    }

//    public ResponseDTO updateStatus(OrderStatusUpdateRequestDTO requestDTO) {
//        return orderService.updateOrderStatus(requestDTO.getOrderId(), requestDTO.getNewStatus());
//    }
}
