package org.example.general;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

@Builder
@RequiredArgsConstructor
public class ErrorResponseDTO implements ResponseDTO{

    private final ResponseCode code;
    private final String msg;

    @Override
    public byte[] toBytes() {
        // Utils.stringToBytes를 사용하면 더 좋지만, 일단 직접 구현을 수정합니다.
        byte[] msgBytes = (this.msg == null) ? new byte[0] : this.msg.getBytes(StandardCharsets.UTF_8);
        int msgLength = msgBytes.length;

        // size: type(1) + code(1) + msgLength(4) + msg(N)
        int size = 1 + 1 + 4 + msgLength;
        byte[] res = new byte[size];

        int offset = 0;
        res[offset++] = 0x02; // Error Type
        res[offset++] = code.getValue();

        // msg 길이를 Big-Endian으로 쓴다.
        res[offset++] = (byte) ((msgLength >> 24) & 0xFF);
        res[offset++] = (byte) ((msgLength >> 16) & 0xFF);
        res[offset++] = (byte) ((msgLength >> 8) & 0xFF);
        res[offset++] = (byte) (msgLength & 0xFF);

        // msg 본문을 쓴다.
        System.arraycopy(msgBytes, 0, res, offset, msgLength);

        return res;
    }
}
