package socketserver.tcp;
import java.io.*;
import java.net.*;

public class SocketServerTCP {

    public static void main(String[] args) throws IOException {
        
        int portNumber = 8080;

        ServerSocket welcomeSocket = new ServerSocket(portNumber);
        
        while(true) {
            Socket connectionSocket = welcomeSocket.accept();
            
            BufferedReader inFromClient =
                    new BufferedReader(new
                    InputStreamReader(connectionSocket.getInputStream()));
            
            DataOutputStream outToClient =
                    new DataOutputStream(connectionSocket.getOutputStream());
            
            String clientSentence = inFromClient.readLine();

            System.out.println(clientSentence);

            String outFromServer = "GREETINGS BACK FROM TCP SERVER " + "\n";

            outToClient.writeBytes(outFromServer);
        }
    }
    
}
