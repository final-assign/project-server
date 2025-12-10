package org.example.restaurant;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.general.ResponseDTO;
import org.example.general.Utils;

import java.util.ArrayList;
import java.util.List;

@Builder
@RequiredArgsConstructor
public class RestaurantListResponseDTO implements ResponseDTO {

    private final ArrayList<Restaurant> list;

    @Override
    public byte[] toBytes() {
        // 1. 데이터(Payload) 크기 계산
        int payloadSize = calculatePayloadSize();

        // 2. 전체 배열 생성: 헤더(6바이트) + 데이터 크기
        byte[] data = new byte[6 + payloadSize];
        int offset = 0;

        // 3. 헤더 작성 [0x02] [0x81] 시간나면 refactor
        data[offset++] = 0x02;
        data[offset++] = (byte) 0x81;

        // 4. 데이터 길이 작성 (4바이트)
        offset = Utils.intToBytes(payloadSize, data, offset);

        // 5. 리스트 데이터 직렬화
        offset = Utils.intToBytes(list.size(), data, offset);

        for (Restaurant r : list) {
            offset = Utils.longToBytes(r.getId(), data, offset);
            offset = Utils.stringToBytes(r.getName().name(), data, offset);
            offset = Utils.stringToBytes(r.getDescription(), data, offset);

            List<RestaurantOperatingInfo> infos = r.getOperatingInfos();
            offset = Utils.intToBytes(infos.size(), data, offset);

            for (RestaurantOperatingInfo info : infos) {
                offset = Utils.longToBytes(info.getId(), data, offset);
                offset = Utils.longToBytes(info.getRestaurantId(), data, offset);
                offset = Utils.stringToBytes(info.getStartAt().toString(), data, offset);
                offset = Utils.stringToBytes(info.getEndAt().toString(), data, offset);

                offset = Utils.longToBytes(info.getMenuType().getId(), data, offset);
                offset = Utils.stringToBytes(info.getMenuType().getName().name(), data, offset);
            }
        }

        return data;
    }

    private int calculatePayloadSize() {
        int size = 0;
        size += 4; // list size

        for (Restaurant r : list) {
            size += 8; // id
            size += Utils.getStrSize(r.getName().name());
            size += Utils.getStrSize(r.getDescription());

            size += 4; // operating infos size

            for (RestaurantOperatingInfo info : r.getOperatingInfos()) {
                size += 8; // id
                size += 8; // restaurantId
                size += Utils.getStrSize(info.getStartAt().toString());
                size += Utils.getStrSize(info.getEndAt().toString());

                size += 8; // menuType Id
                size += Utils.getStrSize(info.getMenuType().getName().name());
            }
        }
        return size;
    }
}