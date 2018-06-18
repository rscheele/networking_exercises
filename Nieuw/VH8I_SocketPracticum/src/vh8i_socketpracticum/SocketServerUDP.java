/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vh8i_socketpracticum;

import java.io.*;
import java.net.*;

/**
 *
 * @author Erco
 */
public class SocketServerUDP {

    public static void main(String[] args) {
        int serverPort  = 7896;
        if(args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }
        SocketServerUDP.provideService(serverPort);
    }

    public static void provideService(int serverPort) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(serverPort);
            System.out.println("SocketServerUDP on (*, " + serverPort + ") is ready to receive capitalization requests.");

            byte[] receiveData = new byte[1024];
            byte[] sendData  = new byte[1024];

            while(true)
            {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                int receivedPacketLength = receivePacket.getLength();
                InetAddress clientIpAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String sentence = new String(receivePacket.getData());
                String capitalizedSentence = sentence.toUpperCase();
                sendData = capitalizedSentence.getBytes();
                DatagramPacket sendPacket =  new DatagramPacket(sendData, receivedPacketLength, clientIpAddress, clientPort);
                serverSocket.send(sendPacket);
                System.out.println("SocketServerUDP has delivered the capitalization service to ("
                        + clientIpAddress + ", " + clientPort + ").");
            }
        } catch (Exception e)
        { System.out.println("Exception: " + e); }
    }
}

