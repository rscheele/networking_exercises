package com.example.rodi.streamservertwo;

import android.content.res.AssetManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;

public class Main extends AppCompatActivity {

    AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        assetManager = this.getAssets();

        Thread thread = new Thread() {
            @Override
            public void run() {

                StreamingServerSetup streamingServerSetup = new StreamingServerSetup(assetManager);
            }
        };
        thread.start();
    }

    /*public class StartServer
    {
        ServerSocket serverSocket = null;
        {
            try {
                while (true){
                    serverSocket = new ServerSocket(8080);
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new StreamingServer(clientSocket, assetManager)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

}
