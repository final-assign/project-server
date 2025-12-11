package org.example.restaurant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.general.ResponseDTO;
import org.example.general.Utils;

import java.util.List;

@Builder
@AllArgsConstructor
public class AvailableRestaurantResponseDTO implements ResponseDTO {

    private final List<AvailableRestaurantDTO> list;

    @Override
    public byte[] toBytes() {

        // === 1. 바디 길이 계산 ===
        int bodyLength = 0;

        for (AvailableRestaurantDTO dto : list) {
            bodyLength += 8;                        // id (8 bytes)
            bodyLength += Utils.getStrSize(dto.getName());        // name length + name bytes
            bodyLength += Utils.getStrSize(dto.getDescription()); // desc length + desc bytes
        }

        // === 2. 전체 배열 생성 ===
        int totalLength = 6 + bodyLength;
        byte[] result = new byte[totalLength];
        int offset = 0;

        // === 3. 헤더 작성 ===
        result[offset++] = 0x02;   // message type 1
        result[offset++] = 0x14;   // message type 2

        offset = Utils.intToBytes(bodyLength, result, offset); // body length(4)

        // === 4. 바디 작성 ===
        for (AvailableRestaurantDTO dto : list) {
            offset = Utils.longToBytes(dto.getId(), result, offset);

            offset = Utils.stringToBytes(dto.getName(), result, offset);

            offset = Utils.stringToBytes(dto.getDescription(), result, offset);
        }

        return result;
    }

}
