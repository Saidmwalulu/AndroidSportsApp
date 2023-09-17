package com.sportcityapp.sportsapp.models;

public class Video {
    private int id, user_id;
    private String video_category, video_desc, video_id;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getVideo_category() {
        return video_category;
    }

    public void setVideo_category(String video_category) {
        this.video_category = video_category;
    }

    public String getVideo_desc() {
        return video_desc;
    }

    public void setVideo_desc(String video_desc) {
        this.video_desc = video_desc;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
