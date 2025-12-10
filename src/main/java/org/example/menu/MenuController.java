package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.image.ImageRequestDTO;
import org.example.image.ImageResponseDTO;

//임시입니다...
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    public ImageResponseDTO getImage(ImageRequestDTO requestDTO) {
        return menuService.findImage(requestDTO.getMenuId());
    }
}
