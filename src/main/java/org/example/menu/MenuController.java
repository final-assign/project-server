package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.menu.storage.ImageRequestDTO;
import org.example.menu.storage.ImageResponseDTO;
import org.example.general.Pair;
import org.example.restaurant.Restaurant;
import org.example.restaurant.RestaurantDAO;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    final private MenuDAO menuDAO;
    final private MenuService menuService;
    final private RestaurantDAO restaurantDAO;

    public ImageResponseDTO getImage(ImageRequestDTO requestDTO) {
        return menuService.findImage(requestDTO.getMenuId());
    }


    public MenuRegisterResponseDTO registerMenu(MenuRegisterRequestDTO req) {
        return menuService.registerMenu(req);
    }
}
