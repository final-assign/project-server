package org.example.order.order_request;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType;
import org.example.general.Utils;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
public class OrderResponseDTO implements ResponseDTO {
    private final ResponseType responseType;
    private final List<OrderDetail> orders;

    @Override
    public byte[] toBytes() {
        int bodySize = 0;

        bodySize += 4; //byte 배열 크기용

        for (OrderDetail order : orders) {
            bodySize += 32; //id 8 + menuId 8 + couponId 8 + amount 4 + purchasePrice 4 = 36 고정이니 하드코딩..

            //문자열 길이용 int + 문자열 길이
            bodySize += 4 + getByteLength(order.getPurchaseType().toString());
            bodySize += 4 + getByteLength(order.getStatus().toString());
            bodySize += 4 + getByteLength(order.getCreatedAt().toString());
        }

        int totalSize = 1 + 1 + 4 + bodySize;
        byte[] res = new byte[totalSize];
        int cursor = 0;

        res[cursor++] = (byte) responseType.getValue();
        //얘도 주문내역 전송만 해서 하드코딩
        res[cursor++] = (byte) 0x41;

        //body 길이
        System.arraycopy(Utils.intToBytes(bodySize), 0, res, cursor, 4);
        cursor += 4;

        //주문 개수
        System.arraycopy(Utils.intToBytes(orders.size()), 0, res, cursor, 4);
        cursor += 4;

        for (OrderDetail order : orders) {
            System.arraycopy(Utils.longToBytes(order.getId()), 0, res, cursor, 8);
            cursor += 8;
            System.arraycopy(Utils.longToBytes(order.getMenuId()), 0, res, cursor, 8);
            cursor += 8;

            //coupon null 가능.
            long cId = 0L;
            if(order.getCouponId() != null)
                cId = order.getCouponId();

            System.arraycopy(Utils.longToBytes(cId), 0, res, cursor, 8);
            cursor += 8;
            System.arraycopy(Utils.intToBytes(order.getAmount()), 0, res, cursor, 4);
            cursor += 4;
            System.arraycopy(Utils.intToBytes(order.getPurchasePrice()), 0, res, cursor, 4);
            cursor += 4;
            System.arraycopy(Utils.intToBytes(order.getCouponPrice()), 0, res, cursor, 4);
            cursor += 4;

            cursor = writeString(res, cursor, order.getPurchaseType().toString());
            cursor = writeString(res, cursor, order.getStatus().toString());
            cursor = writeString(res, cursor, order.getCreatedAt().toString());
            cursor = writeString(res, cursor, order.getMenuName());
        }

        return res;
    }

    //문자열 길이 구하는 함수
    private int getByteLength(String str) {
        return (str == null) ? 0 : str.getBytes().length;
    }

    //배열에 문자열 쓰는 함수
    private int writeString(byte[] dest, int cursor, String str) {
        if (str == null) str = "";

        byte[] bytes = str.getBytes();

        //문자열 길이
        System.arraycopy(Utils.intToBytes(bytes.length), 0, dest, cursor, 4);
        cursor += 4;
        //문자열
        System.arraycopy(bytes, 0, dest, cursor, bytes.length);
        cursor += bytes.length;

        return cursor;
    }
}
