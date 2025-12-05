package org.example.order;

import lombok.*;

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
    private String createdAt;
    private int amount;
}
