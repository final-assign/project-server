package org.example.general;

import org.example.login.LoginRequestDTO;
import org.example.login.LoginResponseDTO;
import org.example.login.LoginResponseType;
import org.example.menu.MenuRegisterRequestDTO;
import org.example.restaurant.RestaurantListResponseDTO;
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

        Long userId = 0L;
        try{

            is = commSocket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            dis = new DataInputStream(is); //입력 받아올 때
            os = commSocket.getOutputStream();
            bw = new BufferedWriter(new OutputStreamWriter(os));
            dos = new DataOutputStream(os); //응답할 때


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
                    case 0x11 -> {
                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);
                        responseDTO = ApplicationContext.getRestaurantController().getUserRestaurantID(userId);


                    }
                    case 0x12 -> {
                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);
                        responseDTO = ApplicationContext.getRestaurantController().getUserRestaurantID(userId);
                    }




                    case (byte) 0x80 -> {

                        responseDTO = ApplicationContext.getRestaurantController().getRestaurantListAll(userId);
                    }

                    case (byte) 0x81 -> {

                        data = new byte[Utils.bytesToInt(header, 2)];
                        dis.readFully(data);
                        responseDTO = ApplicationContext.getMenuController().registerMenu(new MenuRegisterRequestDTO(data));
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