package org.example.menu;

import lombok.Getter;
import org.example.general.Utils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class MenuRegisterRequestDTO {

    private final String restaurantName;
    private final String menuName;
    private final Integer standardPrice;
    private final Integer studentPrice;
    private final Integer defaultAmount;
    private final LocalDate startSalesAt;
    private final LocalDate endSalesAt;
    private final Boolean isDailyMenu; // [추가됨] 상시/오늘의 메뉴 여부
    private final Long menuTypeId;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MenuRegisterRequestDTO(byte[] data) {
        int offset = 0;

        // 1. 식당 ID
        short restNameLen = Utils.bytesToShort(data, offset);
        offset += 2;
        this.restaurantName = new String(data, offset, restNameLen, StandardCharsets.UTF_8);
        offset += restNameLen;

        // 2. 메뉴 이름
        short menuNameLength = Utils.bytesToShort(data, offset);
        offset += 2;
        this.menuName = new String(data, offset, menuNameLength, StandardCharsets.UTF_8);
        System.out.println("메뉴 이름: " + menuName);
        offset += menuNameLength;

        // 3. 가격 및 수량
        this.standardPrice = Utils.bytesToInt(data, offset);
        offset += 4;
        this.studentPrice = Utils.bytesToInt(data, offset);
        offset += 4;
        this.defaultAmount = Utils.bytesToInt(data, offset);
        offset += 4;

        // 4. 시작일
        short startSalesAtLength = Utils.bytesToShort(data, offset);
        offset += 2;
        if (startSalesAtLength > 0) {
            String startSalesAtStr = new String(data, offset, startSalesAtLength, StandardCharsets.UTF_8);
            this.startSalesAt = LocalDate.parse(startSalesAtStr, DATE_FORMATTER);
            offset += startSalesAtLength;
        } else {
            this.startSalesAt = null;
        }

        // 5. 종료일
        short endSalesAtLength = Utils.bytesToShort(data, offset);
        offset += 2;
        if (endSalesAtLength > 0) {
            String endSalesAtStr = new String(data, offset, endSalesAtLength, StandardCharsets.UTF_8);
            this.endSalesAt = LocalDate.parse(endSalesAtStr, DATE_FORMATTER);
            offset += endSalesAtLength;
        } else {
            this.endSalesAt = null;
        }

        // 6. [추가됨] isDailyMenu (1 byte) 읽기
        // 클라이언트에서 보낸 1 (true) 또는 0 (false) 값을 읽어옴
        byte isDailyByte = data[offset];
        this.isDailyMenu = (isDailyByte == 1);
        offset += 1; // 1바이트만큼 오프셋 이동

        // 7. 메뉴 타입 ID
        this.menuTypeId = Utils.bytesToLong(data, offset);
    }
}