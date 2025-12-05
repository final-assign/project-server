package org.example.restaurant;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    private Long id;
    private RestaurantName name;
    private String description;
}
