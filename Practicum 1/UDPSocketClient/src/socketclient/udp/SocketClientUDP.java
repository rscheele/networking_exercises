package socketclient.udp;

import java.io.*;
import java.net.*;

public class SocketClientUDP {

    public static void main(String[] args) throws Exception {
        
        if (args.length != 2) {
            System.err.println("Usage: java UDPClient <host name> <port number>");
            System.exit(1);
        }
        
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
                
        BufferedReader inFromUser
                = new BufferedReader(new InputStreamReader(System.in));

        DatagramSocket clientSocket = new DatagramSocket();

        InetAddress IPAddress = InetAddress.getByName(hostName);

        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        String sentence = inFromUser.readLine();
        sendData = sentence.getBytes();

        DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);

        clientSocket.send(sendPacket);

        DatagramPacket receivePacket
                = new DatagramPacket(receiveData, receiveData.length);

        clientSocket.receive(receivePacket);

        String modifiedSentence
                = new String(receivePacket.getData());

        System.out.println("FROM SERVER:" + modifiedSentence);
        clientSocket.close();

    }

}
