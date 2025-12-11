package org.example.order;

import lombok.Getter;
import org.example.general.Utils;

@Getter
public class OrderCardRequestDTO {

    private final int amount;
    private final Long menuId;

    public OrderCardRequestDTO(byte[] data) {

        this.amount = Utils.bytesToInt(data, 0);
        this.menuId = Utils.bytesToLong(data, 4);
    }
}
