package org.example.menu;

import lombok.Getter;
import org.example.general.Utils;

import java.nio.charset.StandardCharsets;

@Getter
public class MenuListRequestDTO {

    private final String restaurantName;


    public MenuListRequestDTO(byte[] data) {

        int offset = 0;
        // 문자열 길이 파싱 (Short: 2byte)
        short len = Utils.bytesToShort(data, offset);
        offset += 2;
        // 문자열 본문 파싱
        this.restaurantName = new String(data, offset, len, StandardCharsets.UTF_8);
    }
}
