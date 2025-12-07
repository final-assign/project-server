package org.example.menu.img_down;

import lombok.Getter;
import org.example.general.Utils;

@Getter
public class ImageRequestDTO {
    private final long menuID;

    public ImageRequestDTO(byte[] body){
        this.menuID = Utils.bytesToLong(body, 0);
    }
}