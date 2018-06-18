package rodischeele.practicumfinal.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steven on 7-6-2015.
 */
public class HttpApi {

    public static String get(String url) throws Exception {
        return get(url, new HashMap<String, String>());
    }

    // HTTP GET request
    public static String get(String url, Map<String, String> params) throws Exception {

        if(!url.contains("?")){
            url += "?";
        }
        for(String key : params.keySet()){
            if(!url.endsWith("?") && !url.endsWith("&")){
                url += "&";
            }
            url += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params.get(key), "UTF-8");
        }
        System.out.println("[GET]: " + url);
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if(!(responseCode >= 200 && responseCode < 300)){
            throw new IOException("Response code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();

    }

    // HTTP POST request
    public static String post(String url, Map<String, String> params) throws Exception {

        String query = "";
        for(String key : params.keySet()){
            if(!query.equals("")){
                query += "&";
            }
            query += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params.get(key), "UTF-8");
        }
        System.out.println("[POST]: " + url);
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        // optional default is GET
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(query);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        if(!(responseCode >= 200 && responseCode < 300)){
            throw new IOException("Response code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();

    }

}
