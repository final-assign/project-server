package org.example.menu;

import lombok.Getter;
import org.example.general.Utils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class MenuRegisterRequestDTO {

    final private Long restId;
    final private String menuName;
    final private Integer standardPrice;
    final private Integer studentPrice;
    final private Integer defaultAmount;

    final private LocalDate startSalesAt;
    final private LocalDate endSalesAt;

    final private Long menuTypeId;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MenuRegisterRequestDTO(byte[] body) {

        int cursor = 0;

        // 1. 식당 ID (8 byte)
        this.restId = Utils.bytesToLong(body, cursor);
        cursor += 8;

        // 2. 메뉴 이름 (Length + String)
        short menuNameLength = Utils.bytesToShort(body, cursor);
        cursor += 2;
        this.menuName = new String(body, cursor, menuNameLength, StandardCharsets.UTF_8);
        cursor += menuNameLength;

        // 3. 가격 및 수량 (4 byte * 3)
        this.standardPrice = Utils.bytesToInt(body, cursor);
        cursor += 4;

        this.studentPrice = Utils.bytesToInt(body, cursor);
        cursor += 4;

        this.defaultAmount = Utils.bytesToInt(body, cursor);
        cursor += 4;

        short startDateLength = Utils.bytesToShort(body, cursor);
        cursor += 2;
        if (startDateLength == 0) {
            this.startSalesAt = null;
        } else {
            String dateString = new String(body, cursor, startDateLength, StandardCharsets.UTF_8);
            this.startSalesAt = LocalDate.parse(dateString, DATE_FORMATTER);
            cursor += startDateLength;
        }

        short endDateLength = Utils.bytesToShort(body, cursor);
        cursor += 2;
        if (endDateLength == 0) {
            this.endSalesAt = null;
        } else {
            String dateString = new String(body, cursor, endDateLength, StandardCharsets.UTF_8);
            this.endSalesAt = LocalDate.parse(dateString, DATE_FORMATTER);
            cursor += endDateLength;
        }

        // 6. 메뉴 타입 ID (8 byte)
        this.menuTypeId = Utils.bytesToLong(body, cursor);
        // cursor += 8;
    }
}