package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.storage.ImageRequestDTO;
import org.example.storage.ImageResponseDTO;
import org.example.restaurant.RestaurantDAO;

import static org.example.general.ApplicationContext.userService;


@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    final private MenuDAO menuDAO;
    final private RestaurantDAO restaurantDAO;

    public ImageResponseDTO getImage(ImageRequestDTO requestDTO) {
        return menuService.findImage(requestDTO.getMenuId());
    }


    public ResponseDTO getMenus(MenuRequestDTO dto, Long userId) {
        return menuService.findByRestaurantId(dto.getRestaurantId(), userService.getUserType(userId));
    }
}
