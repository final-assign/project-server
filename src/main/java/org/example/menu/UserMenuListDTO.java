package org.example.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.general.ResponseDTO;
import org.example.general.Utils;

import java.util.List;

@Builder
@AllArgsConstructor
public class UserMenuListDTO implements ResponseDTO {

    private final List<MenuDTO> list;

    @Override
    public byte[] toBytes() {

        // 1) Body length 계산
        int bodyLength = 0;

        for (MenuDTO m : list) {
            bodyLength += 8;                       // id (long)
            bodyLength += Utils.getStrSize(m.getMenuName()); // menuName
            bodyLength += 4;                       // price
            bodyLength += 4;                       // amount
            bodyLength += 4;                       // isDailyMenu
        }

        // 전체 길이 = header(6) + body
        int totalLength = 6 + bodyLength;
        byte[] result = new byte[totalLength];
        int offset = 0;

        // 2) Header 작성
        result[offset++] = 0x02;   // 메시지 구분값
        result[offset++] = 0x15;   // 이번 메뉴 리스트 응답 타입

        offset = Utils.intToBytes(bodyLength, result, offset);

        // 3) Body 작성
        for (MenuDTO m : list) {

            offset = Utils.longToBytes(m.getId(), result, offset);

            offset = Utils.stringToBytes(m.getMenuName(), result, offset);

            offset = Utils.intToBytes(m.getPrice(), result, offset);

            offset = Utils.intToBytes(m.getAmount(), result, offset);

            offset = Utils.intToBytes(m.getIsDailyMenu(), result, offset);
        }

        return result;
    }
}
