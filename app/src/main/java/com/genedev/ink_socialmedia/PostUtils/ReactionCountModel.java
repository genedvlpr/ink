package com.genedev.ink_socialmedia.PostUtils;

public class ReactionCountModel {

    private String like_reaction, haha_reaction, heart_reaction, cry_reaction, angry_reaction, wow_reaction;

    public ReactionCountModel(){

    }

    public ReactionCountModel(String like_reaction, String haha_reaction, String heart_reaction, String cry_reaction, String angry_reaction, String wow_reaction) {
        this.like_reaction = like_reaction;
        this.haha_reaction = haha_reaction;
        this.heart_reaction = heart_reaction;
        this.cry_reaction = cry_reaction;
        this.angry_reaction = angry_reaction;
        this.wow_reaction = wow_reaction;
    }

    public String getLike_reaction() {
        return like_reaction;
    }

    public void setLike_reaction(String like_reaction) {
        this.like_reaction = like_reaction;
    }

    public String getHaha_reaction() {
        return haha_reaction;
    }

    public void setHaha_reaction(String haha_reaction) {
        this.haha_reaction = haha_reaction;
    }

    public String getHeart_reaction() {
        return heart_reaction;
    }

    public void setHeart_reaction(String heart_reaction) {
        this.heart_reaction = heart_reaction;
    }

    public String getCry_reaction() {
        return cry_reaction;
    }

    public void setCry_reaction(String cry_reaction) {
        this.cry_reaction = cry_reaction;
    }

    public String getAngry_reaction() {
        return angry_reaction;
    }

    public void setAngry_reaction(String angry_reaction) {
        this.angry_reaction = angry_reaction;
    }

    public String getWow_reaction() {
        return wow_reaction;
    }

    public void setWow_reaction(String wow_reaction) {
        this.wow_reaction = wow_reaction;
    }
}
