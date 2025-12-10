package org.example.login;

import lombok.Getter;
import org.example.general.Utils;

import java.util.Arrays;

@Getter
public class LoginRequestDTO {

    private final String id, pw;

    public LoginRequestDTO(byte[] body){

        int cursor = 0;
        int idLen = Utils.bytesToShort(body, cursor);
        cursor += 2;
        int pwLen = Utils.bytesToShort(body, cursor);
        cursor += 2;
        System.out.println(Arrays.toString(body));
        id = new String(body, cursor, idLen);
        cursor += idLen;
        System.out.println(id);
        pw = new String(body, cursor, pwLen);
    }
}
