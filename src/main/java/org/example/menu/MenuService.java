package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseType;
import org.example.menu.img_down.ImageResponseDTO;
import org.example.menu.img_down.StorageDAO;

import java.util.Optional;

@RequiredArgsConstructor
public class MenuService {
    private final StorageDAO storageDAO;

    public ImageResponseDTO findImage(long menuId) {
        Optional<Storage> storage = storageDAO.findByMenuID(menuId);

        if(storage.isEmpty()) {
            return ImageResponseDTO.builder()
                    .resType(ResponseType.RESPONSE)
                    .imageData(new byte[0])
                    .build();
        }

        return ImageResponseDTO.builder()
                .resType(ResponseType.RESPONSE)
                .imageData(storage.get().getFileData())
                .build();
    }
}
