package org.example.menu;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class DailyMenu {

    private Long dailyMenuId;   // PK (Auto Increment 가정)
    private Long menuId;        // FK (MENU 테이블의 ID)
    private LocalDate servedDate;
    private String mainDish;
    private String subDish;
    private Integer price;
}