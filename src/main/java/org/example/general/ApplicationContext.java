package org.example.general;

import lombok.Getter;
import org.example.menu.MenuController;
import org.example.menu.MenuDAO;
import org.example.menu.StorageDAO;
import org.example.restaurant.RestaurantController;
import org.example.restaurant.RestaurantDAO;
import org.example.user.UserController;
import org.example.user.UserDAO;
import org.example.user.UserService;

public class ApplicationContext {

    public static final UserController userController;
    public static final UserService userService;
    public static final Session session = new Session();
    private static final MenuDAO menuDAO;
    private static final RestaurantDAO restaurantDAO;
    @Getter
    private static final MenuController menuController;
    @Getter
    private static final StorageDAO storageDAO;
    @Getter
    private static final RestaurantController restaurantController;

    static {

        storageDAO = new StorageDAO();
        UserDAO dao = new UserDAO();
        userService = new UserService(dao);
        userController = new UserController(userService);
        menuDAO = new MenuDAO();
        restaurantDAO = new RestaurantDAO();
        menuController = new MenuController(menuDAO, restaurantDAO);
        restaurantController = new RestaurantController(restaurantDAO, userService);
    }
}
