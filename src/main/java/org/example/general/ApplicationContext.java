package org.example.general;

import org.example.menu.MenuController;
import org.example.menu.MenuService;
import org.example.menu.storage.StorageDAO;
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
    @Getter
    private static final MenuController menuController;
    @Getter
    private static final StorageDAO storageDAO;
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
        menuService = new MenuService(storageDAO);
        menuController = new MenuController(menuService);
        menuDAO = new MenuDAO();
        restaurantDAO = new RestaurantDAO();
        menuService = new MenuService(menuDAO);
        menuController = new MenuController(menuDAO, menuService, restaurantDAO);
        restaurantController = new RestaurantController(restaurantDAO, userService);

        CouponDAO couponDAO = new CouponDAO();
        CouponService couponService = new CouponService(menuDAO, couponDAO);
        couponController = new CouponController(couponService);

        // Order-related instances
        OrderDAO orderDAO = new OrderDAO();
        OrderService orderService = new OrderService(menuDAO, orderDAO);
        orderController = new OrderController(orderService);
    }
}
