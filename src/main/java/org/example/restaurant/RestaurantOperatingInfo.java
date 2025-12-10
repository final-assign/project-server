package org.example.restaurant;

import lombok.*;
import org.example.menu.MenuType;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOperatingInfo {

    private Long id;
    private Long restaurantId;
    private LocalTime startAt;
    private LocalTime endAt;
    private MenuType menuType;
}
