package rodischeele.netwerkenmultimediasocket.Client;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import rodischeele.netwerkenmultimediasocket.Config;

/**
 * Created by Rodi on 6-6-2016.
 */
public class TcpClient extends AsyncTask<Void, Void, String> implements ClientInterface {

    private static final String line = "Hi Steven!";

    private String ip;

    public void connect(String ip){
        System.out.println("connect " + ip);
        this.ip = ip;
        this.execute();
    }


    @Override
    protected String doInBackground(Void... params){
        System.out.println("do background");
        try{
            TcpClient.this.run();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void run() throws IOException {
        System.out.println("Connect to: " + ip + ":" + Config.TCP_PORT);
        Socket socket = new Socket(ip, Config.TCP_PORT);
        System.out.println("Connected");
        PrintStream ps = new PrintStream(socket.getOutputStream());
        System.out.println("Send:" + line);
        ps.println(line);
        ps.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = reader.readLine();
        System.out.println("Receive: " + response);
        socket.close();
    }

}