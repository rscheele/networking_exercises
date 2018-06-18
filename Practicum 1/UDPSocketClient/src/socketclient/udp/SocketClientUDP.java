package socketclient.udp;

import java.io.*;
import java.net.*;

public class SocketClientUDP {

    public static void main(String[] args) throws Exception {

        int portNumber = 8080;
        String hostName = "192.168.178.13";

        DatagramSocket clientSocket = new DatagramSocket();

        InetAddress IPAddress = InetAddress.getByName(hostName);

        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        String sentence = "Greetings from the UDP client!";
        sendData = sentence.getBytes();

        DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);

        clientSocket.send(sendPacket);

        DatagramPacket receivePacket
                = new DatagramPacket(receiveData, receiveData.length);

        clientSocket.receive(receivePacket);

        String modifiedSentence
                = new String(receivePacket.getData());

        System.out.println("FROM SERVER: " + modifiedSentence);
        clientSocket.close();

    }

}
