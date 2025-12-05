package org.example.coupon;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    private Long id;
    private Long menuId;            // FK
    private Long couponPrice;
}
