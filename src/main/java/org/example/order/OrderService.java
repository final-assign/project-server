package org.example.order;

import lombok.RequiredArgsConstructor;
import org.example.db.PooledDataSource;
import org.example.general.ApplicationContext;
import org.example.general.Session;
import org.example.menu.Menu;
import org.example.menu.MenuDAO;
import org.example.user.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class OrderService {

    private final MenuDAO menuDAO;
    private final OrderDAO orderDAO;
    private final Session session = ApplicationContext.session;
    private final DataSource ds = PooledDataSource.getDataSource();

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
                    .createdAt(String.valueOf(LocalDateTime.now()))
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
}
