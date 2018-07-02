package com.zjut.jesse.gotogoal;

/**
 * Created by Jesse-PC on 2018/6/28.
 */

public class Message {

    private String author, time, content;
    private int heart_num, id;
    private boolean isLiked=false;

    public Message(int id, String author, String time, String content, int heart_num) {
        this.author = author;
        this.time = time;
        this.content = content;
        this.heart_num = heart_num;
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return  content;
    }

    public int getId() {
        return id;
    }

    public int getHeartNum() {
        return heart_num;
    }

    public boolean getHeart() {
        return isLiked;
    }

    public void setHeartNum(int num) {
        heart_num = num;
    }

    public void setHeart() {
        if (!isLiked) {
            isLiked = true;
        }
    }

}
