package org.example.order;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Long userId;
    private Long menuId;
    private Long couponId;
    private PurchaseType purchaseType;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private int amount;
    private int purchasePrice;
}
