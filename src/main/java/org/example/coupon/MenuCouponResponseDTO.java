package org.example.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.general.ResponseCode;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType;
import org.example.general.Utils;

@Builder
@AllArgsConstructor
public class MenuCouponResponseDTO implements ResponseDTO {

    private final Long couponId;
    private final int count;

    @Override
    public byte[] toBytes() {

        byte[] res = new byte[6 + 12];
        res[0] = ResponseType.RESPONSE.getValue();
        res[1] = ResponseCode.COUPON_BY_MENU.getValue();
        Utils.intToBytes(12, res, 2);
        Utils.longToBytes(couponId, res,6);
        Utils.intToBytes(count, res, 14);

        return res;
    }
}
