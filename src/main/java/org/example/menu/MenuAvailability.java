package org.example.menu;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuAvailability {
    private Long menuId;
    private Long menuTypeId;
    private LocalDateTime salesAt;
}
