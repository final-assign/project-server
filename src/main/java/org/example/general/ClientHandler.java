package org.example.general;

import org.example.coupon.CouponCreateRequestDTO;
import org.example.coupon.MenuCouponRequestDTO;
import org.example.login.LoginRequestDTO;
import org.example.login.LoginResponseDTO;
import org.example.login.LoginResponseType;
import org.example.order.OrderCardRequestDTO;
import org.example.order.OrderCouponRequestDTO;
import org.example.storage.ImgDownReqDTO;
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
        DataInputStream dis = null;
        DataOutputStream dos = null;

        byte[] header = new byte[1 + 1 + 4];
        byte[] data;
        Long userId = 0L;

        try {
            is = commSocket.getInputStream();
            os = commSocket.getOutputStream();
            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);

            dos.write(loginRequestPacket);
            dos.flush();

            while (true) {
                try {
                    ResponseDTO responseDTO;

                    dis.readFully(header);

                    switch (header[1]) {
                        case 0x01 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            LoginResponseDTO loginResponseDTO =
                                    ApplicationContext.userController.login(new LoginRequestDTO(data));
                            if (loginResponseDTO.getLoginResponseType() == LoginResponseType.SUCCESS) {
                                userId = loginResponseDTO.getUserId();
                            }
                            responseDTO = loginResponseDTO;
                        }
                        case 0x21 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getMenuController().getImage(new ImgDownReqDTO(data));
                        }
                        case 0x41 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getOrderController()
                                    .getOrder(new OrderDetailRequestDTO(data), userId);
                        }
                        case 0x42 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getMenuController()
                                    .registerMenu(new MenuRegisterRequestDTO(data));
                        }
                        case (byte) 0x80 -> {
                            responseDTO = ApplicationContext.getRestaurantController()
                                    .getRestaurantListAll(userId);
                        }
                        case (byte) 0x81 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getMenuController()
                                    .registerMenu(new MenuRegisterRequestDTO(data));
                        }
                        case (byte) 0x91 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getStorageController()
                                    .insertImage(new ImageRequestDTO(data));
                        }
                        case (byte) 0xA1 -> {
                            if (user.getType() != UserType.ADMIN) {
                            }
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = null;
                        }
                        case (byte) 0xA2 -> {
                            if (user.getType() != UserType.ADMIN) {
                            }
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getOrderController()
                                    .getOrderAdminHistory(new OrderDetailAdminRequestDTO(data));
                        }
                        case (byte) 0xC2 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getMenuController()
                                    .getMenuList(new MenuListRequestDTO(data));
                        }
                        case (byte) 0xC3 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getCouponController()
                                    .createCoupon(new CouponCreateRequestDTO(data));
                        }
                        case (byte) 0x12 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getMenuController()
                                    .getRestMenu(userId, Utils.bytesToLong(data, 0));
                        }
                        case (byte) 0x14 -> {
                            responseDTO = ApplicationContext.getRestaurantController()
                                    .getAvailableRestaurant(userId);
                        }
                        case (byte) 0x32 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getOrderController()
                                    .processCardOrder(new OrderCardRequestDTO(data), userId);
                        }
                        case (byte) 0x39 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getCouponController()
                                    .getCountByMenuId(new MenuCouponRequestDTO(data), userId);
                        }
                        case (byte) 0x51 -> {
                            data = new byte[Utils.bytesToInt(header, 2)];
                            dis.readFully(data);
                            responseDTO = ApplicationContext.getOrderController()
                                    .processCouponOrder(new OrderCouponRequestDTO(data), userId);
                        }
                        default -> {
                            responseDTO = GeneralResponseDTO.builder()
                                    .code(ResponseCode.FORBIDDEN)
                                    .build();
                        }
                    }

                    if (header[0] == 0x7E) {
                        break;
                    }

                    dos.write(responseDTO.toBytes());
                    dos.flush();

                } catch (GeneralException e) {
                    System.out.println("[CLIENT HANDLER] " + e.getMessage());
                    if (dos != null) {
                        dos.write(GeneralResponseDTO.builder().code(e.getCode()).build().toBytes());
                        dos.flush();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                if (commSocket != null && !commSocket.isClosed()) {
                    commSocket.close();
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}