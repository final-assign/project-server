package org.example;

import org.example.general.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RestaurantManagerApp {

    private static final int PORT = 5000;

    public void run(){
        ServerSocket listenSocket = null;
        Socket commSocket = null;

        try{
            listenSocket = new ServerSocket(PORT);
            System.out.println("Waiting for connection...");

            while(true){
                commSocket = listenSocket.accept();

                System.out.println("Connection received from " + commSocket.getInetAddress().getHostName() + " : " + commSocket.getPort());

                ClientHandler cliHandler = new ClientHandler(commSocket);
                cliHandler.start();
            }
        }catch(IOException e){
            System.err.println(e);
        }finally{
            if(listenSocket != null){
                try{
                    listenSocket.close();
                }catch(IOException e){
                    System.out.println(e);
                }
            }
        }
    }
}
