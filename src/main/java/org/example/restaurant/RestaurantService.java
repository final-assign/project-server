package org.example.restaurant;

import lombok.RequiredArgsConstructor;
import org.example.menu.MenuService;
import org.example.menu.MenuListResponseDTO;
import java.util.List;

@RequiredArgsConstructor //
public class RestaurantService {


    private final RestaurantDAO restaurantDAO;
    private final MenuService menuService;

    public RestaurantDetailDTO getRestaurantDetails(Long restaurantId) {

        Restaurant restaurant = restaurantDAO.findById(restaurantId);

        if (restaurant == null) {

            return null;
        }

        List<MenuListResponseDTO> menus = menuService.getMenusByRestaurant(restaurantId);

        return RestaurantDetailDTO.builder()
                .id(restaurant.getId())
                .name(restaurant.getName().name())
                .description(restaurant.getDescription())
                .operatingInfo(restaurant.getOperatingInfos() != null && !restaurant.getOperatingInfos().isEmpty()
                        ? restaurant.getOperatingInfos().get(0) : null)
                .menus(menus)
                .build();
    }
}