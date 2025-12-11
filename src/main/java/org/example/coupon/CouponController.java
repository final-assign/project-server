package org.example.coupon;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;

@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    public CouponResponseDTO getCoupons(long userId) {
        return couponService.getAllCoupons(userId);
    }

    public ResponseDTO createCoupon(CouponCreateRequestDTO dto) {

        return couponService.createCouponsForMenu(dto);
    }

    public MenuCouponResponseDTO getCountByMenuId(MenuCouponRequestDTO req, long userId){

        return couponService.getCouponByMenu(req, userId);
    }
}
