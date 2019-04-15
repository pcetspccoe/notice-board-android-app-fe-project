package com.pccoedevelopers.noticeboard;

public class DataInputStream {
    public String description, id, image, title,image_name,thumbnail_path;

    public String getImage_name() {
        return image_name;
    }

    public String getThumbnail_path() {
        return thumbnail_path;
    }

    public void setThumbnail_path(String thumbnail_path) {
        this.thumbnail_path = thumbnail_path;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public DataInputStream() {}

    public DataInputStream(String description, String id, String image, String title,String image_name,String thumbnail_path) {
        this.description = description;
        this.id = id;
        this.image = image;
        this.title = title;
        this.image_name=image_name;
        this.thumbnail_path=thumbnail_path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
