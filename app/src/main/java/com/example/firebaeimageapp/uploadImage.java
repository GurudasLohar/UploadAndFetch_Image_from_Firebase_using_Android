package com.example.firebaeimageapp;

import com.google.firebase.database.Exclude;

public class uploadImage {

    String imageName;
    String imageUrl;

    private String imageKey;

    @Exclude
    public String getImageKey() {
        return imageKey;
    }

    @Exclude
    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public uploadImage(){ }

    public uploadImage(String imageName, String imageUrl) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
