package rodischeele.practicumfinal.logic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gj_webdev.communicatie.practicum_final.enities.Globals;
import com.gj_webdev.communicatie.practicum_final.enities.RTPPacket;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * Created by steven on 19-6-2015.
 */
public class ClientManager {

    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_INIT = 2;
    public static final int STATE_PLAYING = 3;

    private Socket socket;
    private DatagramSocket udpSocket;
    private int state = STATE_NONE;
    private Thread tcpThread;
    private Thread udpThread;
    private FrameReceiveListener listener;

    public void connect(String ip) throws IOException {
        if(getState() != STATE_NONE){
            throw new java.lang.IllegalStateException("state must be STATE_NONE, current state: " + state);
        }
        //Connect
        System.out.println("Connect to: " + ip + ":" + Globals.TCP_PORT);
        socket = new Socket(ip, Globals.TCP_PORT);
        System.out.println("Connected");
        state = STATE_CONNECTED;

        if(tcpThread != null){
            if(tcpThread.isAlive()) {
                try {
                    tcpThread.stop();
                }catch (Throwable e){}
            }
        }
        tcpThread = new Thread(){
            public void run(){
                try{
                    while(socket.getInputStream().read() > -1){

                    }
                }catch(Throwable e){
                    e.printStackTrace();
                }
                close();
            }
        };
        tcpThread.start();
    }

    public int getState(){
        if(!isConnected()){
            return STATE_NONE;
        }
        return state;
    }

    public boolean isConnected(){
        if(socket == null){
            return false;
        }
        return !socket.isClosed();
    }

    public void close(){
        state = STATE_NONE;
        if(socket != null){
            try{
                socket.close();
            }catch(Throwable e){}
        }
        if(udpSocket != null){
            udpSocket.close();
        }
        if(udpThread != null) {
            try {
                udpThread.stop();
                udpThread = null;
            }catch(Throwable e){}
        }
        if(tcpThread != null) {
            try {
                tcpThread.stop();
                tcpThread = null;
            }catch(Throwable e){}
        }
    }

    public void setup() throws IOException {

        send(Globals.REQUEST_SETUP);
        System.out.println("Start UDP socket @ " + Globals.UDP_PORT);
        udpSocket = new DatagramSocket(Globals.UDP_PORT);
        if(udpThread != null){
            try {
                udpThread.stop();
            }catch (Throwable e){}
        }
        udpThread = new Thread(){
            public void run(){
                startReceiving();
            }
        };
        udpThread.start();
        state = STATE_INIT;
    }

    public void play() throws IOException {
        send(Globals.REQUEST_PLAY);
        state = STATE_PLAYING;
    }

    public void pause() throws IOException {
        send(Globals.REQUEST_PAUSE);
        state = STATE_INIT;
    }

    public void tearDown() throws IOException {
        send(Globals.REQUEST_TEARDOWN);
        state = STATE_CONNECTED;
        close();
    }

    private void send(String message) throws IOException {
        System.out.println("Send: " + message);
        PrintStream ps = new PrintStream(socket.getOutputStream());
        ps.println(message);
        ps.flush();
    }

    private void startReceiving(){
        while(true) {
            try {
                System.out.println("Receive: " + listener + "|" + state);
                if(listener != null && state == STATE_PLAYING){
                    byte[] buffer = new byte[1024 * 1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(packet);
                    byte[] receiveBuffer = packet.getData();

                    System.out.println("Receive: " + receiveBuffer.length);
                    RTPPacket rtpPacket = new RTPPacket(receiveBuffer, receiveBuffer.length);
                    rtpPacket.printheader();
                    byte[] packetBuffer = new byte[rtpPacket.getPayloadLength()];
                    rtpPacket.getPayload(packetBuffer);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(packetBuffer, 0, packetBuffer.length);

                    listener.onReceive(bitmap);
                }else{
                    try {
                        Thread.sleep(200);
                    }catch(Throwable e){}
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void setFrameReceiveListener(FrameReceiveListener listener){
        this.listener = listener;
    }

    public void removeFrameReceiveListener(){
        this.listener = null;
    }

    public static interface FrameReceiveListener{
        public void onReceive(Bitmap bitmap);
    }

}
