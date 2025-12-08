package org.example.menu.img_down;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Storage {
    private Long id;
    private Long menuId;
    private byte[] fileData;
}
