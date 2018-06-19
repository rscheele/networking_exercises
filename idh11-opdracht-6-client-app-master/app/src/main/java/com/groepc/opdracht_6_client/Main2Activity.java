package com.groepc.opdracht_6_client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class Main2Activity extends AppCompatActivity {

    static int RTP_RCV_PORT = 25000; //port where the client will receive the RTP packets
    static int state; //RTSP state == INIT or READY or PLAYING
    static BufferedReader RTSPBufferedReader;
    static BufferedWriter RTSPBufferedWriter;
    static String VideoFileName; //video file to request to the server
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video

    Client mClient = null;

    Button mButton;
    Button mButton2;
    Button mButton3;
    Button mButton4;

    ImageView videoView;

    Movies  movie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        movie = (Movies) getIntent().getExtras().getSerializable("movie");

        mButton = (Button) findViewById(R.id.button);
        mButton2 = (Button) findViewById(R.id.button2);
        mButton3 = (Button) findViewById(R.id.button3);
        mButton4 = (Button) findViewById(R.id.button4);

        videoView = (ImageView) findViewById(R.id.videoView);

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        System.out.println(movie.port + movie.ip);

                        mClient = new Client(movie.port, movie.ip, movie.movie);
                        mClient.setupButtonListener();
                    }
                }).start();
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (mClient != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            mClient.playButtonListener();
                        }
                    }).start();
                }
            }
        });

        mButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mClient != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            mClient.pauseButtonListener();
                        }
                    }).start();
                }
            }
        });

        mButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mClient != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            mClient.tearButtonListener();
                            mClient = null;
                        }
                    }).start();
                }
            }
        });

    }

    public void changeImageView (Bitmap bmp) {
        videoView.setImageBitmap(bmp);
    }

    public class Client {


        //RTP variables:
        //----------------
        DatagramPacket rcvdp; //UDP packet received from the server
        DatagramSocket RTPsocket; //socket to be used to send and receive UDP packets


        Timer timer; //timer used to receive data from the UDP socket
        byte[] buf; //buffer used to store data received from the server

        //RTSP variables
        //----------------
        //rtsp states
        final static int INIT = 0;
        final static int READY = 1;
        final static int PLAYING = 2;

        Socket RTSPsocket; //socket used to send/receive RTSP messages
        //input and output stream filters

        int RTSPSeqNb = 0; //Sequence number of RTSP messages within the session
        int RTSPid = 0; //ID of the RTSP session (given by the RTSP Server)

        final static String CRLF = "\r\n";

        //Video constants:
        //------------------


        //--------------------------
        //Constructor
        //--------------------------
        public Client(Integer port, String ServerHost, String movieName) {

            try {
                //get server RTSP port and IP address from the command line
                //------------------
                int RTSP_server_port = port;
                InetAddress ServerIPAddr = InetAddress.getByName(ServerHost);

                //get video filename to request:
                VideoFileName = movieName;

                //Establish a TCP connection with the server to exchange RTSP messages
                //------------------
                RTSPsocket = new Socket(ServerIPAddr, RTSP_server_port);

                //Set input and output stream filters:
                RTSPBufferedReader = new BufferedReader(new InputStreamReader(RTSPsocket.getInputStream()));
                RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(RTSPsocket.getOutputStream()));

                //init RTSP state:
                state = INIT;

                timer = new Timer();

                //allocate enough memory for the buffer used to receive data from the server
                buf = new byte[15000];
            } catch (Exception e) {
                System.out.println("Something went wrong");
                System.out.println(e.getMessage());
                System.out.println(e.toString());
            }
        }

        //------------------------------------
        //Handler for buttons
        //------------------------------------
        //.............
        //TO COMPLETE
        //.............
        //Handler for Setup button
        //-----------------------


        public void setupButtonListener() {

            //System.out.println("Setup Button pressed !");
            if (state == INIT) {
                //Init non-blocking RTPsocket that will be used to receive data
                try {

                    //construct a new DatagramSocket to receive RTP packets from the server, on port RTP_RCV_PORT
                    //RTPsocket = ...
                    RTPsocket = new DatagramSocket(RTP_RCV_PORT);

                    //set TimeOut value of the socket to 5msec.
                    //....
                    RTPsocket.setSoTimeout(5);

                } catch (Exception se) {
                    System.out.println("Socket exception: " + se);
                    //System.exit(0);
                }

                //init RTSP sequence number
                RTSPSeqNb = 1;

                //Send SETUP message to the server
                send_RTSP_request("SETUP");

                //Wait for the response
                if (parse_server_response() != 200) {
                    System.out.println("Invalid Server Response");
                } else {
                    //change RTSP state and print new state
                    state = READY;
                    System.out.println("New RTSP state: READY");
                }
            }//else if state != INIT then do nothing
        }


        //Handler for Play button
        //-----------------------


        public void playButtonListener() {

            //System.out.println("Play Button pressed !");
            if (state == READY) {
                //increase RTSP sequence number
                RTSPSeqNb++;

                //Send PLAY message to the server
                send_RTSP_request("PLAY");

                //Wait for the response
                if (parse_server_response() != 200) {
                    System.out.println("Invalid Server Response");
                } else {
                    //change RTSP state and print out new state
                    //.....
                    System.out.println("New RTSP state: PLAYING");
                    state = PLAYING;
                    //start the timer
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            timerAction();
                        }
                    }, 0, 20);
                }
            }//else if state != READY then do nothing
        }


        //Handler for Pause button
        //-----------------------


        public void pauseButtonListener() {

            //System.out.println("Pause Button pressed !");
            if (state == PLAYING) {
                //increase RTSP sequence number
                RTSPSeqNb++;

                //Send PAUSE message to the server
                send_RTSP_request("PAUSE");

                //Wait for the response
                if (parse_server_response() != 200) {
                    System.out.println("Invalid Server Response");
                } else {
                    //change RTSP state and print out new state
                    //........
                    System.out.println("New RTSP state: READY");
                    state = READY;
                    //stop the timer
                    timer.cancel();
                }
            }
            //else if state != PLAYING then do nothing
        }


        //Handler for Teardown button
        //-----------------------


        public void tearButtonListener() {

            //System.out.println("Teardown Button pressed !");
            //increase RTSP sequence number
            // ..........
            RTSPSeqNb++;

            //Send TEARDOWN message to the server
            send_RTSP_request("TEARDOWN");

            //Wait for the response
            if (parse_server_response() != 200) {
                System.out.println("Invalid Server Response");
            } else {
                //change RTSP state and print out new state
                //........
                //System.out.println("New RTSP state: ...");
                state = INIT;
                //stop the timer
                timer.cancel();

                //exit
                System.exit(0);
            }
        }


        //------------------------------------
        //Handler for timer
        //------------------------------------


        public void timerAction() {

            //Construct a DatagramPacket to receive data from the UDP socket
            rcvdp = new DatagramPacket(buf, buf.length);

            try {
                //receive the DP from the socket:
                RTPsocket.receive(rcvdp);

                //create an RTPpacket object from the DP
                RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

                //print important header fields of the RTP packet received:
                System.out.println("Got RTP packet with SeqNum # " + rtp_packet.getsequencenumber() + " TimeStamp " + rtp_packet.gettimestamp() + " ms, of type " + rtp_packet.getpayloadtype());

                //print header bitstream:
                rtp_packet.printheader();

                //get the payload bitstream from the RTPpacket object
                int payload_length = rtp_packet.getpayload_length();
                byte[] payload = new byte[payload_length];
                rtp_packet.getpayload(payload);


                final Bitmap bmp = BitmapFactory.decodeByteArray(payload, 0, payload_length);
                // ImageView image=new ImageView(getBaseContext());
                // image.setImageBitmap(bmp);

                runOnUiThread(new Runnable() {
                    public void run() {
                        changeImageView(bmp);
                    }
                });


            } catch (InterruptedIOException iioe) {
                //System.out.println("Nothing to read");
            } catch (IOException ioe) {
                System.out.println("Exception caught: " + ioe);
            }
        }

        //------------------------------------
        //Parse Server Response
        //------------------------------------
        private int parse_server_response() {
            int reply_code = 0;

            try {
                //parse status line and extract the reply_code:
                String StatusLine = RTSPBufferedReader.readLine();
                //System.out.println("RTSP Client - Received from Server:");
                System.out.println(StatusLine);

                StringTokenizer tokens = new StringTokenizer(StatusLine);
                tokens.nextToken(); //skip over the RTSP version
                reply_code = Integer.parseInt(tokens.nextToken());

                //if reply code is OK get and print the 2 other lines
                if (reply_code == 200) {
                    String SeqNumLine = RTSPBufferedReader.readLine();
                    System.out.println(SeqNumLine);

                    String SessionLine = RTSPBufferedReader.readLine();
                    System.out.println(SessionLine);

                    //if state == INIT gets the Session Id from the SessionLine
                    tokens = new StringTokenizer(SessionLine);
                    tokens.nextToken(); //skip over the Session:
                    RTSPid = Integer.parseInt(tokens.nextToken());
                }
            } catch (Exception ex) {
                System.out.println("Exception caught: " + ex);
                //System.exit(0);
            }

            return (reply_code);
        }

        //------------------------------------
        //Send RTSP Request
        //------------------------------------
        //.............
        //TO COMPLETE
        //.............
        private void send_RTSP_request(String request_type) {
            try {
                //Use the RTSPBufferedWriter to write to the RTSP socket

                //write the request line:
                RTSPBufferedWriter.write(request_type + " " + VideoFileName + " RTSP/1.0" + CRLF);

                //write the CSeq line:
                RTSPBufferedWriter.write("CSeq: " + RTSPSeqNb + CRLF);

                //check if request_type is equal to "SETUP" and in this case write the Transport: line advertising to the server the port used to receive the RTP packets RTP_RCV_PORT
                //otherwise, write the Session line from the RTSPid field
                if (request_type.equals("SETUP")) {
                    RTSPBufferedWriter.write("Transport: RTP/UDP; client_port= " + RTP_RCV_PORT + CRLF);
                } else {
                    RTSPBufferedWriter.write("Session: " + RTSPid + CRLF);
                }
                RTSPBufferedWriter.flush();
            } catch (Exception ex) {
                System.out.println("Exception caught: " + ex);
               // System.exit(0);
            }
        }

    }//end of Class Client
}
