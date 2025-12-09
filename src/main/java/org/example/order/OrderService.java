package org.example.order;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseType;
import org.example.order.order_request.OrderDetailDAO;
import org.example.order.order_request.OrderDetail;
import org.example.order.order_request.OrderResponseDTO;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class OrderService {
    private final OrderDetailDAO orderDetailDAO;

    public OrderResponseDTO getOrder(Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        List<OrderDetail> orders = orderDetailDAO.findByUserId(userId, startAt, endAt);

        if(orders.isEmpty()){
            return OrderResponseDTO.builder()
                    .responseType(ResponseType.RESPONSE)
                    .orders(Collections.emptyList())
                    .build();
        }

        return OrderResponseDTO.builder()
                .responseType(ResponseType.RESPONSE)
                .orders(orders)
                .build();
    }
}
