package com.example.rodi.streamingclientbuffer;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    Button mButton;
    Button mButton2;
    Button mButton3;
    Button mButton4;

    ImageView videoView;

    StreamingClient streamingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.button);
        mButton2 = (Button) findViewById(R.id.button2);
        mButton3 = (Button) findViewById(R.id.button3);
        mButton4 = (Button) findViewById(R.id.button4);

        videoView = (ImageView) findViewById(R.id.videoView);

        streamingClient = new StreamingClient(videoView);
        new Thread(streamingClient).start();

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        streamingClient.setupButtonListener();
                    }
                }).start();
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (streamingClient != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            streamingClient.playButtonListener();
                        }
                    }).start();
                }
            }
        });

        mButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (streamingClient != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            streamingClient.pauseButtonListener();
                        }
                    }).start();
                }
            }
        });

        mButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (streamingClient != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            streamingClient.tearButtonListener();
                            streamingClient = null;
                        }
                    }).start();
                }
            }
        });
    }
}
