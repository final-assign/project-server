package org.example.coupon;

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
public class CouponResponseDTO implements ResponseDTO {
    private final List<CouponDetail> coupons;
    private final ResponseType resType;

    @Override
    public byte[] toBytes() {
        // 1. 바디 크기 계산
        int bodyLength = 4; // 쿠폰 개수(Count)

        for (CouponDetail coupon : coupons) {
            // ID(8) + Price(4) + Qty(4) = 16 bytes
            // 각 필드 앞에 길이 정보(4)가 붙으므로: (4+8) + (4+4) + (4+4) = 28 bytes
            bodyLength += 28;

            // 메뉴 이름: OuterLen(4) + InnerLen(4) + StringBytes
            // getStrPacketSize는 (4+Bytes)를 반환하므로 앞에 OuterLen(4) 더함
            bodyLength += 4 + Utils.getStrSize(coupon.getMenuName());
        }

        byte[] res = new byte[1 + 1 + 4 + bodyLength];
        int cursor = 0;

        // 헤더
        res[cursor++] = (byte) resType.getValue();
        res[cursor++] = (byte) 0x42;
        cursor = Utils.intToBytes(bodyLength, res, cursor);

        // 데이터 쓰기

        // 쿠폰 개수
        cursor = Utils.intToBytes(coupons.size(), res, cursor);

        for (CouponDetail coupon : coupons) {
            // 1. ID (Len 4 + Val 8)
            cursor = Utils.intToBytes(8, res, cursor);
            cursor = Utils.longToBytes(coupon.getId(), res, cursor);

            // 2. 가격 (Len 4 + Val 4)
            cursor = Utils.intToBytes(4, res, cursor);
            cursor = Utils.intToBytes(coupon.getPrice(), res, cursor);

            // 3. 수량 (Len 4 + Val 4)
            cursor = Utils.intToBytes(4, res, cursor);
            cursor = Utils.intToBytes(coupon.getQuantity(), res, cursor);

            // 4. 메뉴 이름 (OuterLen 4 + InnerLen 4 + Bytes)
            int namePacketSize = Utils.getStrSize(coupon.getMenuName());
            cursor = Utils.intToBytes(namePacketSize, res, cursor); // Outer Len
            cursor = Utils.stringToBytes(coupon.getMenuName(), res, cursor); // Inner Len + Bytes
        }

        return res;
    }
}