package com.perryfaro.android2;

import android.content.res.AssetManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText ipadres;
    AssetManager am;
    IpUtils ipUtils = new IpUtils();

    Thread thread;

    Switch serverSwitch;

    String url = "http://192.168.1.100:3000";
    EditText serverHost;
    String ip;
    String idServer;
    Spinner movie;
    EditText previewName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ipadres = (EditText) findViewById(R.id.editText);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        ipadres.setText(ip);

        ip = ipUtils.getIPAddress(true);
        ipadres.setText(ip);

        final AssetManager am = getAssets();

        serverHost = (EditText) findViewById(R.id.editText2);

        movie = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.select_options, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        movie.setAdapter(adapter);
        previewName = (EditText) findViewById(R.id.editText3);

        if (serverHost.getText().length() > 0) {
            url = serverHost.getText().toString();
        }

        serverSwitch = (Switch) findViewById(R.id.switch1);

        serverSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked == false) {

                    ServerProxyParams params = new ServerProxyParams(url, "/api/streams/" + idServer, "", "DELETE");

                    ServerProxy serverProxy = (ServerProxy) new ServerProxy().execute(params);


                } else {

                    JSONObject jObjectData = new JSONObject();
                    try {
                        // 2nd array for user information

                        jObjectData.put("name", previewName.getText().toString());
                        jObjectData.put("ip", ip);
                        jObjectData.put("port", 30000);
                        jObjectData.put("movie", "movie.Mjpeg");
                    } catch (Exception e) {

                    }

                    ServerProxyParams params = new ServerProxyParams(url, "/api/streams/", jObjectData.toString(), "POST");

                    ServerProxy serverProxy = (ServerProxy) new ServerProxy();
                    serverProxy.setDelegate(new AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject jsonObject) throws JSONException {

                            System.out.println(jsonObject.toString());
                            idServer = jsonObject.getString("_id");
                            System.out.println(idServer);
                            Thread thread = new Thread() {
                                @Override
                                public void run() {

                                    new ServerSetup(am);
                                }
                            };
                            thread.start();

                        }
                    });

                    serverProxy.execute(params);
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
