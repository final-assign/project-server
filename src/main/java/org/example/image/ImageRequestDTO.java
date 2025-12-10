package org.example.image;

import lombok.Getter;
import org.example.general.Utils;

@Getter
public class ImageRequestDTO {
    private final long menuId;

    //클라이언트가 보낸 메뉴 id 파싱
    public ImageRequestDTO(byte[] body) {
        int cursor = 0;

        int menuIdLen = Utils.bytesToShort(body, cursor);
        cursor += 2;
        this.menuId = Utils.bytesToLong(body, cursor);
    }
}
