package org.example.general;

import lombok.Getter;

@Getter
public enum ResponseType {

    REQUEST((byte) 0x01), RESPONSE((byte) 0x02), RESULT((byte) 0x03);

    private final Byte value;

    ResponseType(Byte b){

        value = b;
    }
}