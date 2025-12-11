package org.example.storage;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.menu.StorageDAO;

@RequiredArgsConstructor
public class StorageController {

    final private StorageService storageService;

    public ResponseDTO insertImage(ImageRequestDTO dto){

        return storageService.insertImage(dto);
    }
}
