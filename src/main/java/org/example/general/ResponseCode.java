package org.example.general;

import lombok.Getter;

@Getter
public enum ResponseCode {

    FORBIDDEN((byte)0xEF),
    MENU_SUCCESS_REGISTER((byte) 0x87);

    private final Byte value;

    ResponseCode(Byte b){

        value = b;
    }
}
