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
public class SocketServerTCP {

    public static void main(String[] args) {
        int port  = 7896;
        if(args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        SocketServerTCP.provideService(port);
    }

    public static void provideService(int port)
    {
        String clientSentence;
        String capitalizedSentence;
        try {
            ServerSocket listener = new ServerSocket(port);
            System.out.println("SocketServerTCP is listening on (*, " + port + ") and ready to receive capitalization requests.");
            while(true) {
                Socket connectionSocket = listener.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                clientSentence = inFromClient.readLine();
                capitalizedSentence = clientSentence.toUpperCase() + '\n';
                outToClient.writeBytes(capitalizedSentence);
                System.out.println("SocketServerTCP has delivered the capitalization service to ("
                        + connectionSocket.getInetAddress() + ", " + connectionSocket.getPort() + ").");
            }
        } catch (Exception e)
        { System.out.println("Exception: " + e); }
    }
}