package org.example.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {
    private long id;
    private String menuName;
    private int price;
    private int amount;
    private int isDailyMenu;
}