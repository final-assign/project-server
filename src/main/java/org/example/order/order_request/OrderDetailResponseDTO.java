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
public class OrderDetailResponseDTO implements ResponseDTO {
    private final ResponseType responseType;
    private final List<OrderDetail> orders;

    @Override
    public byte[] toBytes() {
        int bodySize = 0;

        bodySize += 4; //byte 배열 크기용

        for (OrderDetail order : orders) {
            bodySize += 28; //idLen 4 id 8  + amountLen 4 amount 4 + purchasePriceLen 4 purchasePrice 4 = 28 고정이니 하드코딩..

            //문자열 길이용 int + 문자열 길이
            bodySize += Utils.getStrSize(order.getSchoolId());
            bodySize += Utils.getStrSize(order.getMenuName());
            bodySize += Utils.getStrSize(order.getPurchaseType().toString());
            bodySize += Utils.getStrSize(order.getStatus().toString());
            bodySize += Utils.getStrSize(order.getCreatedAt().toString());
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

        //주문 계수
        System.arraycopy(Utils.intToBytes(orders.size()), 0, res, cursor, 4);
        cursor += 4;

        for (OrderDetail order : orders) {
            //order Id
            System.arraycopy(Utils.intToBytes(8), 0, res, cursor, 4);
            cursor += 4;
            System.arraycopy(Utils.longToBytes(order.getId()), 0, res, cursor, 8);
            cursor += 8;

            //메뉴 이름
            System.arraycopy(Utils.intToBytes(order.getMenuName().length()), 0, res, cursor, 4);
            cursor += 4;
            cursor = Utils.stringToBytes(order.getMenuName(), res, cursor);


            //식당 이름
            System.arraycopy(Utils.intToBytes(order.getMenuName().length()), 0, res, cursor, 4);
            cursor += 4;
            cursor = Utils.stringToBytes(order.getRestaurantName(), res, cursor);

            //결제 가격
            System.arraycopy(Utils.intToBytes(4), 0, res, cursor, 4);
            cursor += 4;
            System.arraycopy(Utils.intToBytes(order.getPrice()), 0, res, cursor, 4);
            cursor += 4;

            //쿠폰 가격
            System.arraycopy(Utils.intToBytes(4), 0, res, cursor, 4);
            cursor += 4;
            System.arraycopy(Utils.intToBytes(order.getCouponPrice()), 0, res, cursor, 4);
            cursor += 4;

            //구매 유형
            System.arraycopy(Utils.intToBytes(Utils.getStrSize(order.getPurchaseType().toString())), 0, res, cursor, 4);
            cursor += 4;
            cursor = Utils.stringToBytes(order.getPurchaseType().toString(), res, cursor);

            //상태
            System.arraycopy(Utils.intToBytes(Utils.getStrSize(order.getStatus().toString())), 0, res, cursor, 4);
            cursor += 4;
            cursor = Utils.stringToBytes(order.getStatus().toString(), res, cursor);

            //결제 시간
            System.arraycopy(Utils.intToBytes(Utils.getStrSize(order.getCreatedAt().toString())), 0, res, cursor, 4);
            cursor += 4;
            cursor = Utils.stringToBytes(order.getCreatedAt().toString(), res, cursor);
        }

        return res;
    }
}
