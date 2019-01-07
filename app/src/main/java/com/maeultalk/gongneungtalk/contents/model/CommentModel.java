package com.maeultalk.gongneungtalk.contents.model;

public class CommentModel {
    private String no;
    private String comment;

    public CommentModel(String no, String comment) {
        this.no = no;
        this.comment = comment;
    }

    public String getNo() {
        return no;
    }

    public String getComment() {
        return comment;
    }
}