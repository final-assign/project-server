package org.example.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 식당별 메뉴 목록 조회 결과를 담는 응답 데이터 전송 객체 (Response DTO)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuListResponseDTO {

    private Long menuId;
    private String menuName;

    private int standardPrice;
    private Integer studentPrice;
}