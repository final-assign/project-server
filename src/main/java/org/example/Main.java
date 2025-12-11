package org.example;

import org.example.utils.Client;
import test.RestaurantTest;
import test.StorageTest;
import org.example.menu.MenuListResponseDTO;
import java.util.List;
import java.util.Scanner;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        new RestaurantManagerApp().run();
        new Client().run();


        //RestaurantTest.restaurantTest();
        //new StorageTest().run();

    }
}