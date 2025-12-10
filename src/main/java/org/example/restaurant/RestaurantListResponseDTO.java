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
        byte[] data = getBytes();
        int offset = 0;

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

    private byte[] getBytes() {
        int totalSize = 0;
        totalSize += 4;

        for (Restaurant r : list) {
            totalSize += 8;
            totalSize += Utils.getStrSize(r.getName().name());
            totalSize += Utils.getStrSize(r.getDescription());

            totalSize += 4;

            for (RestaurantOperatingInfo info : r.getOperatingInfos()) {
                totalSize += 8;
                totalSize += 8;
                totalSize += Utils.getStrSize(info.getStartAt().toString());
                totalSize += Utils.getStrSize(info.getEndAt().toString());

                totalSize += 8;
                totalSize += Utils.getStrSize(info.getMenuType().getName().name());
            }
        }

        byte[] data = new byte[totalSize];
        return data;
    }
}