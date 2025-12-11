package org.example.coupon;

import lombok.RequiredArgsConstructor;
import org.example.db.PooledDataSource;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType;
import org.example.general.SuccessResponseDTO;
import org.example.menu.Menu;
import org.example.menu.MenuDAO;
import org.example.user.UserDAO;
import org.example.user.UserType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class CouponService {

    private final DataSource ds = PooledDataSource.getDataSource();

    private final MenuDAO menuDAO;
    private final CouponDAO couponDAO;
    private final UserDAO userDAO;

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

    public ResponseDTO createCouponsForMenu(CouponCreateRequestDTO requestDTO) {
        // 1. DTO에서 정보 추출
        int amount = requestDTO.getAmount();
        Long menuId = requestDTO.getMenuId();

        // ============================================================
        // CASE A: 금액권 (Menu ID == 0)
        // ============================================================
        if (menuId == 0) {
            Coupon moneyCoupon = Coupon.builder()
                    .menuId(0L)
                    .userType(UserType.ALL) // 금액권은 누구나 사용 (NULL)
                    .couponPrice(amount)
                    .build();

            couponDAO.insert(moneyCoupon); // 커넥션 없이 바로 호출
        }
        // ============================================================
        // CASE B: 메뉴 교환권 (Menu ID != 0)
        // ============================================================
        else {
            // 메뉴 정보 조회
            Menu menu = menuDAO.findById(menuId); // 커넥션 없이 바로 호출

            if (menu == null) {
                throw new RuntimeException("메뉴 정보를 찾을 수 없습니다: " + menuId);
            }

            // 스태프용 쿠폰 생성
            Coupon staffCoupon = Coupon.builder()
                    .menuId(menuId)
                    .userType(UserType.STAFF)
                    .couponPrice(menu.getStandardPrice())
                    .build();

            // 학생용 쿠폰 생성
            Coupon studentCoupon = Coupon.builder()
                    .menuId(menuId)
                    .userType(UserType.STUDENT)
                    .couponPrice(menu.getStudentPrice())
                    .build();

            // 저장
            couponDAO.insert(staffCoupon);
            couponDAO.insert(studentCoupon);
        }

        return new SuccessResponseDTO();
    }

    public MenuCouponResponseDTO getCouponByMenu(MenuCouponRequestDTO req, long userId) {

        try (Connection conn = ds.getConnection()) {

            long menuId = req.getMenuId();

            // 1. USER 타입 조회 (STUDENT / STAFF)
            String userType = userDAO.getUserType(conn, userId);

            // 2. menu_id 기반으로 쿠폰 목록 가져오기
            List<Coupon> coupons = couponDAO.findByMenuId(conn, menuId);
            if (coupons.isEmpty()) {
                return new MenuCouponResponseDTO(0L, 0); // 쿠폰 없음
            }

            // 3. 유저 타입과 일치하는 쿠폰 찾기
            Coupon matched = null;
            for (Coupon c : coupons) {
                if (c.getUserType().toString().equals(userType)) {
                    matched = c;
                    break;
                }
            }

            if (matched == null) {
                return new MenuCouponResponseDTO(0L, 0); // 타입 맞는 쿠폰 없음
            }

            long couponId = matched.getId();

            // 4. COUPON_INVENTORY 에서 유저 보유 수량 조회
            int count = couponDAO.getUserCouponQuantity(conn, couponId, userId);

            return new MenuCouponResponseDTO(couponId, count);

        } catch (SQLException e) {
            System.out.println("[CouponService] SQL Error: " + e.getMessage());
            return new MenuCouponResponseDTO(0L, 0); // 실패하면 그냥 0개
        }
    }
}
