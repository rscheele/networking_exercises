package socketclient.tcp;
import java.io.*;
import java.net.*;

public class SocketClientTCP {

    public static void main(String[] args) throws IOException {
        String sentence;
        String modifiedSentence;
        
        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java TCPClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        
        Socket clientSocket = new Socket(hostName,portNumber);
        
        DataOutputStream outToServer =
                new DataOutputStream(clientSocket.getOutputStream());
        
        BufferedReader inFromServer =
                new BufferedReader(new
                InputStreamReader(clientSocket.getInputStream()));
        
        sentence = inFromUser.readLine();
        
        outToServer.writeBytes(sentence + '\n');
        
        modifiedSentence = inFromServer.readLine();
        
        System.out.println("FROM SERVER: " + modifiedSentence);
        
        clientSocket.close();
    }
    
}
