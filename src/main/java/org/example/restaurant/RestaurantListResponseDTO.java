package org.example.restaurant;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;

import java.util.ArrayList;

@Builder
@RequiredArgsConstructor
public class RestaurantListResponseDTO implements ResponseDTO {

    private final ArrayList<Restaurant> list;

    @Override
    public byte[] toBytes() {

        int restSize =
        return new byte[0];
    }
}
