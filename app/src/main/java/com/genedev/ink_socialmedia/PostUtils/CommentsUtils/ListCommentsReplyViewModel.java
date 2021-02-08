package com.genedev.ink_socialmedia.PostUtils.CommentsUtils;

public class ListCommentsReplyViewModel {

    private String user_id, first_name, last_name, img_profile, comment, date, time;

    public ListCommentsReplyViewModel(String user_id, String first_name, String last_name, String img_profile, String comment, String date, String time) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.img_profile = img_profile;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }

    public ListCommentsReplyViewModel(){

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

    public String getImg_profile() {
        return img_profile;
    }

    public void setImg_profile(String img_profile) {
        this.img_profile = img_profile;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
}
