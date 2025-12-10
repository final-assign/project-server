package org.example.coupon;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseType;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class CouponService {
    private final CouponDAO couponDAO;

    public CouponResponseDTO getAllCoupons(long userId) {
        List<CouponDetail> coupons = couponDAO.findByUserId(userId);

        if (coupons.isEmpty()) {
            return CouponResponseDTO.builder()
                    .resType(ResponseType.RESPONSE)
                    .coupons(Collections.emptyList())
                    .build();
        }

        else{
            return CouponResponseDTO.builder()
                    .resType(ResponseType.RESPONSE)
                    .coupons(coupons)
                    .build();
        }
    }
}
