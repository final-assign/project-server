package org.example.coupon;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponInventory {
    private Long couponId;
    private Long userId;
    private int quantity;
}
