package org.example.coupon;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    public CouponResponseDTO getCoupons(long userId) {
        return couponService.getAllCoupons(userId);
    }
}
