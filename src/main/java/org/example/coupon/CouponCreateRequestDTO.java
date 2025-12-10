package org.example.coupon;

import lombok.Getter;
import org.example.general.Utils;

@Getter
public class CouponCreateRequestDTO {

    private final Long restId;
    private final Long menuId;

    public CouponCreateRequestDTO(byte[] body) {
        int cursor = 0;

        // 1. 식당 ID (8 byte)
        this.restId = Utils.bytesToLong(body, cursor);
        cursor += 8;

        // 2. 메뉴 ID (8 byte)
        this.menuId = Utils.bytesToLong(body, cursor);
    }
}
