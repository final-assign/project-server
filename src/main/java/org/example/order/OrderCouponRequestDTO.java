package org.example.order;

import lombok.Getter;
import org.example.general.Utils;

@Getter
public class OrderCouponRequestDTO {

    private final Long menuId;

    public OrderCouponRequestDTO(byte[] data) {

        this.menuId = Utils.bytesToLong(data, 0);
    }
}
