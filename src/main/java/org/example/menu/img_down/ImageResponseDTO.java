package org.example.menu.img_down;

import lombok.Builder;
import lombok.Data;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType;
import org.example.general.Utils;

@Data
@Builder
public class ImageResponseDTO implements ResponseDTO {

    private final ResponseType resType;
    private byte[] imageData;

    @Override
    public byte[] toBytes() {
        byte[] data = (imageData != null) ? imageData : new byte[0];

        int totalSize = 1 + 1 + 4 + data.length; //타입, 코드, 데이터 길이
        byte[] res = new byte[totalSize];

        res[0] = resType.getValue();
        res[1] = (byte) 0x21; //차피 고정이니 하드코딩

        byte[] dataLen = Utils.intToBytes(data.length);
        System.arraycopy(dataLen, 0, res, 2, 4);
        System.arraycopy(data, 0, res, 6, data.length);

        return res;
    }
}
