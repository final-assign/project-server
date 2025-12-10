package org.example.coupon;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseType;
import org.example.menu.Menu;
import org.example.menu.MenuDAO;
import org.example.user.UserType;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class CouponService {

    private final MenuDAO menuDAO;
    private final CouponDAO couponDAO;

    public CouponResponseDTO getAllCoupons(long userId) {
        List<CouponDetail> coupons = couponDAO.findByUserId(userId);
        if (coupons.isEmpty()) {
            return CouponResponseDTO.builder()
                    .resType(ResponseType.RESPONSE)
                    .coupons(Collections.emptyList())
                    .build();
        } else {
            return CouponResponseDTO.builder()
                    .resType(ResponseType.RESPONSE)
                    .coupons(coupons)
                    .build();
        }
    }

    public void createCouponsForMenu(CouponCreateRequestDTO requestDTO) {
        // 1. DTO에서 정보 추출
        Long restId = requestDTO.getRestId();
        Long menuId = requestDTO.getMenuId();

        // 2. MenuDAO를 통해 메뉴 정보(가격 포함) 조회
        //    - 이 부분은 MenuDAO의 실제 구현에 따라 변경될 수 있습니다.
        //    - Menu 엔티티나 가격 정보만 담은 DTO를 반환한다고 가정합니다.
        Menu menu = menuDAO.findById(menuId);
        if (menu == null) {
            // 적절한 예외 처리
            throw new RuntimeException("메뉴 정보를 찾을 수 없습니다: " + menuId);
        }

        // 3. 쿠폰 생성 (STAFF, STUDENT)
        Coupon staffCoupon = Coupon.builder()
                .restId(restId)
                .menuId(menuId)
                .userType(UserType.STAFF)
                .couponPrice(menu.getStandardPrice())
                .build();

        Coupon studentCoupon = Coupon.builder()
                .restId(restId)
                .menuId(menuId)
                .userType(UserType.STUDENT)
                .couponPrice(menu.getStudentPrice())
                .build();

        // 4. CouponDAO를 통해 쿠폰 저장
        //    - 트랜잭션 처리는 이 메서드를 호출하는 상위 레벨(예: ClientHandler)에서 필요합니다.
        couponDAO.insert(staffCoupon);
        couponDAO.insert(studentCoupon);
    }
}
