package com.genedev.ink_socialmedia.PostUtils;

import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class PostViewModel {

    private String user_id;
    private String first_name;
    private String last_name;
    private String date;
    private String time;
    private String caption;
    private String likes;
    private String love;
    private String wow;
    private String sad;
    private String angry;
    private String ink_color;
    private String comments;
    private String shares;
    private String ink_align;
    private String sharing_pref;
    private String comments_pref;
    private String img_profile;
    @PropertyName("images")
    private List<String> images;

    //private ArrayList<String> images = new ArrayList<String>();

    public PostViewModel() {
    }

    public PostViewModel(String user_id, String first_name, String last_name, String date, String time, String caption, String post_image, String likes, String love, String wow, String sad, String angry, String ink_color, String comments, String shares, String ink_align, String sharing_pref, String comments_pref, String img_profile, List<String> images) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.date = date;
        this.time = time;
        this.caption = caption;
        this.likes = likes;
        this.love = love;
        this.wow = wow;
        this.sad = sad;
        this.angry = angry;
        this.ink_color = ink_color;
        this.comments = comments;
        this.shares = shares;
        this.ink_align = ink_align;
        this.sharing_pref = sharing_pref;
        this.comments_pref = comments_pref;
        this.img_profile = img_profile;
        this.images = images;
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

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getLove() {
        return love;
    }

    public void setLove(String love) {
        this.love = love;
    }

    public String getWow() {
        return wow;
    }

    public void setWow(String wow) {
        this.wow = wow;
    }

    public String getSad() {
        return sad;
    }

    public void setSad(String sad) {
        this.sad = sad;
    }

    public String getAngry() {
        return angry;
    }

    public void setAngry(String angry) {
        this.angry = angry;
    }

    public String getInk_color() {
        return ink_color;
    }

    public void setInk_color(String ink_color) {
        this.ink_color = ink_color;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getInk_align() {
        return ink_align;
    }

    public void setInk_align(String ink_align) {
        this.ink_align = ink_align;
    }

    public String getSharing_pref() {
        return sharing_pref;
    }

    public void setSharing_pref(String sharing_pref) {
        this.sharing_pref = sharing_pref;
    }

    public String getComments_pref() {
        return comments_pref;
    }

    public void setComments_pref(String comments_pref) {
        this.comments_pref = comments_pref;
    }

    public String getImg_profile() {
        return img_profile;
    }

    public void setImg_profile(String img_profile) {
        this.img_profile = img_profile;
    }

    @PropertyName("images")
    public List<String> getImages(){
        return images;
    }
    @PropertyName("images")
    public void setImages(List<String> images){
        this.images = images;
    }
}
