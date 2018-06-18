package rodischeele.practicumfinal.enities;

import com.gj_webdev.communicatie.practicum_final.logic.ClientManager;
import com.gj_webdev.communicatie.practicum_final.logic.ServerManager;

/**
 * Created by steven on 7-6-2015.
 */
public class Globals {

    public static final String HOST = "http://47310012.00x12.eu";

    public static final String URL_LIST = HOST + "/streamers";
    public static final String URL_REGISTER = HOST + "/register";
    public static final String URL_UNREGISTER = HOST + "/unregister";

    public static final int TCP_PORT = 9999;
    public static final int UDP_PORT = 9998;

    /** FPS for the video stream (30 FPS) */
    public static final int FRAME_RATE = 1000 / 30;

    public static final ServerManager SERVER_MANAGER = new ServerManager();
    public static final ClientManager CLIENT_MANAGER = new ClientManager();

    public static final String REQUEST_CONNECT = "CONNECT";
    public static final String REQUEST_SETUP = "SETUP";
    public static final String REQUEST_PLAY = "PLAY";
    public static final String REQUEST_PAUSE = "PAUSE";
    public static final String REQUEST_TEARDOWN = "TEARDOWN";

}
