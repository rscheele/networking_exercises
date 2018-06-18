package rodischeele.netwerkenmultimediasocket.Client;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import rodischeele.netwerkenmultimediasocket.Config;

/**
 * Created by Rodi on 6-6-2016.
 */
public class UdpClient extends AsyncTask<Void, Void, String> implements ClientInterface {

    private static final String SEND_LINE = "Hello Gino!";

    private DatagramSocket socket;

    private String ip;

    public void connect(String ip){
        this.ip = ip;
        this.execute();
    }

    public void run() throws IOException {
        socket = new DatagramSocket();

        DatagramPacket packet = new DatagramPacket(SEND_LINE.getBytes(), SEND_LINE.length(), InetAddress.getByName(ip), Config.UDP_PORT);
        socket.send(packet);
    }

    public void disconnect() throws IOException {
        if(socket != null) {
            socket.close();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            this.run();
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
