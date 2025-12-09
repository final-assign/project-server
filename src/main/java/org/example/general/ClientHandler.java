package org.example.general;

import org.example.login.LoginRequestDTO;
import org.example.login.LoginResponseDTO;
import org.example.login.LoginResponseType;
import org.example.menu.img_down.ImageRequestDTO;
import org.example.order.order_request.OrderRequestDTO;
import org.example.user.User;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread{

    private Socket commSocket;
    private User user;
    private final static byte[] loginRequestPacket = new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00};

    public ClientHandler(Socket commSocket){

        this.commSocket = commSocket;
    }

    public void run(){

        InputStream is;
        OutputStream os;
        BufferedReader br;
        BufferedWriter bw;
        DataInputStream dis;
        DataOutputStream dos;

        byte[] header = new byte[1 + 1 + 4];
        byte[] data = null;
        Long userId = 0L; //유저의 아이디, 조인할 때 필요
        try{
            is = commSocket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            dis = new DataInputStream(is);
            os = commSocket.getOutputStream();
            bw = new BufferedWriter(new OutputStreamWriter(os));
            dos = new DataOutputStream(os);


            dos.write(loginRequestPacket);
            dos.flush();

            ResponseDTO responseDTO = null;
            while(true){

                dis.readFully(header);

                switch (header[1]) {

                    case 0x01 -> {
                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);
                        LoginResponseDTO loginResponseDTO = ApplicationContext.userController.login(new LoginRequestDTO(data));

                        if(loginResponseDTO.getLoginResponseType() == LoginResponseType.SUCCESS)
                            userId = loginResponseDTO.getUserId();

                        responseDTO = loginResponseDTO;
                    }

                    case 0x21 ->{
                        //메뉴 id 파싱 후 사진
                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);

                        responseDTO = ApplicationContext.menuController.getImage(new ImageRequestDTO(data));
                    }

                    case 0x41->{
                        //조회기간 파싱 후 이용 내역
                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);

                        responseDTO = ApplicationContext.orderController.getOrder(new OrderRequestDTO(data), userId);
                    }

                    case 0x42->{
                        //잔여 쿠폰
                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);

                        responseDTO = ApplicationContext.couponController.getCoupons(userId);
                    }
                }

                if(header[0] == 0x7E) break;
                dos.write(responseDTO.toBytes());
                dos.flush();
            }

            commSocket.close();
        }catch(IOException e){
            System.err.println(e);
        }
    }

}