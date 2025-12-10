package org.example.menu;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuType {
    private Long id;
    private MenuTypeName name;
}
