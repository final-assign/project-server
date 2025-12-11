package org.example.general;

import lombok.Getter;

@Getter
public enum ResponseCode {

    FORBIDDEN((byte)0xEF),
    MENU_SUCCESS_REGISTER((byte) 0x87),
    ORDER_SUCCESS((byte)0x33),
    ORDER_FAILURE((byte)0x34),
    COUPON_BY_MENU((byte)0x39);

    private final Byte value;

    ResponseCode(Byte b){

        value = b;
    }
}
