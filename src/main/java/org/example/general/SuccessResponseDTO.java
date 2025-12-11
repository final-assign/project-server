package org.example.general;

public class SuccessResponseDTO implements ResponseDTO{

    @Override
    public byte[] toBytes() {

        byte[] res = new byte[6];
        res[0] = 0x02;
        res[1] = (byte) 0x92;
        return res;
    }
}
