package rodischeele.practicumfinal.logic;

import com.gj_webdev.communicatie.practicum_final.enities.Globals;
import com.gj_webdev.communicatie.practicum_final.enities.RTPPacket;
import com.gj_webdev.communicatie.practicum_final.util.HttpApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by steven on 11-6-2015.
 */
public class ServerManager {

    private ServerSocket server;
    private DatagramSocket broadcastSocket;
    private ArrayList<ClientHandler> clients = new ArrayList();
    private String name;
    private int frameNumber = 0;
    private Timer timer;

    private byte[] frameBuffer;

    public void register(String n) throws Exception {
        if(name != null){
            throw new Exception("Server already registered");
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", n);
        params.put("ip", getServerAdress());
//        String result = HttpApi.post(Globals.URL_REGISTER, params);
//        if(!result.equals("done")){
//            throw new Exception(result);
//        }
    }

    public void unregister(){
        if(name != null){
            try{
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("ip", getServerAdress());
                name = null;
                HttpApi.post(Globals.URL_UNREGISTER, params);
            }catch(Throwable e){}
        }
    }

    public void open() throws IOException {
        if(!isOpen()) {
            System.out.println("Open server @ port: " + Globals.TCP_PORT);
            server = new ServerSocket(Globals.TCP_PORT);
            broadcastSocket = new DatagramSocket();
            System.out.println("Server opened");
            clients = new ArrayList();
            frameNumber = 0;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcast();
                }
            }, Globals.FRAME_RATE, Globals.FRAME_RATE);
            try{
                while (true) {
                    Socket socket = server.accept();
                    System.out.println("New connection @ " + socket.getInetAddress().getHostAddress());
                    ClientHandler handler = new ClientHandler(socket);
                    new Thread(handler).start();
                    clients.add(handler);
                }
            }catch(IOException e){
                close();
                throw e;
            }
        }
    }

    public void close(){
        if(clients != null){
            clients = null;
        }
        try {
            server.close();
        } catch (Exception e) {}
        server  = null;
        if(timer != null){
            timer.cancel();
        }
    }

    public boolean isOpen(){
        if(server == null){
            return false;
        }
        return server.isBound() && !server.isClosed();
    }

    public void prepareBroadcase(byte[] data){
        frameBuffer = data;
    }

    private void broadcast(){
        //Do nothing if socket is closed
        if(!isOpen()){
            return;
        }
        //Get buffer
        byte[] data = frameBuffer;
        //Skip frame if buffer is empty
        if(data == null){
            return;
        }
        frameBuffer = null;

        //Create rtpPacket
        frameNumber++;
        RTPPacket rtpPacket = new RTPPacket(RTPPacket.MJPEG_TYPE, frameNumber, (int) System.currentTimeMillis(), data, data.length);
        //Put rtpPacket into byte[] buffer
        byte[] rtpData = new byte[rtpPacket.getLength()];
        rtpPacket.getPacket(rtpData);
        //System.out.println("RTP Package:");
        //rtpPacket.printheader();

        //Send to each client that is active
        try {
            for (ClientHandler client : clients) {
                if (client.isActive()) {
                    try {
                        System.out.println("send to: " + client.getAddress().getHostAddress() + " -> " + rtpData.length);
                        DatagramPacket datagramPacket = new DatagramPacket(rtpData, rtpData.length, client.getAddress(), Globals.UDP_PORT);
                        broadcastSocket.send(datagramPacket);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    public String getServerAdress(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }catch(Throwable e){
            return null;
        }
    }

    private static class ClientHandler implements Runnable {

        private Socket socket;
        private InetAddress address;
        private int state = ClientManager.STATE_CONNECTED;

        public ClientHandler(Socket socket){
            this.socket = socket;
            if(socket != null) {
                this.address = socket.getInetAddress();
            }
        }

        @Override
        public void run() {
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while((line = reader.readLine()) != null){
                    System.out.println("Receive from '" + socket.getInetAddress().getHostAddress() + "': " + line);
                    handle(line);
                }
            }catch(Throwable e){
                e.printStackTrace();
            }
            state = ClientManager.STATE_NONE;
        }

        private void handle(String line){
            try{
                switch (line){
                    case Globals.REQUEST_SETUP:
                        state = ClientManager.STATE_INIT;
                        System.out.println("[INIT]");
                        break;
                    case Globals.REQUEST_PLAY:
                        state = ClientManager.STATE_PLAYING;
                        System.out.println("[PLAY]");
                        break;
                    case Globals.REQUEST_PAUSE:
                        state = ClientManager.STATE_INIT;
                        System.out.println("[PAUSE]");
                        break;
                    case Globals.REQUEST_TEARDOWN:
                        state = ClientManager.STATE_CONNECTED;
                        System.out.println("[TEARDOWN]");
                        break;
                    default:
                        System.out.println("[UNKNOWN]");
                }

            }catch(Throwable e){
                e.printStackTrace();
            }
        }

        public boolean isClosed(){
            if(socket == null){
                return true;
            }
            return socket.isClosed();
        }

        public boolean isActive(){
            if(isClosed()){
                return false;
            }
            return state == ClientManager.STATE_PLAYING;
        }

        public InetAddress getAddress(){
            return address;
        }

    }

}
