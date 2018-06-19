package com.example.rodi.streamservertwo;

import android.content.res.AssetManager;

import java.net.ServerSocket;
import java.net.Socket;

public class StreamingServerSetup {
    AssetManager mAm;

    public StreamingServerSetup(AssetManager am) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
            for (;;) {
                Socket clientSocket;
                clientSocket = serverSocket.accept();
                //delegate to new thread
                new Thread(new StreamingServer(clientSocket, am)).start();
            }
        } catch (Exception e) {
            System.out.println("Error");
            System.out.println(e.toString());
        }

    }
}
