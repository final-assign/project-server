package org.example.menu;

public enum MenuResponseType {

    RESULT((byte) 0x87), FAILED((byte) 0x88);

    private final Byte value;

    MenuResponseType(Byte b){

        value = b;
    }
}
