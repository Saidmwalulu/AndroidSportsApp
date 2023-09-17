package com.sportcityapp.sportsapp.models;

public class AllUsers {
    int id, role;
    private String name, profile_image_url, email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
