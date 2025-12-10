package org.example.menu;

import lombok.Getter;

@Getter
public class MenuBatchRequestDTO {

    private final byte[] csvBytes;

    public MenuBatchRequestDTO(byte[] body){

        csvBytes = body;
    }
}
