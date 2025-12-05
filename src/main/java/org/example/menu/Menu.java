package org.example.menu;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    private Long id;
    private Long restaurantId;
    private String menuName;
    private int standardPrice;
    private Integer studentPrice;
    private Integer employeePrice;
    private int amount;
}