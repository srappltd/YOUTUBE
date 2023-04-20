package com.sandhya.youtube.models;

public class ContentModels {
    String video_title,channel_name,views,date,id,type,publisher,description,video_url,video_tags,video;

    public ContentModels() {
    }

    public ContentModels(String video_title, String channel_name, String views, String date, String id, String type, String publisher, String description, String video_url, String video_tags, String video) {
        this.video_title = video_title;
        this.channel_name = channel_name;
        this.views = views;
        this.date = date;
        this.id = id;
        this.type = type;
        this.publisher = publisher;
        this.description = description;
        this.video_url = video_url;
        this.video_tags = video_tags;
        this.video = video;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url() {
        this.video_url = video_url;
    }

    public String getVideo_tags() {
        return video_tags;
    }

    public void setVideo_tags(String video_tags) {
        this.video_tags = video_tags;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
