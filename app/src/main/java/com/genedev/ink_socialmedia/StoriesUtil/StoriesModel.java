package com.genedev.ink_socialmedia.StoriesUtil;

public class StoriesModel {
    String user_id, first_name, last_name, moments_photo, img_profile, caption;

    public StoriesModel(){

    }

    public StoriesModel(String user_id, String first_name, String last_name, String moments_photo, String img_profile, String caption) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.moments_photo = moments_photo;
        this.img_profile = img_profile;
        //this.timestamp = timestamp;
        this.caption = caption;
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

    public String getMoments_photo() {
        return moments_photo;
    }

    public void setMoments_photo(String moments_photo) {
        this.moments_photo = moments_photo;
    }

    public String getImg_profile() {
        return img_profile;
    }

    public void setImg_profile(String img_profile) {
        this.img_profile = img_profile;
    }

    //public String getTimestamp() {
        //return timestamp;
    //}

    //public void setTimestamp(String timestamp) {
        //this.timestamp = timestamp;
    //}

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
