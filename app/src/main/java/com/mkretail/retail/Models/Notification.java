package com.mkretail.retail.Models;

public class Notification {

    public String title;
    public String content;

    public Notification(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Notification() {
    }

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
}
