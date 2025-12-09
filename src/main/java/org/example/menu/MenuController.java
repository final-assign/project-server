package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.general.Pair;
import org.example.restaurant.Restaurant;
import org.example.restaurant.RestaurantDAO;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MenuController {

    final private MenuDAO menuDAO;
    final private RestaurantDAO restaurantDAO;

//    public MenuRegisterResponseDTO registerMenu(MenuRegisterRequestDTO req){
//
//    }

    public MenuBatchResponseDTO registerMenuByCSV(MenuBatchRequestDTO req) {

        List<Restaurant> restaurantList = restaurantDAO.findAll();
        String[] lines = new String(req.getCsvBytes(), StandardCharsets.UTF_8).split("\n");

        ArrayList<Pair<Long, Pair<Long, String>>> successList = new ArrayList<>();

        for (int i = 1; i < lines.length; ++i) {
            String line = lines[i].strip();
            if (line.isEmpty()) continue;

            try {
                String[] parsedLine = line.split(",");
                if (parsedLine.length < 8) continue;

                String targetRestName = parsedLine[0].strip();
                Restaurant foundRestaurant = null;

                for (Restaurant r : restaurantList) {
                    if (r.getName().getValue().equals(targetRestName)) {
                        foundRestaurant = r;
                        break;
                    }
                }

                if (foundRestaurant == null) continue;

                String menuName = parsedLine[1].strip();
                int standardPrice = Integer.parseInt(parsedLine[2].strip());
                int studentPrice = Integer.parseInt(parsedLine[3].strip());
                int amount = Integer.parseInt(parsedLine[4].strip());
                String photoUrl = parsedLine[7].strip();

                LocalDate datePart = LocalDate.parse(parsedLine[5].strip());
                LocalDateTime saleDateTime = datePart.atStartOfDay();

                String timeStr = parsedLine[6].strip();
                MenuTypeName menuTypeNameEnum = null;

                for (MenuTypeName type : MenuTypeName.values()) {
                    if (type.getValue().equals(timeStr)) {
                        menuTypeNameEnum = type;
                        break;
                    }
                }
                if (menuTypeNameEnum == null) continue;

                Menu menu = new Menu();
                menu.setRestaurantId(foundRestaurant.getId());
                menu.setMenuName(menuName);
                menu.setStandardPrice(standardPrice);
                menu.setStudentPrice(studentPrice);
                menu.setEmployeePrice(standardPrice);
                menu.setAmount(amount);

                Long savedMenuId = menuDAO.insert(menu);
                if (savedMenuId == null) continue;

                Long menuTypeId = menuDAO.findMenuTypeIdByName(menuTypeNameEnum);

                if (menuTypeId == null) {
                    continue;
                }

                MenuAvailability availability = new MenuAvailability();
                availability.setMenuId(savedMenuId);
                availability.setMenuTypeId(menuTypeId);
                availability.setSalesAt(saleDateTime);

                menuDAO.insertAvailability(availability);

                successList.add(new Pair<>((long)(i + 1), new Pair<>(savedMenuId, photoUrl)));

            } catch (Exception e) {
                continue;
            }
        }

        return MenuBatchResponseDTO.builder()
                //.successList(successList)
                .build();
    }
}
