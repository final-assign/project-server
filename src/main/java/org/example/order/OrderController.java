package org.example.order;

import lombok.RequiredArgsConstructor;
import org.example.general.GeneralResponseDTO;
import org.example.order.order_request.*;

@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    //얘도 임시
    public OrderDetailResponseDTO getOrder(OrderDetailRequestDTO orderDetailRequestDTO, long userId) {
        //user
        return orderService.getOrder(userId, orderDetailRequestDTO.getStartAt(), orderDetailRequestDTO.getEndAt());
    }

    public OrderDetailAdminResponseDTO getOrderAdminHistory(OrderDetailAdminRequestDTO requestDTO) {
        //admin
        return orderService.getOrderHistory(requestDTO.getRestaurantId(), requestDTO.getStartAt(), requestDTO.getEndAt());
    }


    public GeneralResponseDTO processCardOrder(OrderCardRequestDTO requestDTO, Long id) {

        return orderService.createCardOrder(requestDTO, id);
    }

    public GeneralResponseDTO processCouponOrder(OrderCouponRequestDTO requestDTO, Long id) {

        return orderService.createCouponOrder(requestDTO.getMenuId(), id);
    }
}
