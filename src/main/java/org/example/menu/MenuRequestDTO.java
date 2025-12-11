package org.example.menu;

import lombok.Getter;
import org.example.general.Utils;

@Getter
public class MenuRequestDTO {
    private final long restaurantId;

    public MenuRequestDTO(byte[] data){
        int cursor = 0;
        this.restaurantId = Utils.bytesToLong(data, cursor);
    }
}
