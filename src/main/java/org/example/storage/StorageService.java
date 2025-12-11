package org.example.storage;

import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.general.SuccessResponseDTO;
import org.example.menu.StorageDAO;

@RequiredArgsConstructor
public class StorageService {

    private final StorageDAO storageDAO;

    public ResponseDTO insertImage(ImageRequestDTO dto){

        storageDAO.insert(dto.getMenuId(), dto.getImageStream(), dto.getFileLen());

        return new SuccessResponseDTO();
    }
}
