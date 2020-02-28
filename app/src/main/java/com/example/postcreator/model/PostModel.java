package com.example.postcreator.model;

import java.io.Serializable;

public class PostModel implements Serializable {
    int id;
    String title,size,color;
    byte[] bgImage,postImage;

    public PostModel(int id, String title, String size, String color, byte[] bgImage, byte[] postImage) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.color = color;
        this.bgImage = bgImage;
        this.postImage = postImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public byte[] getBgImage() {
        return bgImage;
    }

    public void setBgImage(byte[] bgImage) {
        this.bgImage = bgImage;
    }

    public byte[] getPostImage() {
        return postImage;
    }

    public void setPostImage(byte[] postImage) {
        this.postImage = postImage;
    }
}
