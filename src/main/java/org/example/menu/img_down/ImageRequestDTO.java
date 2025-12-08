package org.example.menu.img_down;

import lombok.Getter;
import org.example.general.Utils;

@Getter
public class ImageRequestDTO {
    private final long menuId;

    //클라이언트가 보낸 메뉴 id 파싱
    public ImageRequestDTO(byte[] body) {
        this.menuId = Utils.bytesToLong(body, 0);
    }
}
