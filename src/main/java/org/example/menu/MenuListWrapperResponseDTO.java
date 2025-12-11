package org.example.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.general.ResponseDTO;
import org.example.general.ResponseType; // ResponseType import 필요
import org.example.general.Utils; // Utils import 필요
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MenuListWrapperResponseDTO implements ResponseDTO {

    // ResponseDTO 필드 (성공 응답 DTO의 기본 구조를 OrderDetailAdminResponseDTO와 유사하게 맞춤)
    private final ResponseType responseType; // OrderDetailAdminResponseDTO의 패턴을 따름
    private final List<MenuListResponseDTO> menuList;

    // 추가적인 상태 필드 (isSuccess, errorCode 등)는 responseType으로 대체되었다고 가정

    // 헬퍼 메서드: 전체 바디 크기 계산 (헤더 제외)
    private int calculateBodySize() {
        int bodySize = 0;

        // 1. 리스트 계수 크기 (4 bytes)
        bodySize += 4;

        if (menuList != null) {
            for (MenuListResponseDTO menu : menuList) {
                // 고정 길이 필드: 8 + 4 + 4 + 4 = 20
                // menuId (Long: 8) + standardPrice (int: 4) + studentPrice (int: 4) + isAvailable (int: 4)
                bodySize += 16;

                // 가변 길이 문자열 필드 (4 bytes 길이 + N bytes 문자열)
                bodySize += Utils.getStrSize(menu.getMenuName());
            }
        }

        return bodySize;
    }

    @Override
    public byte[] toBytes() {
        int bodySize = calculateBodySize();

        // 1. 헤더 크기 (ResponseType 1 + Command 1 + Body Length 4 = 6) + Body 크기
        int totalSize = 1 + 1 + 4 + bodySize;
        byte[] res = new byte[totalSize];
        int cursor = 0;

        // 2. 헤더 쓰기 (ResponseType + Command + Body Length)

        // 2.1. ResponseType (1 byte)
        res[cursor++] = (byte) responseType.getValue();

        // 2.2. Command Type (1 byte)
        // 주문 목록(0x41)과 구분되는 메뉴 목록 커맨드 (예: 0x82)를 사용한다고 가정
        res[cursor++] = (byte) 0x82;

        // 2.3. Body 길이 (4 byte)
        // Utils.intToBytes(int, byte[], int) 메서드가 offset을 반환하는 패턴을 따릅니다.
        cursor = Utils.intToBytes(bodySize, res, cursor);

        // 3. 바디 쓰기 시작

        // 3.1. 메뉴 계수 (int: 4 byte)
        int listSize = menuList != null ? menuList.size() : 0;
        cursor = Utils.intToBytes(listSize, res, cursor);

        // 3.2. 메뉴 목록 데이터 쓰기 (반복)
        if (menuList != null) {
            for (MenuListResponseDTO menu : menuList) {

                // a. menuId (Long: 8 byte)
                cursor = Utils.longToBytes(menu.getMenuId(), res, cursor);

                // b. menuName (String: 4 byte 길이 + N byte 문자열)
                cursor = Utils.stringToBytes(menu.getMenuName(), res, cursor);

                // c. standardPrice (int: 4 byte)
                cursor = Utils.intToBytes(menu.getStandardPrice(), res, cursor);

                // d. studentPrice (Integer: 4 byte)
                int studentPrice = menu.getStudentPrice() != null ? menu.getStudentPrice() : 0;
                cursor = Utils.intToBytes(studentPrice, res, cursor);
            }
        }

        return res;
    }
}