package org.example.coupon;

import lombok.Getter;
import org.example.general.Utils;

@Getter
public class MenuCouponRequestDTO {

    private final Long menuId;

    public MenuCouponRequestDTO(byte[] data) {

        menuId = Utils.bytesToLong(data, 0);
    }
}
