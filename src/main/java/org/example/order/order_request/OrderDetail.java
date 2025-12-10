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
    private String schoolId;
    private String menuName;
    private String restaurantName;
    private int couponPrice;
    private PurchaseType purchaseType;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private int purchasePrice;
}
