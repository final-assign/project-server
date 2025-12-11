package org.example.coupon;

import lombok.RequiredArgsConstructor;
import org.example.dao.DBConnection;
import org.example.menu.Menu;
import org.example.menu.MenuDAO;
import org.example.user.UserType;

import java.sql.Connection;
import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class CouponService {

    private final MenuDAO menuDAO;
    private final CouponDAO couponDAO;

    /**
     * 메뉴에 대한 쿠폰 2종(STAFF, STUDENT)을 생성합니다.
     * 트랜잭션 안에서 처리되어야 합니다.
     */
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
    // CouponService.java
// ... (생략)

    public boolean reduceCouponStock(int userId, int couponId) {
        String sql = "UPDATE coupon_inventory SET count = count - 1 WHERE user_id = ? AND coupon_id = ? AND count > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, userId);
            pst.setInt(2, couponId);

            int affected = pst.executeUpdate();

            // 쿼리 실행 성공 시, 업데이트된 행이 1개 이상이면 true 반환
            return affected > 0;

        } catch (Exception e) {
            // DB 연결 또는 쿼리 실행 실패 시, 예외를 런타임 예외로 감싸서 다시 던짐 (throw)
            throw new RuntimeException("쿠폰 차감 실패.", e);
        }

    }
    }
