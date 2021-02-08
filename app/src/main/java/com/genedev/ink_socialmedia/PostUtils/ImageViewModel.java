package com.genedev.ink_socialmedia.PostUtils;

import java.util.ArrayList;
import java.util.List;

public class ImageViewModel {
    private String images;
    private String date;
    private String time;
    private String img_cover;
    private String img_profile;

    public ImageViewModel(){

    }

    public ImageViewModel(String images, String date, String time, String img_cover, String img_profile) {
        this.images = images;
        this.date = date;
        this.time = time;
        this.img_cover = img_cover;
        this.img_profile = img_profile;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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

    public String getImg_cover() {
        return img_cover;
    }

    public void setImg_cover(String img_cover) {
        this.img_cover = img_cover;
    }

    public String getImg_profile() {
        return img_profile;
    }

    public void setImg_profile(String img_profile) {
        this.img_profile = img_profile;
    }
}
