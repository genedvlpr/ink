package com.genedev.ink_socialmedia.Utils;

import java.util.ArrayList;

public class AccountInfo {

    private String user_id;
    private String address;
    private String age;
    private String birthday;
    private String country_code;
    private String email;
    private String first_name;
    private String middle_name;
    private String last_name;
    private String status;
    private String img_profile;
    private String img_cover;
    private ArrayList<String> interests = new ArrayList<String>();

    public AccountInfo() {}

    public AccountInfo(String user_id, String address, String age, String birthday, String country_code, String email, String first_name, String middle_name, String last_name, String status, String img_profile, String img_cover, ArrayList<String> interests) {
        this.user_id = user_id;
        this.address = address;
        this.age = age;
        this.birthday = birthday;
        this.country_code = country_code;
        this.email = email;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.status = status;
        this.img_profile = img_profile;
        this.img_cover = img_cover;
        this.interests = interests;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImg_profile() {
        return img_profile;
    }

    public void setImg_profile(String img_profile) {
        this.img_profile = img_profile;
    }

    public String getImg_cover() {
        return img_cover;
    }

    public void setImg_cover(String img_cover) {
        this.img_cover = img_cover;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }
}