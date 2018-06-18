package rodischeele.netwerkenmultimediasocket;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import rodischeele.netwerkenmultimediasocket.Client.ClientInterface;
import rodischeele.netwerkenmultimediasocket.Client.TcpClient;
import rodischeele.netwerkenmultimediasocket.Client.UdpClient;
import rodischeele.netwerkenmultimediasocket.R;
import rodischeele.netwerkenmultimediasocket.Server.ServerInterface;
import rodischeele.netwerkenmultimediasocket.Server.TcpServer;
import rodischeele.netwerkenmultimediasocket.Server.UdpServer;

public class MainActivity extends Activity {

    private ServerInterface runningServer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textview = (TextView)findViewById(R.id.textViewIp);
        textview.setText(Utils.getIPAddress(true));

        final Button button = (Button)this.findViewById(R.id.button);
        final TextView textViewStatus = (TextView)findViewById(R.id.textView2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (runningServer != null) {
                        runningServer.stop();
                        runningServer = null;
                        button.setText("Start server");
                        textViewStatus.setText("Server not running");
                    } else {
                        runningServer = getServerInterface();
                        runningServer.start();
                        button.setText("Stop server");
                        textViewStatus.setText("Running in "+( isUDP() ? "UDP" : "TCP" )+" mode");
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        final Button button2 = (Button)this.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    System.out.println("click");
                    EditText editText = (EditText)findViewById(R.id.editText);
                    String ip = editText.getText().toString();
                    ClientInterface client = getClientInterface();
                    client.connect(ip);
                }catch(Throwable e){
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isUDP(){
        Switch aSwitch = (Switch) this.findViewById(R.id.switch1);
        return aSwitch.isChecked();
    }

    private ServerInterface getServerInterface(){
        return (isUDP() ? new UdpServer() : new TcpServer());
    }

    private ClientInterface getClientInterface(){
        return (isUDP() ? new UdpClient() : new TcpClient());
    }
}

