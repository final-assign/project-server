package org.example.restaurant;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOperatingInfo {

    private Long id;
    private Long restaurantId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
