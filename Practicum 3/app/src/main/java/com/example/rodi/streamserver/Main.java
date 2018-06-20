package com.example.rodi.streamserver;

import android.content.res.AssetManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.net.ServerSocket;
import java.net.Socket;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        AssetManager assetManager = this.getAssets();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        StreamingServer streamingServer = new StreamingServer(assetManager);
        try {
            streamingServer.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
