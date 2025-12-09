package org.example.order.order_request;

import lombok.Getter;
import org.example.general.Utils;

import java.time.LocalDateTime;

@Getter
public class OrderRequestDTO {
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;

    public OrderRequestDTO(byte[] body) {
        int cursor = 0;
        int startLen = Utils.bytesToInt(body, cursor);
        cursor += 4;
        int endLen = Utils.bytesToInt(body, cursor);
        cursor += 4;

        startAt = LocalDateTime.parse(new String(body, cursor, startLen));
        cursor += startLen;
        endAt = LocalDateTime.parse(new String(body, cursor, endLen));
    }
}
