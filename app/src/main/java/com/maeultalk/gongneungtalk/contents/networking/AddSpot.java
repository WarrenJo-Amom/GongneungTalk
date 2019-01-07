package com.maeultalk.gongneungtalk.contents.networking;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AddSpot extends Thread {

    private String result;
    private String URL;
    private String spot;
    private String spotCode;
    private String content;
    private String img;

    public AddSpot(String url, String inSpot, String inSpotCode) {
        URL = url;
        spot = inSpot;
        spotCode = inSpotCode;
    }

    @Override
    public void run() {
        final String output = request(URL, spot, spotCode);
        result = output;
    }

    public String getResult() {
        return result;
    }

    private String request(String urlStr, String spotStr, String spotCodeStr) {
        StringBuilder output = new StringBuilder();
        try {
            //String nickStr2 = nickStr.replace(" ", "+");
            //URL url = new URL(urlStr + "/user.php?nickname=" + nickStr);
            java.net.URL url = new URL(urlStr + "add_spot.php?spot=" + URLEncoder.encode(spotStr, "utf-8") + "&spot_code=" + URLEncoder.encode(spotCodeStr, "utf-8"));
            //java.net.URL url = new URL(urlStr + "/add_spotContent.php?spot=" + URLEncoder.encode(spotStr, "utf-8") + "&content=" + URLEncoder.encode(contentStr.replace("\"", "\\\""), "utf-8"));
            //URL url = new URL("http://amant.in/user.php?nickname=" + nickStr);
            //URL url = new URL("http://amant.in/user.php?nickname=성공3");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.getResponseCode();
            }
        } catch (Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }

        return output.toString();
    }

}