package org.example.general;

import lombok.Getter;

@Getter
public enum ResponseCode {

    FORBIDDEN((byte)0xEF);

    private final Byte value;

    ResponseCode(Byte b){

        value = b;
    }
}
