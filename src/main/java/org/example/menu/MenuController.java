package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.menu.img_down.ImageRequestDTO;
import org.example.menu.img_down.ImageResponseDTO;
import org.example.menu.img_down.StorageDAO;

//이미지 다운용 야매 구현
@RequiredArgsConstructor
public class MenuController {
    private final StorageDAO storageDAO;

    public ImageResponseDTO getImage(ImageRequestDTO imageRequestDTO) {
        byte[] image = storageDAO.findImageByMenuID(imageRequestDTO.getMenuID());

        return ImageResponseDTO.builder().imageBytes(image).build();
    }
}
