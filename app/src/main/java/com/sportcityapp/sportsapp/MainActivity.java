package com.sportcityapp.sportsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this code will pause the app for 2.0 seconds and anything in run method will run
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                boolean isLoggedIn = userPref.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    finish();
                } else {
                    isFirstTime();
                }
            }
        },2000);
    }

    private void isFirstTime() {
        //we need to check if the app is running for the first time
        //therefore we will save the value in shared preference.
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("onBoard", Context.MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);
        //default value is true
        if (isFirstTime) {
            //if is true then its first time and we will change it to false
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            //start OnBoard Activity
            startActivity(new Intent(MainActivity.this, OnBoardActivity.class));
            finish();
        } else {
            //start Login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}