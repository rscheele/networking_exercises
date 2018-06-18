package rodischeele.practicumfinal;

import com.gj_webdev.communicatie.practicum_final.logic.ServerManager;

/**
 * Created by steven on 7-6-2015.
 */
public class Globals2 {

    public static final String HOST = "http://47310012.00x12.eu";

    public static final String URL_LIST = HOST + "/streamers";
    public static final String URL_REGISTER = HOST + "/register";
    public static final String URL_UNREGISTER = HOST + "/unregister";

    public static final int TCP_PORT = 9999;
    public static final int UDP_PORT = 9998;

    /** FPS for the video stream (30 FPS) */
    public static final int FRAME_RATE = 1000 / 30;

    public static final ServerManager SERVER_MANAGER = new ServerManager();

}
