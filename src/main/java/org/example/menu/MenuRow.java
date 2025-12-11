package org.example.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuRow {
    private long id;
    private long restaurantId;
    private String menuName;
    private int standardPrice;
    private int studentPrice;
    private int amount;
    private int isDailyMenu;
}