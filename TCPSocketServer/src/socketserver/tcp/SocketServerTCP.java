package socketserver.tcp;
import java.io.*;
import java.net.*;

public class SocketServerTCP {

    public static void main(String[] args) throws IOException {
        String clientSentence;
        String capitalizedSentence;
        
        if (args.length != 1) {
            System.err.println("Usage: java TCPServer <port number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        
        ServerSocket welcomeSocket = new ServerSocket(portNumber);
        
        while(true) {
            Socket connectionSocket = welcomeSocket.accept();
            
            BufferedReader inFromClient =
                    new BufferedReader(new
                    InputStreamReader(connectionSocket.getInputStream()));
            
            DataOutputStream outToClient =
                    new DataOutputStream(connectionSocket.getOutputStream());
            
            clientSentence = inFromClient.readLine();
            
            capitalizedSentence = clientSentence.toUpperCase() + "\n";
            
            System.out.println(capitalizedSentence);
            
            outToClient.writeBytes(capitalizedSentence);
        }
    }
    
}
