package org.example.general;

import lombok.Getter;
import org.example.menu.MenuController;
import org.example.menu.MenuDAO;
import org.example.user.UserController;
import org.example.user.UserDAO;
import org.example.user.UserService;

@Getter
public class ApplicationContext {

    public static final UserController userController;
    public static final UserService userService;
    public static final Session session = new Session();
    private static final MenuDAO menuDAO;
    private static final MenuController menuController;
    static {

        UserDAO dao = new UserDAO();
        userService = new UserService(dao);
        userController = new UserController(userService);
        menuDAO = new MenuDAO();
        menuController = new MenuController(menuDAO);
    }
}
