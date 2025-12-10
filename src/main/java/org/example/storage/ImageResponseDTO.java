package org.example.storage;

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
        int dataLength = data.length;

        // size: resType(1) + responseCode(1) + dataLength(4) + data(N)
        int totalSize = 1 + 1 + 4 + dataLength;
        byte[] res = new byte[totalSize];

        int offset = 0;
        res[offset++] = resType.getValue();
        res[offset++] = (byte) 0x21; // 이미지 응답 코드

        offset = Utils.intToBytes(dataLength, res, offset);
        System.arraycopy(data, 0, res, offset, dataLength);

        return res;
    }
}
