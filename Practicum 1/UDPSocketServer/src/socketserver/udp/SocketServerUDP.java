package socketserver.udp;

import java.io.*;
import java.net.*;

public class SocketServerUDP {

    public static void main(String[] args) throws Exception {

        int portNumber = 8080;
        
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

            System.out.println(sentence);

            String outSentence = "Hello back form the UDP server";

            sendData = outSentence.getBytes();
            

            DatagramPacket sendPacket
                    = new DatagramPacket(sendData, sendData.length, IPAddress,
                            port);

            serverSocket.send(sendPacket);
        }

    }

}
