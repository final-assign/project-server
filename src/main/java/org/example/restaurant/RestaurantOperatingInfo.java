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

    public RestaurantOperatingInfo(Long id, Long restaurantId, String openTime, String closeTime) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.startAt = LocalDateTime.parse(openTime);
        this.endAt = LocalDateTime.parse(closeTime);
    }

    public String getStartAtStr() {
        return startAt != null ? startAt.toString() : null;
    }

    public String getEndAtStr() {
        return endAt != null ? endAt.toString() : null;
    }
}
