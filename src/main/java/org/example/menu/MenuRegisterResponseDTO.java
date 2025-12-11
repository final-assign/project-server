package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.general.Utils;

@RequiredArgsConstructor
public class MenuRegisterResponseDTO implements ResponseDTO {


    private final Long generatedId;

    public byte[] toBytes(){

        byte[] res = new byte[1 + 1 + 4 + 8]; // header + data
        res[0] = 0x02;
        res[1] = (byte) 0x87;
        Utils.intToBytes(8, res, 2);
        Utils.longToBytes(generatedId, res, 6);
        return res;
    }
}
