package com.sportcityapp.sportsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import com.sportcityapp.sportsapp.Utility.NetworkChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WelcomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.nav_host_display);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}