package org.example.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class PaymentApiClient {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5000;

    /**
     * 최종 결제 요청 패킷을 서버에 전송합니다.
     * 클라이언트가 선택한 purchaseType을 문자열로 전송합니다.
     */
    public String sendPaymentRequest(long userId, long menuId, long charge, String purchaseType, long couponId) throws Exception {

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream()))
        {
            byte[] initialPacket = new byte[4];
            dis.readFully(initialPacket);

            byte requestCommand = 0x10;

            byte[] purchaseTypeBytes = purchaseType.getBytes("UTF-8");
            int purchaseTypeLength = purchaseTypeBytes.length;

            int totalBodyLength = 4 + purchaseTypeLength + (4 * 5);

            byte[] header = new byte[6];
            header[0] = 0x00;
            header[1] = requestCommand;
            ByteBuffer.wrap(header, 2, 4).putInt(totalBodyLength); // Body 길이 기록

            ByteBuffer body = ByteBuffer.allocate(totalBodyLength);
            body.putInt(purchaseTypeLength);

            body.put(purchaseTypeBytes);

            body.putInt((int) menuId);
            body.putInt((int) couponId);
            body.putInt((int) charge);
            body.putInt((int) userId);
            body.putInt(0);

            // 3. 서버에 전송
            dos.write(header);
            dos.write(body.array());
            dos.flush();

            byte[] responseHeader = new byte[6];
            dis.readFully(responseHeader);
            int responseDataLen = ByteBuffer.wrap(responseHeader, 2, 4).getInt();

            if (responseDataLen > 0) {
                byte[] responseBody = new byte[responseDataLen];
                dis.readFully(responseBody);
                return new String(responseBody, "UTF-8");
            } else {
                return "결제 요청 성공.";
            }
        } catch (IOException e) {
            throw new IOException("서버 통신 실패 (결제 요청): " + e.getMessage());
        }
    }
}