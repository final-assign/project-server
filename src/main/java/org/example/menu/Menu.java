package org.example.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private int amount;
    private LocalDate startSalesAt;
    private LocalDate endSalesAt;
    private boolean isDailyMenu;
}
