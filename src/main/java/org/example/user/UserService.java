package org.example.user;

import lombok.RequiredArgsConstructor;
import org.example.general.ApplicationContext;
import org.example.general.ResponseType;
import org.example.login.LoginResponseDTO;
import org.example.login.LoginResponseType;

import java.util.Optional;

@RequiredArgsConstructor
public class UserService {

    final private UserDAO userDAO;

    public LoginResponseDTO login(String id, String pw){

         Optional<User> user = userDAO.findByIdAndPassword(id, pw);

         if(user.isEmpty()) return LoginResponseDTO.builder()
                                        .resType(ResponseType.RESPONSE)
                                        .loginResponseType(LoginResponseType.FAILURE).build();

         if(!ApplicationContext.session.addSession(user.get().getId()))
             return LoginResponseDTO.builder()
                     .resType(ResponseType.RESPONSE)
                     .loginResponseType(LoginResponseType.DISCONNECTED).build();

         return LoginResponseDTO.builder()
                        .resType(ResponseType.RESPONSE)
                        .loginResponseType(LoginResponseType.SUCCESS)
                        .userId(user.get().getId())
                        .userType(user.get().getType()).build();
    }

    public UserType getUserType(Long userId){

        return userDAO.findTypeById(userId);
    }
}
