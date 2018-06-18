package socketserver.udp;

import java.io.*;
import java.net.*;

public class SocketServerUDP {

    public static void main(String[] args) throws Exception {
        
        if (args.length != 1) {
            System.err.println("Usage: java UDPServer <port number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        
        DatagramSocket serverSocket = new DatagramSocket(portNumber);

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while (true) {

            DatagramPacket receivePacket
                    = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            String sentence = new String(receivePacket.getData());

            InetAddress IPAddress = receivePacket.getAddress();

            int port = receivePacket.getPort();

            String capitalizedSentence = sentence.toUpperCase();

            sendData = capitalizedSentence.getBytes();
            
            System.out.println(capitalizedSentence);

            DatagramPacket sendPacket
                    = new DatagramPacket(sendData, sendData.length, IPAddress,
                            port);

            serverSocket.send(sendPacket);
        }

    }

}
