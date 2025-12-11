package org.example.general;

import org.example.login.LoginRequestDTO;
import org.example.login.LoginResponseDTO;
import org.example.login.LoginResponseType;
import org.example.menu.MenuListRequestDTO;
import org.example.menu.MenuRegisterRequestDTO;
import org.example.order.order_request.OrderDetailAdminRequestDTO;
import org.example.storage.ImageRequestDTO;
import org.example.order.order_request.OrderDetailRequestDTO;
import org.example.user.User;
import org.example.user.UserType;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket commSocket;
    private User user;
    private final static byte[] loginRequestPacket = new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00};

    public ClientHandler(Socket commSocket) {

        this.commSocket = commSocket;
    }

    public void run() {

        InputStream is;
        OutputStream os;
        BufferedReader br;
        BufferedWriter bw;
        DataInputStream dis;
        DataOutputStream dos;

        byte[] header = new byte[1 + 1 + 4];
        byte[] data = null;
        Long userId = 0L; //유저의 아이디, 조인할 때 필요
        try {
            is = commSocket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            dis = new DataInputStream(is);
            os = commSocket.getOutputStream();
            bw = new BufferedWriter(new OutputStreamWriter(os));
            dos = new DataOutputStream(os);


            dos.write(loginRequestPacket);
            dos.flush();

            ResponseDTO responseDTO = null;
            while (true) {

                dis.readFully(header);

                switch (header[1]) {

                    case 0x01 -> {
                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);
                        LoginResponseDTO loginResponseDTO = ApplicationContext.userController.login(new LoginRequestDTO(data));

                        if (loginResponseDTO.getLoginResponseType() == LoginResponseType.SUCCESS)
                            userId = loginResponseDTO.getUserId();

                        responseDTO = loginResponseDTO;
                    }

                    case 0x21 -> {
                        //메뉴 id 파싱 후 사진
                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);

                        responseDTO = ApplicationContext.getMenuController().getImage(new ImageRequestDTO(data));
                    }

                    case 0x41 -> {
                        //조회기간 파싱 후 이용 내역
                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);

                        responseDTO = ApplicationContext.getOrderController().getOrder(new OrderDetailRequestDTO(data), userId);
                    }

                    case 0x42 -> {
                        //잔여 쿠폰
                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);
                        responseDTO = ApplicationContext.getMenuController().registerMenu(new MenuRegisterRequestDTO(data));
                    }

                    case (byte) 0x80 -> {

                        responseDTO = ApplicationContext.getRestaurantController().getRestaurantListAll(userId);
                    }

                    case (byte) 0x81 -> {

                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);
                        responseDTO = ApplicationContext.getMenuController().registerMenu(new MenuRegisterRequestDTO(data));
                    }

                    case (byte) 0x91 -> {

                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);
                        responseDTO = ApplicationContext.getStorageController().insertImage(new ImageRequestDTO(data));
                    }

                    case (byte) 0xA1 -> {
                        //admim side 식당별 이용 내역 '선택' 식당명 리턴
                        if (user.getType() != UserType.ADMIN) ;//UserSerevice의 체크로 변경 필요 에러 처리도 그때가서 생각하기
                        //뭔가 넣어야할 삘...

                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);

                        //responseDTO = ApplicationContext.restaruantController.getResturant()  필요
                    }

                    case (byte) 0xA2 -> {
                        //admin side 식당 선택 이용내역 리턴
                        if (user.getType() != UserType.ADMIN) ;
                        //뭔가 넣어야할 삘...

                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);

                        responseDTO = ApplicationContext.getOrderController().getOrderAdminHistory(new OrderDetailAdminRequestDTO(data));
                    }

                    case (byte)0xC2 -> {

                        responseDTO = ApplicationContext.getMenuController().getMenuList(new MenuListRequestDTO(data));
                    }
                }

                if (header[0] == 0x7E) break;
                dos.write(responseDTO.toBytes());
                dos.flush();
            }

            commSocket.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}