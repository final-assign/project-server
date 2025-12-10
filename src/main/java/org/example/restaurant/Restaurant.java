package org.example.restaurant;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    private Long id;
    private RestaurantName name;
    private String description;
    List<RestaurantOperatingInfo> operatingInfos;
}
