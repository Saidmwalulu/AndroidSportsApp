package com.sportcityapp.sportsapp.models;

public class Fixture {
    private int id, user_id;
    String sponsor_logo, sponsor_name, date, time, venue, team_a_name, team_b_name;
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

    public String getSponsor_logo() {
        return sponsor_logo;
    }

    public void setSponsor_logo(String sponsor_logo) {
        this.sponsor_logo = sponsor_logo;
    }

    public String getSponsor_name() {
        return sponsor_name;
    }

    public void setSponsor_name(String sponsor_name) {
        this.sponsor_name = sponsor_name;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getTeam_a_name() {
        return team_a_name;
    }

    public void setTeam_a_name(String team_a_name) {
        this.team_a_name = team_a_name;
    }

    public String getTeam_b_name() {
        return team_b_name;
    }

    public void setTeam_b_name(String team_b_name) {
        this.team_b_name = team_b_name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
