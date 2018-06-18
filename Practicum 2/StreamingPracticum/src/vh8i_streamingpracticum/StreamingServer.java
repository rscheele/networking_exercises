/* ------------------
   StreamingServer
   usage: java StreamingServer [RTSP listening port]
   ---------------------- */

package vh8i_streamingpracticum;


import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class StreamingServer extends JFrame implements ActionListener {

    //RTP variables:
    //----------------
    DatagramSocket RTPsocket; // socket to be used to send and receive UDP packets
    DatagramPacket senddp; // UDP packet containing the video frames

    InetAddress clientIpAddr; // Client IP address
    int rtpDestPort = 0; // destination port for RTP packets  (given by the RTSP Client)

    //GUI:
    //----------------
    JLabel label;

    //Video variables:
    //----------------
    int imagenb = 0; // image nb of the image currently transmitted
    VideoStream video; // VideoStream object used to access video frames
    static int MJPEG_TYPE = 26; // RTP payload type for MJPEG video
    static int FRAME_PERIOD = 100; // Frame period of the video to stream, in ms
    static int VIDEO_LENGTH = 500; // length of the video in frames

    Timer timer; // timer used to send the images at the video frame rate
    byte[] buf; // buffer used to store the images to send to the client

    //RTSP variables
    //----------------
    //rtsp states
    final static int INIT = 0;
    final static int READY = 1;
    final static int PLAYING = 2;
    //rtsp message types
    final static int SETUP = 3;
    final static int PLAY = 4;
    final static int PAUSE = 5;
    final static int TEARDOWN = 6;

    static int state; //RTSP StreamingServer state == INIT or READY or PLAY
    Socket rtspSocket; //socket used to send/receive RTSP messages
    //input and output stream filters
    static BufferedReader rtspBufferedReader;
    static BufferedWriter rtspBufferedWriter;
    static String VideoFileName; //video file requested from the client
    static int RTSP_ID = 123456; //ID of the RTSP session
    int rtspSeqNb = 0; //Sequence number of RTSP messages within the session

    final static String CRLF = "\r\n";

    //--------------------------------
    //Constructor
    //--------------------------------
    public StreamingServer() {

        // init Frame
        super("Server");

        // init Timer
        timer = new Timer(FRAME_PERIOD, this);
        timer.setInitialDelay(0);
        timer.setCoalesce(true);

        // allocate memory for the sending buffer
        buf = new byte[15000];

        // Handler to close the main window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //stop the timer and exit
                timer.stop();
                System.exit(0);
            }
        });

        //GUI:
        label = new JLabel("Send frame #        ", JLabel.CENTER);
        getContentPane().add(label, BorderLayout.CENTER);
    }

    //------------------------------------
    //main
    //------------------------------------
    public static void main(String argv[]) throws Exception {
        //create a StreamingServer object
        StreamingServer theStreamingServer = new StreamingServer();

        //show GUI:
        theStreamingServer.pack();
        theStreamingServer.setVisible(true);

        //get RTSP socket port from the command line
        int RTSPport = Integer.parseInt(argv[0]);

        //Initiate TCP connection with the client for the RTSP session
        ServerSocket listenSocket = new ServerSocket(RTSPport);
        theStreamingServer.rtspSocket = listenSocket.accept();
        listenSocket.close();

        //Get Client IP address
        theStreamingServer.clientIpAddr = theStreamingServer.rtspSocket.getInetAddress();

        //Initiate RTSPstate
        state = INIT;

        //Set input and output stream filters:
        rtspBufferedReader = new BufferedReader(new InputStreamReader(theStreamingServer.rtspSocket.getInputStream()));
        rtspBufferedWriter = new BufferedWriter(new OutputStreamWriter(theStreamingServer.rtspSocket.getOutputStream()));

        //Wait for the SETUP message from the client
        int request_type;
        boolean done = false;
        while (!done) {
            request_type = theStreamingServer.parse_RTSP_request(); //blocking

            if (request_type == SETUP) {
                done = true;

                //update RTSP state
                state = READY;
                System.out.println("(streamingServer) new RTSP state: READY");

                //Send response
                theStreamingServer.send_RTSP_response();

                //init the VideoStream object:
                theStreamingServer.video = new VideoStream(VideoFileName);

                //init RTP socket
                theStreamingServer.RTPsocket = new DatagramSocket();
            }
        }

        //loop to handle RTSP requests
        while (true) {
            //parse the request
            request_type = theStreamingServer.parse_RTSP_request(); //blocking

            if ((request_type == PLAY) && (state == READY)) {
                //send back response
                theStreamingServer.send_RTSP_response();
                //start timer
                theStreamingServer.timer.start();
                //update state
                state = PLAYING;
                System.out.println("(streamingServer) New RTSP state: PLAYING");
            } else if ((request_type == PAUSE) && (state == PLAYING)) {
                //send back response
                theStreamingServer.send_RTSP_response();
                //stop timer
                theStreamingServer.timer.stop();
                //update state
                state = READY;
                System.out.println("(streamingServer) New RTSP state: READY");
            } else if (request_type == TEARDOWN) {
                //send back response
                theStreamingServer.send_RTSP_response();
                //stop timer
                theStreamingServer.timer.stop();
                //close sockets
                theStreamingServer.rtspSocket.close();
                theStreamingServer.RTPsocket.close();

                System.exit(0);
            }
        }
    }


    //------------------------
    //Handler for timer
    //------------------------
    public void actionPerformed(ActionEvent e) {
        //if the current image nb is less than the length of the video
        if (imagenb < VIDEO_LENGTH) {
            //update current imagenb
            imagenb++;

            try {
                //get next frame to send from the video, as well as its size
                int image_length = video.getnextframe(buf);

                //Builds an RTPpacket object containing the frame
                RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb * FRAME_PERIOD, buf, image_length);

                //get to total length of the full rtp packet to send
                int packet_length = rtp_packet.getlength();

                //retrieve the packet bitstream and store it in an array of bytes
                byte[] packet_bits = new byte[packet_length];
                rtp_packet.getpacket(packet_bits);

                //send the packet as a DatagramPacket over the UDP socket
                senddp = new DatagramPacket(packet_bits, packet_length, clientIpAddr, rtpDestPort);
                RTPsocket.send(senddp);

                //System.out.println("Send frame #"+imagenb);
                //print the header bitstream
                rtp_packet.printheader();

                //update GUI
                label.setText("Send frame #" + imagenb);
            } catch (Exception ex) {
                System.out.println("Exception caught: " + ex);
                ex.printStackTrace();
                System.exit(0);
            }
        } else {
            //if we have reached the end of the video file, stop the timer
            timer.stop();
        }
    }

    //------------------------------------
    //Parse RTSP Request
    //------------------------------------
    private int parse_RTSP_request() {
        int request_type = -1;
        try {
            //parse request line and extract the request_type:
            String RequestLine = rtspBufferedReader.readLine();
            System.out.println("(streamingServer) received from client (request line): " + RequestLine);

            StringTokenizer tokens = new StringTokenizer(RequestLine);
            String request_type_string = tokens.nextToken();

            //convert to request_type structure:
            if ((new String(request_type_string)).compareTo("SETUP") == 0)
                request_type = SETUP;
            else if ((new String(request_type_string)).compareTo("PLAY") == 0)
                request_type = PLAY;
            else if ((new String(request_type_string)).compareTo("PAUSE") == 0)
                request_type = PAUSE;
            else if ((new String(request_type_string)).compareTo("TEARDOWN") == 0)
                request_type = TEARDOWN;

            if (request_type == SETUP) {
                //extract VideoFileName from RequestLine
                VideoFileName = tokens.nextToken();
            }

            //parse the SeqNumLine and extract CSeq field
            String SeqNumLine = rtspBufferedReader.readLine();
            System.out.println("(streamingServer) received from client (seq num line): " + SeqNumLine);
            tokens = new StringTokenizer(SeqNumLine);
            tokens.nextToken();
            rtspSeqNb = Integer.parseInt(tokens.nextToken());

            //get LlstLine
            String lastLine = rtspBufferedReader.readLine();
            System.out.println("(streamingServer) received from client (last line): " + lastLine);

            if (request_type == SETUP) {
                //extract rtpDestPort from LastLine
                tokens = new StringTokenizer(lastLine);
                for (int i = 0; i < 3; i++)
                    tokens.nextToken(); //skip unused stuff
                rtpDestPort = Integer.parseInt(tokens.nextToken());
            }
            //else LastLine will be the SessionId line ... do not check for now.
        } catch (Exception ex) {
            System.out.println("Exception caught: " + ex);
            ex.printStackTrace();
            System.exit(0);
        }
        return (request_type);
    }

    //------------------------------------
    //Send RTSP Response
    //------------------------------------
    private void send_RTSP_response() {
        try {
            rtspBufferedWriter.write("RTSP/1.0 200 OK" + CRLF);
            rtspBufferedWriter.write("CSeq: " + rtspSeqNb + CRLF);
            rtspBufferedWriter.write("Session: " + RTSP_ID + CRLF);
            rtspBufferedWriter.flush();
            System.out.println("(streamingServer) sent response to client");
        } catch (Exception ex) {
            System.out.println("Exception caught: " + ex);
            ex.printStackTrace();
            System.exit(0);
        }
    }
}