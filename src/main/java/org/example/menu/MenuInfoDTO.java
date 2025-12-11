package org.example.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.general.Utils;

import java.nio.charset.StandardCharsets;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class MenuInfoDTO {

    private Long menuId;
    private String menuName;
    private int price;
    private boolean isDailyMenu;
    private String servedDate;

    public byte[] toBytes() {
        byte[] nameBytes = menuName.getBytes(StandardCharsets.UTF_8);
        byte[] dateBytes = (servedDate != null) ? servedDate.getBytes(StandardCharsets.UTF_8) : new byte[0];

        // 전체 크기 계산
        // ID(8) + 이름길이(2) + 이름내용 + 가격(4) + 데일리여부(1) + 날짜길이(2) + 날짜내용
        int totalSize = 8 + 2 + nameBytes.length + 4 + 1 + 2 + dateBytes.length;

        byte[] data = new byte[totalSize];
        int offset = 0;

        // 1. Menu ID (Long -> 8 bytes)
        offset = Utils.longToBytes(menuId, data, offset);

        // 2. Menu Name (Length 2bytes + String bytes)
        offset = Utils.shortToBytes((short) nameBytes.length, data, offset);
        System.arraycopy(nameBytes, 0, data, offset, nameBytes.length);
        offset += nameBytes.length;

        // 3. Price (Int -> 4 bytes)
        offset = Utils.intToBytes(price, data, offset);

        // 4. Is Daily Menu (Boolean -> 1 byte)
        data[offset++] = (byte) (isDailyMenu ? 1 : 0);

        // 5. Served Date (Length 2bytes + String bytes)
        offset = Utils.shortToBytes((short) dateBytes.length, data, offset);
        System.arraycopy(dateBytes, 0, data, offset, dateBytes.length);

        return data;
    }
}
