package org.example;

import test.RestaurantTest;
import test.StorageTest;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        new RestaurantManagerApp().run();

        //RestaurantTest.restaurantTest();
        //new StorageTest().run();
    }
}