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
        // 1. 전체 Body 사이즈 계산
        int bodySize = 4; // 주문 개수(int)
        for (OrderDetail order : orders) {
            bodySize += 8; // id(long)
            bodySize += Utils.getStrSize(order.getSchoolId());
            bodySize += Utils.getStrSize(order.getMenuName());
            bodySize += Utils.getStrSize(order.getRestaurantName());
            bodySize += 4; // price(int)
            bodySize += 4; // couponPrice(int)
            bodySize += Utils.getStrSize(order.getPurchaseType().toString());
            bodySize += Utils.getStrSize(order.getStatus().toString());
            bodySize += Utils.getStrSize(order.getCreatedAt().toString());
        }

        // 2. 전체 패킷 사이즈 계산 및 버퍼 할당
        int totalSize = 1 + 1 + 4 + bodySize;
        byte[] res = new byte[totalSize];
        int offset = 0;

        // 3. Header 채우기
        res[offset++] = (byte) responseType.getValue();
        res[offset++] = (byte) 0x41; // 주문 내역 전송 코드
        offset = Utils.intToBytes(bodySize, res, offset);

        // 4. Body 채우기
        offset = Utils.intToBytes(orders.size(), res, offset);
        for (OrderDetail order : orders) {
            offset = Utils.longToBytes(order.getId(), res, offset);
            offset = Utils.stringToBytes(order.getSchoolId(), res, offset);
            offset = Utils.stringToBytes(order.getMenuName(), res, offset);
            offset = Utils.stringToBytes(order.getRestaurantName(), res, offset);
            offset = Utils.intToBytes(order.getPrice(), res, offset);
            offset = Utils.intToBytes(order.getCouponPrice(), res, offset);
            offset = Utils.stringToBytes(order.getPurchaseType().toString(), res, offset);
            offset = Utils.stringToBytes(order.getStatus().toString(), res, offset);
            offset = Utils.stringToBytes(order.getCreatedAt().toString(), res, offset);
        }

        return res;
    }
}
