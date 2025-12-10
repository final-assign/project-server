package org.example.order;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    public void processOrder(OrderRequestDTO requestDTO, Long id) {
        orderService.createOrder(requestDTO, id);
    }
}
