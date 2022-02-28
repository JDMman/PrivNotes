package com.example.privnotes;

import java.util.Comparator;

public class firebasemodel {

    private String title;
    private String content;
    private String bgColor;
    private String date;

    public firebasemodel()
    {

    }

    public firebasemodel(String title, String content, String bgColor, String date) {
        this.title = title;
        this.content = content;
        this.bgColor = bgColor;
        this.date = date;
    }
    /* public firebasemodel(String title,String content, String bgColor)
    {
        this.title=title;
        this.content=content;
        this.bgColor=bgColor;
    }*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}


