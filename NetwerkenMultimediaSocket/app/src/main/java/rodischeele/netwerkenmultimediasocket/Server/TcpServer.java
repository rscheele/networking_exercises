package rodischeele.netwerkenmultimediasocket.Server;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import rodischeele.netwerkenmultimediasocket.Config;

/**
 * Created by Rodi on 6-6-2016.
 */
public class TcpServer extends AsyncTask<Void, Void, String> implements ServerInterface {

    private ServerSocket server;

    protected String doInBackground(Void... params){
        try {
            this.run();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public void run() throws IOException {
        System.out.println("Start server on port: " + Config.TCP_PORT);
        server = new ServerSocket(Config.TCP_PORT);
        while(!server.isClosed()){
            try {
                Socket socket = server.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                System.out.println("Receive: " + line);
                PrintStream ps = new PrintStream(socket.getOutputStream());
                ps.println(System.currentTimeMillis());
                ps.flush();
                socket.close();
            }catch(Throwable e){
                e.printStackTrace();
            }
        }
    }

    public void start() {
        this.execute();
    }

    public void stop() {
        try {
            if (server != null) {
                server.close();
            }
        }catch(Throwable e){

        }
    }

}