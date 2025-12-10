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
public class OrderDetailAdminResponseDTO implements ResponseDTO {
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
        cursor += Utils.intToBytes(bodySize, res, cursor);

        //주문 계수
        cursor += Utils.intToBytes(orders.size(), res, cursor);

        for (OrderDetail order : orders) {
            //order Id 길이
            cursor += Utils.intToBytes(8, res, cursor);
            //id
            cursor += Utils.longToBytes(order.getId(), res, cursor);

            //학생 id len
            cursor += Utils.intToBytes(order.getMenuName().length(), res, cursor);
            //id
            cursor = Utils.stringToBytes(order.getSchoolId(), res, cursor);

            //메뉴 이름
            cursor += Utils.intToBytes(order.getMenuName().length(), res, cursor);
            cursor = Utils.stringToBytes(order.getMenuName(), res, cursor);

            //결제 가격
            cursor += Utils.intToBytes(4, res, cursor);
            cursor += Utils.intToBytes(order.getPrice(), res, cursor);

            //쿠폰 가격
            System.arraycopy(Utils.intToBytes(order.getCouponPrice()), 0, res, cursor, 4);
            cursor += Utils.intToBytes(order.getCouponPrice(), res, cursor);

            //구매 유형
            cursor += Utils.intToBytes(order.getPurchaseType().toString().length(), res, cursor);
            cursor = Utils.stringToBytes(order.getPurchaseType().toString(), res, cursor);

            //상태
            cursor += Utils.intToBytes(order.getStatus().toString().length(), res, cursor);
            cursor = Utils.stringToBytes(order.getStatus().toString(), res, cursor);

            //결제 시간
            cursor += Utils.intToBytes(order.getCreatedAt().toString().length(), res, cursor);
            cursor = Utils.stringToBytes(order.getCreatedAt().toString(), res, cursor);
        }

        return res;
    }
}
