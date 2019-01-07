package com.maeultalk.gongneungtalk.contents.networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadSpots extends Thread {

    String urls;
    String str;

    public LoadSpots(String url, String spot_name) {
        /*urls = url;
        this.theNumberOfContents = theNumberOfContents;
        this.contentNoOfNextLoad = contentNoOfNextLoad;*/
        urls = url + "search_spots.php?spot_name=" + spot_name;
    }

    public String getStr() {
        return str;
    }

    public void run() {

        StringBuilder jsonHtml = new StringBuilder();

        try {

            // 연결 url 설정
            URL url = new URL(urls/* + "?number_of_content=" + String.valueOf(theNumberOfContents) + "&content_no_of_next=" + String.valueOf(contentNoOfNextLoad)*/);

            // 커넥션 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 연결되었으면.
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);

                // 연결되었음 코드가 리턴되면.
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                    for (; ; ) {

                        // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                        String line = br.readLine();

                        if (line == null) break;

                        // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                        jsonHtml.append(line + "\n");

                    }

                    br.close();

                }

                conn.disconnect();

            }

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        str = jsonHtml.toString();

    }

}