package org.example.general;

import lombok.Getter;
import org.example.coupon.CouponController;
import org.example.coupon.CouponDAO;
import org.example.coupon.CouponService;
import org.example.menu.*;
import org.example.order.OrderController;
import org.example.order.OrderDAO;
import org.example.order.OrderService;
import org.example.order.order_request.OrderDetailDAO;
import org.example.restaurant.RestaurantController;
import org.example.restaurant.RestaurantDAO;
import org.example.storage.StorageController;
import org.example.storage.StorageService;
import org.example.user.UserController;
import org.example.user.UserDAO;
import org.example.user.UserService;

public class ApplicationContext {

    public static final UserController userController;
    public static final UserService userService;
    public static final Session session = new Session();
    private static final MenuDAO menuDAO;
    private static final MenuService menuService;
    private static final RestaurantDAO restaurantDAO;
    private static final StorageDAO storageDAO;
    private static final StorageService storageService;
    private static final DailyMenuDAO dailyMenuDAO;

    @Getter
    private static final StorageController storageController;
    @Getter
    private static final MenuController menuController;
    @Getter
    private static final RestaurantController restaurantController;
    @Getter
    private static final CouponController couponController;
    @Getter
    private static final OrderController orderController;

    static {

        UserDAO dao = new UserDAO();
        userService = new UserService(dao);
        userController = new UserController(userService);
        storageDAO = new StorageDAO();
        menuDAO = new MenuDAO();
        restaurantDAO = new RestaurantDAO();
        dailyMenuDAO = new DailyMenuDAO();
        menuService = new MenuService(dao, menuDAO, storageDAO, restaurantDAO, dailyMenuDAO);
        menuController = new MenuController(menuService, menuDAO, restaurantDAO);
        restaurantController = new RestaurantController(restaurantDAO, userService);

        CouponDAO couponDAO = new CouponDAO();
        CouponService couponService = new CouponService(menuDAO, couponDAO, dao);
        couponController = new CouponController(couponService);

        // Order-related instances
        OrderDAO orderDAO = new OrderDAO();
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        OrderService orderService = new OrderService(orderDetailDAO,menuDAO, orderDAO, dao, couponDAO);
        orderController = new OrderController(orderService);
        storageService = new StorageService(storageDAO);
        storageController = new StorageController(storageService);
    }


}
