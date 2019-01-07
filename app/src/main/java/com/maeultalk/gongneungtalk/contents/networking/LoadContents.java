package com.maeultalk.gongneungtalk.contents.networking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadContents extends Thread {

    String urls;
    String str;
    int theNumberOfContents;
    int contentNoOfNextLoad;

    public LoadContents(String url, int theNumberOfContents, int contentNoOfNextLoad) {
        /*urls = url;
        this.theNumberOfContents = theNumberOfContents;
        this.contentNoOfNextLoad = contentNoOfNextLoad;*/
        urls = url + "load_contents_limit.php?number_of_content=" + String.valueOf(theNumberOfContents) + "&content_no_of_next=" + String.valueOf(contentNoOfNextLoad);
    }

    public LoadContents(String url, int theNumberOfContents, int contentNoOfNextLoad, String spot_code) {
        /*urls = url;
        this.theNumberOfContents = theNumberOfContents;
        this.contentNoOfNextLoad = contentNoOfNextLoad;*/
        urls = url + "load_contents_limit_where_spot.php?spot_code=" + spot_code + "&number_of_content=" + String.valueOf(theNumberOfContents) + "&content_no_of_next=" + String.valueOf(contentNoOfNextLoad);
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

        /*String category;
        String img;


        try {


            JSONObject root = new JSONObject(str);

            JSONArray ja = root.getJSONArray("results");

            //row_num = root.getString("num_results");
            row_num_int = ja.length();


            for (int i = 0; i < ja.length(); i++) {

                JSONObject jo = ja.getJSONObject(i);

                //row_num = jo.getString("num_results");

                    *//*imgurl = jo.getString("title").replaceAll("&quot;", "\"");

                    txt1 = jo.getString("descr").replaceAll("&quot;", "\"");*//*

                category = jo.getString("contents");
                img = jo.getString("img");

                //categoryItems.add(new CategoryItem(category));
                spotCons.add(new SpotCon(category, img));

            }


        } catch (JSONException e) {

            e.printStackTrace();

        }*/


    }

}