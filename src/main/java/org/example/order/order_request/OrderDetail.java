package org.example.order.order_request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.order.OrderStatus;
import org.example.order.PurchaseType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    private Long id;
        private Long userId;
        private Long menuId;
        private Long couponId;
        private PurchaseType purchaseType;
        private OrderStatus status;
        private LocalDateTime createdAt;
        private int amount;
        private int purchasePrice;

        private String menuName;
        private int couponPrice;
}
