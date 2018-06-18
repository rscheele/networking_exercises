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
public class SocketClientUDP {

    public static void main(String[] args) {
        String ipAddress = "192.168.1.102";
        int port  = 7896;
        if(args.length == 2) {
            ipAddress = args[0];
            port = Integer.parseInt(args[1]);
        }
        SocketClientUDP.consumeService(ipAddress, port);
    }

    public static void consumeService(String ipAddress, int port) {
        byte[] sendData = new String("send nice text").getBytes();
        byte[] receiveData = new byte[1024];
        try {
            System.out.println("SocketClientUDP will try to use capitalization service on (" + ipAddress + ", " + port + ")");
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress iPAddress = InetAddress.getByName(ipAddress);

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, iPAddress, port);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("capitalized text: " + modifiedSentence);
            clientSocket.close();
        } catch(Exception e)
        { System.out.println("Exception: " + e); }
    }
}

