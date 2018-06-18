package com.perryfaro.android2;

import android.content.res.AssetManager;
import android.net.Uri;

import java.io.InputStream;

public class VideoStream {

    InputStream fis; //video file
    int frame_nb; //current frame nb

    //-----------------------------------
    //constructor
    //-----------------------------------
    public VideoStream(AssetManager am, String filename) throws Exception{



        //init variables
        Uri path = Uri.parse("assets://" + filename);

        String newPath = path.toString();
        //filename = "assets://" + filename;


        fis = am.open(filename);


       // fis = new FileInputStream(filename);
        frame_nb = 0;
    }

    //-----------------------------------
    // getnextframe
    //returns the next frame as an array of byte and the size of the frame
    //-----------------------------------
    public int getnextframe(byte[] frame) throws Exception
    {
        int length = 0;
        String length_string;
        byte[] frame_length = new byte[5];

        //read current frame length
        fis.read(frame_length,0,5);

        //transform frame_length to integer
        length_string = new String(frame_length);
        length = Integer.parseInt(length_string);

        return(fis.read(frame,0,length));
    }
}