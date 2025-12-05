package org.example.menu;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuAvailability {
    private Long menuId;
    private Long menuTypeId;
}
