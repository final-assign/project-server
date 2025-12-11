package org.example.storage;

import lombok.Getter;
import org.example.general.Utils;

import java.io.ByteArrayInputStream;

@Getter
public class ImageRequestDTO {

    private final long menuId;
    private final ByteArrayInputStream imageStream;
    private int fileLen;
    //클라이언트가 보낸 메뉴 id 파싱
    public ImageRequestDTO(byte[] body) {
        int cursor = 0;

        menuId = Utils.bytesToLong(body, cursor);
        cursor += 8;
        fileLen = body.length - cursor;
        imageStream = new ByteArrayInputStream(body, cursor, body.length - cursor);
    }
}
