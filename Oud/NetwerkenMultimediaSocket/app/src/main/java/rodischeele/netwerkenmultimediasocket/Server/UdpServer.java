package rodischeele.netwerkenmultimediasocket.Server;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import rodischeele.netwerkenmultimediasocket.Config;

/**
 * Created by Rodi on 6-6-2016.
 */
public class UdpServer extends AsyncTask<Void, Void, String> implements ServerInterface {

    private DatagramSocket socket;

    public void start(){
        this.execute();
    }

    public void run() throws IOException {
        socket = new DatagramSocket(Config.UDP_PORT);
        while (!socket.isClosed()) {
            byte[] buffer = new byte[4048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            System.out.println("Receive: " + new String(buffer));
        }
    }

    public void stop() {
        try {
            if (socket != null) {
                socket.close();
            }
        }catch(Throwable e){}
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
