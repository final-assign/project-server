package org.example.menu;

import org.example.general.ResponseDTO;
import org.example.general.Utils;

import java.util.ArrayList;
import java.util.List;

public class MenuListResponseDTO implements ResponseDTO {

    private List<MenuInfoDTO> menuList;

    // [서버용] DB 조회 결과로 생성
    public MenuListResponseDTO(List<MenuInfoDTO> menuList) {
        this.menuList = menuList;
    }

    // [서버용] 직렬화: ByteArrayOutputStream 없이 구현
    public byte[] toBytes() {
        // 1. 각 메뉴를 바이트로 변환하여 임시 리스트에 저장 및 전체 크기 계산
        List<byte[]> serializedMenus = new ArrayList<>();
        int menusByteSize = 0;

        for (MenuInfoDTO info : menuList) {
            byte[] infoBytes = info.toBytes(); // MenuInfoDTO.toBytes() 호출
            serializedMenus.add(infoBytes);
            menusByteSize += infoBytes.length;
        }

        // 2. 전체 패킷 크기 계산
        // Body 구조: [메뉴개수(4byte)] + [메뉴바이트들...]
        int bodySize = 4 + menusByteSize;

        // Header 구조: [Type(1)] + [Code(1)] + [BodyLength(4)] = 6 bytes
        int packetSize = 6 + bodySize;

        byte[] packet = new byte[packetSize];
        int offset = 0;

        // ------------------------------------------------
        // 3. 헤더 작성 (0 ~ 5 index)
        // ------------------------------------------------
        packet[0] = 0x02; // 0x02
        packet[1] = (byte) 0x91;                    // Code

        // Body Length (4byte)
        System.arraycopy(Utils.intToBytes(bodySize), 0, packet, 2, 4);
        offset += 6;

        // ------------------------------------------------
        // 4. 바디 작성 (6 ~ end index)
        // ------------------------------------------------

        // (1) 리스트 개수 (List Size) 쓰기
        System.arraycopy(Utils.intToBytes(menuList.size()), 0, packet, offset, 4);
        offset += 4;

        // (2) 미리 변환해둔 각 메뉴 바이트 배열들을 순서대로 복사
        for (byte[] menuBytes : serializedMenus) {
            System.arraycopy(menuBytes, 0, packet, offset, menuBytes.length);
            offset += menuBytes.length;
        }

        return packet;
    }
}