package com.genedev.ink_socialmedia.StoriesUtil;

public class Stories {

    String user_id, first_name, last_name, date, time, caption, ink_color, ink_style;

    public Stories(){

    }

    public Stories(String user_id, String first_name, String last_name, String date, String time, String caption, String ink_color, String ink_style) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.date = date;
        this.time = time;
        this.caption = caption;
        this.ink_color = ink_color;
        this.ink_style = ink_style;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getInk_color() {
        return ink_color;
    }

    public void setInk_color(String ink_color) {
        this.ink_color = ink_color;
    }

    public String getInk_style() {
        return ink_style;
    }

    public void setInk_style(String ink_style) {
        this.ink_style = ink_style;
    }
}
