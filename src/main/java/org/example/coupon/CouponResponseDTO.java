package org.example.coupon;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType;
import org.example.general.Utils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
public class CouponResponseDTO implements ResponseDTO {
    private final List<CouponDetail> coupons;
    private final ResponseType resType;

    @Override
    public byte[] toBytes() {
        // 쿠폰 개수 4바이트
        int bodyLength = 4;
        for (CouponDetail coupon : coupons) {
            // id 8, price 4, quantity 4, menuName 길이 4 + menuName
            bodyLength += 8 + 4 + 4 + 4 + coupon.getMenuName().getBytes(StandardCharsets.UTF_8).length;
        }

        byte[] res = new byte[1 + 1 + 4 + bodyLength];
        int cursor = 0;

        res[cursor++] = (byte) resType.getValue();
        res[cursor++] = (byte) 0x42; // 고정값

        // body 사이즈
        System.arraycopy(Utils.intToBytes(bodyLength), 0, res, cursor, 4);
        cursor += 4;

        // 쿠폰 개수
        System.arraycopy(Utils.intToBytes(coupons.size()), 0, res, cursor, 4);
        cursor += 4;

        for (CouponDetail coupon : coupons) {
            // id 사이즈
            System.arraycopy(Utils.intToBytes(8), 0, res, cursor, 8);
            cursor += 4;
            // id
            System.arraycopy(Utils.longToBytes(coupon.getId()), 0, res, cursor, 8);
            cursor += 8;

            //가격 사이즈
            System.arraycopy(Utils.intToBytes(4), 0, res, cursor, 8);
            cursor += 4;
            // 가격
            System.arraycopy(Utils.intToBytes(coupon.getPrice()), 0, res, cursor, 4);
            cursor += 4;

            //쿠폰 수량 사이즈
            System.arraycopy(Utils.intToBytes(4), 0, res, cursor, 8);
            cursor += 4;
            // 쿠폰 수량
            System.arraycopy(Utils.intToBytes(coupon.getQuantity()), 0, res, cursor, 4);
            cursor += 4;

            //메뉴 이름
            System.arraycopy(Utils.intToBytes(Utils.getByteLength(coupon.getMenuName())), 0, res, cursor, 4);
            cursor = Utils.writeString(res, cursor, coupon.getMenuName());
        }

        return res;
    }
}
