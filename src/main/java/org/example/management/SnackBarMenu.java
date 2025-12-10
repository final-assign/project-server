package org.example.management;

import java.math.BigDecimal;

public class SnackBarMenu {
    private int menuId;
    private String menuName;
    private BigDecimal price;
    private String description;
    private boolean isAvailable;

    public SnackBarMenu() {}

    public SnackBarMenu(String menuName, BigDecimal price, String description) {
        this.menuName = menuName;
        this.price = price;
        this.description = description;
        this.isAvailable = true;
    }

    public int getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        String status = isAvailable ? "" : " (품절)";
        return String.format("[메뉴 ID: %d] %s - %,d원 (%s)%s",
                menuId, menuName, price.intValue(), description, status);
    }
}
