package org.example.order;

import lombok.Getter;
import org.example.general.Utils;

@Getter
public class OrderRequestDTO {

    private final long menuId;
    private final long couponId; // 0이면 쿠폰 없음
    private final int charge;

    public OrderRequestDTO(byte[] body) {
        int cursor = 0;

        // 1. 메뉴 ID (8 byte)
        this.menuId = Utils.bytesToLong(body, cursor);
        cursor += 8;

        // 2. 쿠폰 ID (8 byte)
        this.couponId = Utils.bytesToLong(body, cursor);
        cursor += 8;

        // 3. 지불 금액 (4 byte)
        this.charge = Utils.bytesToInt(body, cursor);
    }
}
