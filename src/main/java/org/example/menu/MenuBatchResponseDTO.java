package org.example.menu;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType;

@Builder
@RequiredArgsConstructor
public class MenuBatchResponseDTO implements ResponseDTO {

    private final ResponseType responseType;
    private final

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }
}
