package org.example.general;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

@Builder
@RequiredArgsConstructor
public class ErrorResponseDTO implements ResponseDTO{

    private final ResponseCode code;
    private final String msg = "";

    @Override
    public byte[] toBytes() {

        byte[] bMsg = msg.getBytes(StandardCharsets.UTF_8);
        int size = 1 + 1 + 4 + bMsg.length;

        byte[] res = new byte[size];

        res[0] = 0x02;
        res[1] = code.getValue();

        return res;
    }
}
