package org.example.menu;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuType {
    private Long id;
    private MenuTypeName name;
}
