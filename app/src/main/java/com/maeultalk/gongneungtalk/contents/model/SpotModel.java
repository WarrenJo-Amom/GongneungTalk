package com.maeultalk.gongneungtalk.contents.model;

public class SpotModel {
    private String no;
    private String spot;
    private String spot_code;

    public SpotModel(String no, String spot, String spot_code) {
        this.no = no;
        this.spot = spot;
        this.spot_code = spot_code;
    }

    public String getNo() {
        return no;
    }

    public String getSpot() {
        return spot;
    }

    public String getSpot_code() {
        return spot_code;
    }
}