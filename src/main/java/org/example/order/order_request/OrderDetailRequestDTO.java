package org.example.order.order_request;

import lombok.Getter;
import org.example.general.Utils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class OrderDetailRequestDTO {
    private final long restaurantId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;

    public OrderDetailRequestDTO(byte[] body) {
        int cursor = 0;
        //식당 ID 8
        restaurantId = Utils.bytesToLong(body, cursor);
        cursor += 8;

        // startAt str len 4 + data
        int startLen = Utils.bytesToInt(body, cursor);
        cursor += 4;
        String startStr = new String(body, cursor, startLen, StandardCharsets.UTF_8);
        //날짜만 입력받음
        LocalDate startDate = LocalDate.parse(startStr, DateTimeFormatter.ISO_DATE);
        //파싱 후 00:00:00 붙이기
        startAt = startDate.atStartOfDay();
        cursor += startLen;

        // endAt str len 4 + data
        int endLen = Utils.bytesToInt(body, cursor);
        cursor += 4;
        String endStr = new String(body, cursor, endLen, StandardCharsets.UTF_8);
        LocalDate endDate = LocalDate.parse(endStr, DateTimeFormatter.ISO_DATE);
        //파싱 후 23:59:59 붙이기
        endAt = endDate.atTime(LocalTime.MAX);
    }
}
