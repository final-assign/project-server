package org.example.general;

import org.example.menu.MenuController;
import org.example.menu.MenuService;
import org.example.menu.img_down.StorageDAO;
import org.example.user.UserController;
import org.example.user.UserDAO;
import org.example.user.UserService;

public class ApplicationContext {

    public static final UserController userController;
    public static final UserService userService;
    public static final Session session = new Session();
    public static final StorageDAO storageDAO;
    public static final MenuService menuService;
    public static final MenuController menuController;

    static {

        UserDAO dao = new UserDAO();
        userService = new UserService(dao);
        userController = new UserController(userService);
        storageDAO = new StorageDAO();
        menuService = new MenuService(storageDAO);
        menuController = new MenuController(menuService);
    }
}
