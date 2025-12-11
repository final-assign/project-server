package org.example.general;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class GeneralResponseDTO implements ResponseDTO {

    private final ResponseCode code;

    @Override
    public byte[] toBytes() {

        byte[] res = new byte[6];
        res[0] = ResponseType.RESPONSE.getValue();
        res[1] = code.getValue();

        return res;
    }
}
