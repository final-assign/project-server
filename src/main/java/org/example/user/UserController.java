package org.example.user;

import lombok.RequiredArgsConstructor;
import org.example.login.LoginRequestDTO;
import org.example.login.LoginResponseDTO;

import java.util.Optional;

@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){

        return userService.login(loginRequestDTO.getId(), loginRequestDTO.getPw());
    }
}
