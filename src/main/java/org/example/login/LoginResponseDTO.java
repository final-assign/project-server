package org.example.login;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType;
import org.example.general.Utils;
import org.example.user.UserType;

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

        byte[] data = loginResponseType == LoginResponseType.SUCCESS ? userType.toString().getBytes() : new byte[0];

        int totalSize = 1 + 1 + 4 + data.length; //타입, 코드, 데이터 길이
        byte[] res = new byte[totalSize];

        res[0] = resType.getValue();
        res[1] = loginResponseType.getValue(); //실수할 거 같으면 LoginRequestDTO처럼 cursor로 좌표 잡아가면서 하기.

        byte[] dataLen = Utils.intToBytes(data.length);
        System.arraycopy(dataLen, 0, res, 2, 4);
        System.arraycopy(data, 0, res, 6, data.length);

        return res;
    }
}
