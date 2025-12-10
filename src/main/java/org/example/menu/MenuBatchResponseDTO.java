package org.example.menu;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.general.Pair;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Builder
@RequiredArgsConstructor
public class MenuBatchResponseDTO implements ResponseDTO {

    private final ResponseType responseType;
    private final ArrayList<Pair<Long, Pair<Long, String>>> list;

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }
}
