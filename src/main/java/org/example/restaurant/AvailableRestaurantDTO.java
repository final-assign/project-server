package org.example.restaurant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableRestaurantDTO {

    private Long id;
    private String name;
    private String description;
}
