package com.sportcityapp.sportsapp;

public class Constant {

    //Authentication URLs section
    public static final String URL = "http://sportcityapp.co.ke/sports/public/";
    public static final String HOME = URL+"api";
    public static final String LOGIN = HOME+"/login";
    public static final String REGISTER = HOME+"/register";
    public static final String FORGOT_PASSWORD = HOME+"/forgot-password";
    public static final String RESET_PASSWORD = HOME+"/reset-password";

    //Set admins URLs
    public static final String ALL_USERS = HOME+"/all-users";
    public static final String UPDATE_USERS = HOME+"/update-users";

    //Users URLs section
    public static final String USER_PROFILE = HOME+"/user-profile";
    public static final String USER_NAME = HOME+"/user-name";
    public static final String CHANGE_PASSWORD = HOME+"/change-password";
    public static final String LOGOUT = HOME+"/logout";

    //Posts URLs section
    public static final String POSTS = HOME+"/posts";
    public static final String ADD_POST = POSTS+"/create";
    public static final String UPDATE_POST = POSTS+"/update";
    public static final String DELETE_POST = POSTS+"/delete";

    //Likes URLs section
    public static final String LIKE_POST = POSTS+"/like";

    //Comments URLs section
    public static final String COMMENTS = HOME+"/comments";
    public static final String ADD_COMMENTS = COMMENTS+"/create";
    public static final String DELETE_COMMENTS = COMMENTS+"/delete";

    //Videos URLs section
    public static final String VIDEOS = HOME+"/videos";
    public static final String ADD_VIDEO = VIDEOS+"/create";
    public static final String EDIT_VIDEO = VIDEOS+"/edit";
    public static final String DELETE_VIDEO = VIDEOS+"/delete";

    //Fixtures URLs section
    public static final String FIXTURES = HOME+"/fixtures";
    public static final String ADD_FIXTURE = FIXTURES+"/create";
    public static final String EDIT_FIXTURE = FIXTURES+"/edit";
    public static final String DELETE_FIXTURE = FIXTURES+"/delete";
}
