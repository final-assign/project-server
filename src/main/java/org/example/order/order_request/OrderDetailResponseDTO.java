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
        // ---------------------------------------------------------
        // 1. Body Size 계산
        // ---------------------------------------------------------
        int bodySize = 0;
        bodySize += 4; // 주문 개수(Count)용

        for (OrderDetail order : orders) {
            // (1) 고정 길이 숫자 필드들
            // ID(12) + Price(8) + CouponPrice(8) = 28 bytes
            bodySize += 28;

            // (2) 문자열 필드들 (OuterLength + InnerLength + Data)
            // Utils.getStrSize()는 (4 + byteLength)를 반환한다고 가정 시,
            // 앞에 OuterLength(4)를 더해줘야 함.

            // [수정 1] SchoolId 제거 -> RestaurantName 추가 (쓰기 로직과 일치시킴)
            bodySize += 4 + Utils.getStrSize(order.getMenuName());
            bodySize += 4 + Utils.getStrSize(order.getRestaurantName());
            bodySize += 4 + Utils.getStrSize(order.getPurchaseType().toString());
            bodySize += 4 + Utils.getStrSize(order.getStatus().toString());
            bodySize += 4 + Utils.getStrSize(order.getCreatedAt().toString());
        }

        int totalSize = 1 + 1 + 4 + bodySize;
        byte[] res = new byte[totalSize];
        int cursor = 0;

        // Header
        res[cursor++] = (byte) responseType.getValue();
        res[cursor++] = (byte) 0x41; // 주문 내역 코드

        // Body Length
        System.arraycopy(Utils.intToBytes(bodySize), 0, res, cursor, 4);
        cursor += 4;

        // ---------------------------------------------------------
        // 3. 데이터 쓰기
        // ---------------------------------------------------------

        // 주문 개수
        System.arraycopy(Utils.intToBytes(orders.size()), 0, res, cursor, 4);
        cursor += 4;

        for (OrderDetail order : orders) {
            // 1. Order ID (Long) -> [Len 4] + [Val 8]
            System.arraycopy(Utils.intToBytes(8), 0, res, cursor, 4);
            cursor += 4;
            System.arraycopy(Utils.longToBytes(order.getId()), 0, res, cursor, 8);
            cursor += 8;

            // 2. Menu Name (String)
            // [수정 3] .length() 대신 getStrSize 사용 (한글 깨짐 방지 & OuterLen 계산)
            int menuNameSize = Utils.getStrSize(order.getMenuName());
            System.arraycopy(Utils.intToBytes(menuNameSize), 0, res, cursor, 4); // Outer Length
            cursor += 4;
            cursor = Utils.stringToBytes(order.getMenuName(), res, cursor); // Inner Length + Bytes

            // 3. Restaurant Name (String)
            // [수정 2] 복붙 실수 수정 (getMenuName -> getRestaurantName)
            int restNameSize = Utils.getStrSize(order.getRestaurantName());
            System.arraycopy(Utils.intToBytes(restNameSize), 0, res, cursor, 4);
            cursor += 4;
            cursor = Utils.stringToBytes(order.getRestaurantName(), res, cursor);

            // 4. Price (Int)
            System.arraycopy(Utils.intToBytes(4), 0, res, cursor, 4);
            cursor += 4;
            System.arraycopy(Utils.intToBytes(order.getPrice()), 0, res, cursor, 4);
            cursor += 4;

            // 5. Coupon Price (Int)
            System.arraycopy(Utils.intToBytes(4), 0, res, cursor, 4);
            cursor += 4;
            System.arraycopy(Utils.intToBytes(order.getCouponPrice()), 0, res, cursor, 4);
            cursor += 4;

            // 6. Purchase Type (String)
            String pTypeStr = order.getPurchaseType().toString();
            System.arraycopy(Utils.intToBytes(Utils.getStrSize(pTypeStr)), 0, res, cursor, 4);
            cursor += 4;
            cursor = Utils.stringToBytes(pTypeStr, res, cursor);

            // 7. Status (String)
            String statusStr = order.getStatus().toString();
            System.arraycopy(Utils.intToBytes(Utils.getStrSize(statusStr)), 0, res, cursor, 4);
            cursor += 4;
            cursor = Utils.stringToBytes(statusStr, res, cursor);

            // 8. Created At (String)
            String dateStr = order.getCreatedAt().toString();
            System.arraycopy(Utils.intToBytes(Utils.getStrSize(dateStr)), 0, res, cursor, 4);
            cursor += 4;
            cursor = Utils.stringToBytes(dateStr, res, cursor);
        }

        return res;
    }
}