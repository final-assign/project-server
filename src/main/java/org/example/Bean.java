package org.example;

import org.example.user.UserDAO;
import org.example.user.UserService;

public class Bean {

    public static final UserService userService;

    static {

        UserDAO dao = new UserDAO();
        userService = new UserService(dao);

        System.out.println("객체 조립 완료!");
    }
}