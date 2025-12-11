package org.example.order;

import lombok.RequiredArgsConstructor;
import org.example.coupon.Coupon;
import org.example.coupon.CouponDAO;
import org.example.db.PooledDataSource;
import org.example.general.*;
import org.example.menu.DailyMenuRow;
import org.example.menu.Menu;
import org.example.menu.MenuDAO;
import org.example.order.order_request.OrderDetailAdminResponseDTO;
import org.example.order.order_request.OrderDetailDAO;
import org.example.order.order_request.OrderDetail;
import org.example.order.order_request.OrderDetailResponseDTO;
import org.example.user.User;
import org.example.user.UserDAO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class OrderService {
    private final OrderDetailDAO orderDetailDAO;
    private final MenuDAO menuDAO;
    private final OrderDAO orderDAO;
    private final UserDAO userDAO;
    private final CouponDAO couponDAO;
    private final Session session = ApplicationContext.session;
    private final DataSource ds = PooledDataSource.getDataSource();

    public OrderDetailResponseDTO getOrder(Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        List<OrderDetail> orders = orderDetailDAO.findByUserId(userId, startAt, endAt);

        if (orders.isEmpty()) {
            return OrderDetailResponseDTO.builder()
                    .responseType(ResponseType.RESPONSE)
                    .orders(Collections.emptyList())
                    .build();
        }

        return OrderDetailResponseDTO.builder()
                .responseType(ResponseType.RESPONSE)
                .orders(orders)
                .build();
    }

    public OrderDetailAdminResponseDTO getOrderHistory(Long restaurantId, LocalDateTime startAt, LocalDateTime endAt) {
        List<OrderDetail> orders = orderDetailDAO.findByRestaurantAndTime(restaurantId, startAt, endAt);

        //결과가 없으면 빈 리스트
        if (orders.isEmpty()) {
            return OrderDetailAdminResponseDTO.builder()
                    .responseType(ResponseType.RESPONSE)
                    .orders(Collections.emptyList())
                    .build();
        }

        return OrderDetailAdminResponseDTO.builder()
                .responseType(ResponseType.RESPONSE)
                .orders(orders)
                .build();
    }

    public void createOrder(OrderRequestDTO requestDTO, Long userId) {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);

            Menu menu = menuDAO.findById(requestDTO.getMenuId(), conn);
            if (menu == null) {
                throw new RuntimeException("메뉴를 찾을 수 없습니다.");
            }

            int soldAmount = orderDAO.countTodaysOrdersByMenuId(requestDTO.getMenuId(), conn);
            if (menu.getAmount() <= soldAmount) {
                throw new RuntimeException("금일 재고가 모두 소진되었습니다.");
            }


            // TODO: 가격 검증 및 쿠폰 적용 로직
            // int finalPrice = ...;
            // if (finalPrice != requestDTO.getCharge()) {
            //     throw new RuntimeException("결제 금액이 일치하지 않습니다.");
            // }

            Order newOrder = Order.builder()
                    .menuId(requestDTO.getMenuId())
                    .couponId(requestDTO.getCouponId() == 0 ? null : requestDTO.getCouponId())
                    .userId(userId)
                    .status(OrderStatus.COOKING)
                    .createdAt(LocalDateTime.now())
                    .build();

            orderDAO.insert(newOrder, conn);

            conn.commit();

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // 로깅 필요
                }
            }
            throw new RuntimeException("주문 처리 중 오류가 발생했습니다.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // 로깅 필요
                }
            }
        }
    }

    public GeneralResponseDTO createCardOrder(OrderCardRequestDTO requestDTO, Long userId) {

        Connection conn = null;

        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);

            // 1. 유저 조회
            User user = userDAO.findById(userId, conn);
            if (user == null) {
                throw new GeneralException(ResponseCode.ORDER_FAILURE, "유저가 존재하지 않습니다.");
            }
            String userType = String.valueOf(user.getType());      // STUDENT / STAFF

            int userBalance = user.getBalance();

            // 2. 메뉴 조회
            Menu menu = menuDAO.findById(requestDTO.getMenuId(), conn);

            if (menu == null) {
                throw new GeneralException(ResponseCode.ORDER_FAILURE, "메뉴를 찾을 수 없습니다.");
            }

            // 3. 오늘 재고 확인
            int soldAmount = orderDAO.countTodaysOrdersByMenuId(requestDTO.getMenuId(), conn);

            if (menu.getAmount() <= soldAmount) {
                throw new GeneralException(ResponseCode.ORDER_FAILURE, "금일 재고가 소진되었습니다.");
            }

            // 4. 데일리 메뉴 가격 우선 적용
            System.out.println("메뉴 ID = " + menu.getId());  // 1
            DailyMenuRow daily = menuDAO.getTodayDailyMenu(conn, menu.getId());
            System.out.println("daily 객체 = " + daily);      // 2
            System.out.println("----- 이거 출력되면 됨 -----"); // 3
            System.out.println("출력안됨");
            int menuPrice;

            if (daily != null) {

                // 데일리 메뉴 가격 적용
                menuPrice = userType.equals("STUDENT")
                        ? daily.getStudentPrice()
                        : daily.getStandardPrice();
            } else {

                menuPrice = userType.equals("STUDENT")
                        ? menu.getStudentPrice()
                        : menu.getStandardPrice();
            }

            // 5. 유저 잔액 체크

            if (userBalance < menuPrice) {
                throw new GeneralException(ResponseCode.ORDER_FAILURE, "잔액이 부족합니다.");
            }

            // 6. 잔액 차감
            int newBalance = userBalance - menuPrice;
            userDAO.updateBalance(userId, newBalance, conn);

            // 7. 주문 생성
            Order newOrder = Order.builder()
                    .menuId(requestDTO.getMenuId())
                    .couponId(null)
                    .userId(userId)
                    .price(menuPrice) // 가격 저장해두는 게 맞음
                    .status(OrderStatus.COOKING)
                    .createdAt(LocalDateTime.now())
                    .build();

            orderDAO.insert(newOrder, conn);

            // 8. commit
            conn.commit();

        } catch (Exception e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            System.out.println(e.getMessage());
            throw new GeneralException(ResponseCode.ORDER_FAILURE, "잔액이 부족합니다.");

        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {}
            }
        }

        return GeneralResponseDTO.builder()
                .code(ResponseCode.ORDER_SUCCESS)
                .build();
    }

    public GeneralResponseDTO createCouponOrder(long menuId, long userId) {

        Connection conn = null;

        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);

            // 1. USER 조회
            User user = userDAO.findById(userId, conn);
            if (user == null) {
                throw new GeneralException(ResponseCode.ORDER_FAILURE, "유저가 존재하지 않습니다.");
            }
            String userType = String.valueOf(user.getType());

            // 2. 메뉴 조회
            Menu menu = menuDAO.findById(menuId, conn);
            if (menu == null) {
                throw new GeneralException(ResponseCode.ORDER_FAILURE, "메뉴를 찾을 수 없습니다.");
            }

            // 3. 재고 확인
            int soldAmount = orderDAO.countTodaysOrdersByMenuId(menuId, conn);
            if (menu.getAmount() <= soldAmount) {
                throw new GeneralException(ResponseCode.ORDER_FAILURE, "금일 재고가 소진되었습니다.");
            }

            // ---------------------------------------------------
            //         새로운 쿠폰 결제 로직 (쿠폰 없으면 주문 불가)
            // ---------------------------------------------------

            // 4. menu_id 로 쿠폰 조회
            List<Coupon> coupons = couponDAO.findByMenuId(conn, menuId);
            if (coupons.isEmpty()) {
                throw new GeneralException(ResponseCode.ORDER_FAILURE, "해당 메뉴는 쿠폰 결제가 필요합니다.");
            }

            // 5. 타입 일치 쿠폰 찾기
            Coupon matched = null;
            for (Coupon c : coupons) {
                if (c.getUserType().toString().equals(userType)) {
                    matched = c;
                    break;
                }
            }

            if (matched == null) {
                throw new GeneralException(ResponseCode.ORDER_FAILURE, "이 메뉴는 귀하의 쿠폰으로 결제가 불가능합니다.");
            }

            long couponId = matched.getId();

            // 6. 유저 쿠폰 보유 여부 확인
            int count = couponDAO.getUserCouponQuantity(conn, couponId, userId);
            if (count <= 0) {
                throw new GeneralException(ResponseCode.ORDER_FAILURE, "사용할 수 있는 쿠폰이 없습니다.");
            }

            // 7. 쿠폰 1장 차감
            couponDAO.decreaseUserCoupon(conn, couponId, userId);

            // 8. 주문 생성 (가격은 0원)
            Order newOrder = Order.builder()
                    .menuId(menuId)
                    .couponId(couponId)
                    .userId(userId)
                    .price(0)
                    .status(OrderStatus.COOKING)
                    .createdAt(LocalDateTime.now())
                    .build();

            orderDAO.insert(newOrder, conn);

            conn.commit();

        } catch (Exception e) {

            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            System.out.println(e.getMessage());
            throw new GeneralException(ResponseCode.ORDER_FAILURE, e.getMessage());

        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {}
            }
        }

        return GeneralResponseDTO.builder()
                .code(ResponseCode.ORDER_SUCCESS)
                .build();
    }

    private int calculateMenuPrice(String userType, Menu menu, Connection conn) throws SQLException {

        DailyMenuRow daily = menuDAO.getTodayDailyMenu(conn, menu.getId());

        if (daily != null) {
            return userType.equals("STUDENT")
                    ? daily.getStudentPrice()
                    : daily.getStandardPrice();
        }

        return userType.equals("STUDENT")
                ? menu.getStudentPrice()
                : menu.getStandardPrice();
    }
}
