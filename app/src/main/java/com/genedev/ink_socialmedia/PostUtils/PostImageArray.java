package com.genedev.ink_socialmedia.PostUtils;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class PostImageArray {

    @PropertyName("images")
    private List<String> images;

    public PostImageArray() {
        // Must have a public no-argument constructor
    }

    // Initialize all fields of a dungeon
    public PostImageArray(List<String> images) {
        this.images = images;
    }

    @PropertyName("images")
    public List<String> getImages() {
        return images;
    }

    @PropertyName("images")
    public void setImages(List<String> images) {
        this.images = images;
    }

}
