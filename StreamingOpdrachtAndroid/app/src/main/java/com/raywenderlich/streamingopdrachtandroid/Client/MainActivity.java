package com.raywenderlich.streamingopdrachtandroid.Client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.raywenderlich.streamingopdrachtandroid.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Client client = null;
    private Button b1, b2, b3, b4 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new Client();

        b1 = (Button) findViewById(R.id.setup_button);
        b1.setOnClickListener(this);

        b2 = (Button) findViewById(R.id.play_button);
        b2.setOnClickListener(this);

        b3 = (Button) findViewById(R.id.pause_button);
        b3.setOnClickListener(this);

        b4 = (Button) findViewById(R.id.teardown_button);
        b4.setOnClickListener(this);
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.setup_button: /** Setup stream */
                client.setupButtonListener();
                break;

            case R.id.play_button: /** Play stream */
                client.playButtonListener();
                break;

            case R.id.pause_button: /** Pause stream */
                try {
                    client.pauseButtonListener();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.teardown_button: /** Teardown button */
                try {
                    client.tearButtonListener();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
