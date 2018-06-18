package socketclient.tcp;
import java.io.*;
import java.net.*;

public class SocketClientTCP {

    public static void main(String[] args) throws IOException {
        String modifiedSentence;

        String hostName = "192.168.178.13";
        int portNumber = 8080;
        String sentence = "greetings from the TCP client!";

        Socket clientSocket = new Socket(hostName, portNumber);
        
        DataOutputStream outToServer =
                new DataOutputStream(clientSocket.getOutputStream());
        
        BufferedReader inFromServer =
                new BufferedReader(new
                InputStreamReader(clientSocket.getInputStream()));

        outToServer.writeBytes(sentence + '\n');
        
        modifiedSentence = inFromServer.readLine();
        
        System.out.println("FROM SERVER: " + modifiedSentence);
        
        clientSocket.close();
    }
    
}
