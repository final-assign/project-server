package org.example.general;

import org.example.coupon.CouponController;
import org.example.coupon.CouponDAO;
import org.example.coupon.CouponService;
import org.example.menu.MenuController;
import org.example.menu.MenuService;
import org.example.image.StorageDAO;
import org.example.order.OrderController;
import org.example.order.OrderService;
import org.example.order.order_request.OrderDetailDAO;
import org.example.user.UserController;
import org.example.user.UserDAO;
import org.example.user.UserService;

public class ApplicationContext {

    public static final UserController userController;
    public static final UserService userService;
    public static final Session session = new Session();
    public static final MenuService menuService;
    public static final MenuController menuController;
    public static final OrderService orderService;
    public static final OrderController orderController;
    public static final CouponService couponService;
    public static final CouponController couponController;
    public static final StorageDAO storageDAO;

    static {

        UserDAO dao = new UserDAO();
        userService = new UserService(dao);
        userController = new UserController(userService);

        storageDAO = new StorageDAO();
        menuService = new MenuService(storageDAO);
        menuController = new MenuController(menuService);

        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        orderService = new OrderService(orderDetailDAO);
        orderController = new OrderController(orderService);

        CouponDAO couponDAO = new CouponDAO();
        couponService = new CouponService(couponDAO);
        couponController = new CouponController(couponService);
    }
}
