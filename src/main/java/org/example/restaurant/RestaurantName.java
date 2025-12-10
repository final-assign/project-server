package org.example.restaurant;

import lombok.Getter;

@Getter
public enum RestaurantName {
    STUDENT_CAFETERIA("학생식당"), STAFF_CAFETERIA("교직원식당"), SNACK_CAFETERIA("분식당");

    String value;

    RestaurantName(String val){
        value = val;
    }
}
