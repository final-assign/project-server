package org.example.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.general.Utils;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImgDownReqDTO {

    private long menuId;

    public ImgDownReqDTO(byte[] body) {

        this.menuId = Utils.bytesToLong(body, 0);
    }
}
