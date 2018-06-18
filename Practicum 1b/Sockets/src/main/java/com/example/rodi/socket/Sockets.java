package com.example.rodi.socket;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.rodi.socketclienttcp.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Sockets extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_client_tcp);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void runTcpClient(View view) throws IOException {
        System.out.println("Running TCP client.");

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

        TextView tv = (TextView)findViewById(R.id.socketText);
        tv.setText(modifiedSentence);

        clientSocket.close();
    }

    public void runUdpClient(View view) throws IOException {
        System.out.println("Running UDP client.");

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

        TextView tv = (TextView)findViewById(R.id.socketText);
        tv.setText(modifiedSentence);

        System.out.println("FROM SERVER: " + modifiedSentence);
        clientSocket.close();
    }

    public void runTcpServer(View view) throws IOException {
        System.out.println("Running TCP server.");

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

            TextView tv = (TextView)findViewById(R.id.socketText);
            tv.setText(clientSentence);

            String outFromServer = "GREETINGS BACK FROM TCP SERVER " + "\n";

            outToClient.writeBytes(outFromServer);
        }
    }

    public void runUdpServer(View view) throws IOException {
        System.out.println("Running UDP server.");

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

            TextView tv = (TextView)findViewById(R.id.socketText);
            tv.setText(sentence);

            serverSocket.send(sendPacket);
        }
    }
}
