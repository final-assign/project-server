package org.example.general;

import org.example.user.UserController;
import org.example.user.UserDAO;
import org.example.user.UserService;

public class ApplicationContext {

    public static final UserController userController;
    public static final UserService userService;
    public static final Session session = new Session();

    static {

        UserDAO dao = new UserDAO();
        userService = new UserService(dao);
        userController = new UserController(userService);
    }
}
