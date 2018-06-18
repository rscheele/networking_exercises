/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vh8i_socketpracticum;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
/**
 *
 * @author Erco
 */
public class SocketClientTCP {

    public static void main(String[] args) {
        String ipAddress = "192.168.1.102";
        int port  = 7896;
        if(args.length == 2) {
            ipAddress = args[0];
            port = Integer.parseInt(args[1]);
        }
        SocketClientTCP.consumeService(ipAddress, port);
    }

    public static void consumeService(String ipAddress, int port) {
        String inTxt = "";
        try {
            System.out.println("SocketClientTCP will try to use capitalization service on (" + ipAddress + ", " + port + ")");
            Socket socket = new Socket(ipAddress, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("send nice text");
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inTxt = inFromServer.readLine();
            socket.close();
        } catch(Exception e)
        { System.out.println("Exception: " + e); }
        System.out.println("capitalized text: " + inTxt);
    }

}