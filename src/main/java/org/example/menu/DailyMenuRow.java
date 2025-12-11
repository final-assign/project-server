package org.example.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyMenuRow {
    private long id;
    private long menuId;
    private String mainDish;
    private String subDish;
    private int standardPrice;
    private int studentPrice;
}