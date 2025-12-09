package org.example.order;

import lombok.RequiredArgsConstructor;
import org.example.order.order_request.OrderRequestDTO;
import org.example.order.order_request.OrderResponseDTO;

@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    //얘도 임시
    public OrderResponseDTO getOrder(OrderRequestDTO orderRequestDTO, long userId) {
        return orderService.getOrder(userId, orderRequestDTO.getStartAt(), orderRequestDTO.getEndAt());
    }
}
