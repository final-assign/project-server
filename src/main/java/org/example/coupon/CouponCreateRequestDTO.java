package org.example.coupon;

import lombok.Getter;
import org.example.general.Utils;

@Getter

public class CouponCreateRequestDTO {

    private final Integer amount;
    private final Long menuId;

    public CouponCreateRequestDTO(byte[] bytes) {
        int offset = 0;

        this.menuId = Utils.bytesToLong(bytes, offset);
        offset += 8;

        this.amount = Utils.bytesToInt(bytes, offset);
        offset += 4;
    }

}
