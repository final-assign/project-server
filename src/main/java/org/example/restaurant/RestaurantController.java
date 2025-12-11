package org.example.restaurant;

import lombok.RequiredArgsConstructor;
import org.example.general.ErrorResponseDTO;
import org.example.general.ResponseCode;
import org.example.general.ResponseDTO;
import org.example.user.UserService;
import org.example.user.UserType;

@RequiredArgsConstructor
public class RestaurantController {

    final private RestaurantDAO restaurantDAO;
    final private UserService userService;

    public ResponseDTO getRestaurantListAll(Long userId){

        if(userService.getUserType(userId) != UserType.ADMIN)
            return ErrorResponseDTO.builder()
                    .code(ResponseCode.FORBIDDEN).build();

        return RestaurantListResponseDTO.builder()
                .list(restaurantDAO.findAll()).build();
    }

    public AvailableRestaurantResponseDTO getAvailableRestaurant(Long id){

        return AvailableRestaurantResponseDTO.builder().list(restaurantDAO.findAvailableRestaurant(id)).build();
    }
}
