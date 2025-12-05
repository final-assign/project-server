package org.example.restaurant;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOperatingInfo {
    private Long id;
    private Long restaurantId;
    private String startAt;
    private String endAt;
}
