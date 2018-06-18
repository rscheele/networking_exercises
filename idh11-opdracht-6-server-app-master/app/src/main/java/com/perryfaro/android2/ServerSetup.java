package com.perryfaro.android2;

import android.content.res.AssetManager;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by perryfaro on 23-03-16.
 */
public class ServerSetup {

    AssetManager mAm;

    public ServerSetup(AssetManager am) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Integer.parseInt("30000"));
            System.out.println("We komen hier!!!");
            for (;;) {
                Socket clientSocket = null;
                clientSocket = serverSocket.accept();
                //delegate to new thread
                System.out.println("We komen hier!!!");
                new Thread(new Server(clientSocket, am)).start();
            }
        } catch (Exception e) {
            System.out.println("Error");
            System.out.println(e.toString());
        }

    }
}
