package com.maeultalk.gongneungtalk.contents.model;

public class ContentModel {
    private String no;
    private String spot;
    private String spot_code;
    private String content;
    private String image;
    private String nick;
    private String identity;
    private String time;
    private String comments;

    public ContentModel(String no, String spot, String spot_code, String content, String image, String nick, String identity, String time, String comments) {
        this.no = no;
        this.spot = spot;
        this.spot_code = spot_code;
        this.content = content;
        this.image = image;
        this.nick = nick;
        this.identity = identity;
        this.time = time;
        this.comments = comments;
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

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public String getNick() {
        return nick;
    }

    public String getIdentity() {
        return identity;
    }

    public String getTime() {
        return time;
    }

    public String getComments() {
        return comments;
    }
}