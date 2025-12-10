package org.example.storage;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType;
import org.example.general.Utils;

@Builder
@Data
@RequiredArgsConstructor
public class ImageResponseDTO implements ResponseDTO {
    private final byte[] imageBytes;
    private final ResponseType resType;

    @Override
    public byte[] toBytes() {
        //사진 없으면 빈채로 냅두기..
        byte[] data = (imageBytes != null) ? imageBytes : new byte[0];
        int totalSize = 1 + 1 + 4 + data.length;

        byte[] res =  new byte[totalSize];

        //type 채우기
        res[0] = resType.getValue();
        //사진은 고정이니 응답 하드코딩
        res[1] = (byte) 0x21;

        byte[] dataLen = Utils.intToBytes(data.length);
        System.arraycopy(dataLen, 0, res, 2, 4);
        System.arraycopy(data, 0, res, 6, data.length);

        return res;
    }
}
