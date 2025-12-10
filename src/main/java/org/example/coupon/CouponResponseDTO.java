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
        // 1. Body 사이즈 계산
        int bodySize = 4; // 쿠폰 개수(int)
        for (CouponDetail coupon : coupons) {
            bodySize += 8; // id(long)
            bodySize += 4; // price(int)
            bodySize += 4; // quantity(int)
            bodySize += Utils.getStrSize(coupon.getMenuName());
        }

        // 2. 전체 패킷 사이즈 계산 및 버퍼 할당
        int totalSize = 1 + 1 + 4 + bodySize;
        byte[] res = new byte[totalSize];
        int offset = 0;

        // 3. Header 채우기
        res[offset++] = (byte) resType.getValue();
        res[offset++] = (byte) 0x42; // 쿠폰 응답 코드
        offset = Utils.intToBytes(bodySize, res, offset);

        // 4. Body 채우기
        offset = Utils.intToBytes(coupons.size(), res, offset);
        for (CouponDetail coupon : coupons) {
            offset = Utils.longToBytes(coupon.getId(), res, offset);
            offset = Utils.intToBytes(coupon.getPrice(), res, offset);
            offset = Utils.intToBytes(coupon.getQuantity(), res, offset);
            offset = Utils.stringToBytes(coupon.getMenuName(), res, offset);
        }

        return res;
    }
}
