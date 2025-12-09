package org.example.menu;

import lombok.Getter;

@Getter
public enum MenuTypeName {

    BREAKFAST("아침"), LUNCH("점심"), DINNER("저녁"), ALL("상시"); //ALL은 메뉴 등록에서만 사용 예정

    String value;

    MenuTypeName(String val){

        value = val;
    }
}

