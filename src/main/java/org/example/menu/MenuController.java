package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.storage.ImageRequestDTO;
import org.example.storage.ImageResponseDTO;
import org.example.restaurant.RestaurantDAO;
import org.example.storage.ImgDownReqDTO;


@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    final private MenuDAO menuDAO;
    final private RestaurantDAO restaurantDAO;

    public ImageResponseDTO getImage(ImgDownReqDTO requestDTO) {
        return menuService.findImage(requestDTO.getMenuId());
    }


    public MenuRegisterResponseDTO registerMenu(MenuRegisterRequestDTO req) {

        return menuService.registerMenu(req);
    }

    public MenuListResponseDTO getMenuList(MenuListRequestDTO requestDTO){

        System.out.println(">> [Controller] 메뉴 목록 조회 요청: " + requestDTO.getRestaurantName());

        return menuService.getCouponsTargetMenuList(requestDTO);
    }

    public UserMenuListDTO getRestMenu(Long userId, Long rest){

        return UserMenuListDTO.builder().list(menuService.getRestaurantMenu(userId, rest)).build();
    }
}
