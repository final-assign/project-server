package org.example.login;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType;
import org.example.general.Utils;
import org.example.user.UserType;

import java.nio.charset.StandardCharsets;

@Builder
@Data
@RequiredArgsConstructor
public class LoginResponseDTO implements ResponseDTO {

    private final ResponseType resType;
    private final LoginResponseType loginResponseType;
    private final UserType userType;
    private final Long userId;

    @Override
    public byte[] toBytes() {

        byte[] data = (loginResponseType == LoginResponseType.SUCCESS && userType != null)
                ? userType.toString().getBytes(StandardCharsets.UTF_8)
                : new byte[0];
        int dataLength = data.length;

        // size: resType(1) + loginResponseType(1) + dataLength(4) + data(N)
        int totalSize = 1 + 1 + 4 + dataLength;
        byte[] res = new byte[totalSize];

        int offset = 0;
        res[offset++] = resType.getValue();
        res[offset++] = loginResponseType.getValue();

        offset = Utils.intToBytes(dataLength, res, offset);
        System.arraycopy(data, 0, res, offset, dataLength);

        return res;
    }
}
