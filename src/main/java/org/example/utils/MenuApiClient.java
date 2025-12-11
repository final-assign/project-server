package org.example.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MenuApiClient {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5000;

    /**
     * 서버에 접속하여 특정 식당의 메뉴 목록을 요청하고 응답을 받습니다.
     */
    public List<MenuListResponseDTO> getMenus(long restaurantId) throws Exception {

        List<MenuListResponseDTO> menus = new ArrayList<>();

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream()))
        {
            byte[] loginRequestPacket = new byte[6];
            dis.readFully(loginRequestPacket);

            byte[] dataLength = ByteBuffer.allocate(4).putInt(4).array();

            byte[] requestPacket = new byte[10];
            requestPacket[0] = 0x00;
            requestPacket[1] = (byte) 0x80;
            System.arraycopy(dataLength, 0, requestPacket, 2, 4);

            byte[] restaurantIdBytes = ByteBuffer.allocate(4).putInt((int) restaurantId).array();
            System.arraycopy(restaurantIdBytes, 0, requestPacket, 6, 4);

            dos.write(requestPacket); // 서버에 요청 전송
            dos.flush();

            byte[] responseHeader = new byte[6];
            dis.readFully(responseHeader);

            int dataLen = ByteBuffer.wrap(responseHeader, 2, 4).getInt();

            if (responseHeader[1] == (byte) 0x80) {

                if (dataLen > 0) {

                    throw new IOException("실제 메뉴 데이터 파싱 로직이 필요합니다.");
                }

            } else {
                throw new IOException("서버로부터 예상치 못한 응답 코드 (" + responseHeader[1] + ")를 받았습니다.");
            }

        } catch (IOException e) {
            // 통신 실패 시 예외 처리 (서버가 실행 중이지 않을 때 등)
            System.err.println(" 오류");
            throw e;
        }

        // ⚠️ 네트워크 파싱 로직이 구현되지 않아 빈 리스트 반환
        return menus;
    }
}