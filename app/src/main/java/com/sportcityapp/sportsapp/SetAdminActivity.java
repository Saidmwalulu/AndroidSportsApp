package com.sportcityapp.sportsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.Adapters.AllUsersAdapter;
import com.sportcityapp.sportsapp.models.AllUsers;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SetAdminActivity extends AppCompatActivity {

    public static RecyclerView recyclerViewAllUsers;
    public static ArrayList<AllUsers> arrayAllUsers;
    private SwipeRefreshLayout refreshLayout;
    private AllUsersAdapter allUsersAdapter;
    private TextView usersCount;
    private SharedPreferences preferences;
    private int count;
    private ShimmerFrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_admin);

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        usersCount = findViewById(R.id.usersCount);

        recyclerViewAllUsers = findViewById(R.id.recycler_set_admins);
        recyclerViewAllUsers.setHasFixedSize(true);
        frameLayout = findViewById(R.id.shimmer_all_users);
        frameLayout.startShimmer();

        LinearLayoutManager layoutManager = new LinearLayoutManager(SetAdminActivity.this);
        recyclerViewAllUsers.setLayoutManager(layoutManager);

        refreshLayout = findViewById(R.id.swipe_set_admins);

        getAllUsers();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllUsers();
            }
        });
    }

    private void getAllUsers() {
        arrayAllUsers = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        String token = preferences.getString("token", "");

        AndroidNetworking.get(Constant.ALL_USERS)
                .addHeaders("Authorization", "Bearer "+token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        refreshLayout.setRefreshing(false);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                count = object.getInt("count");
                                usersCount.setText(count+"");
                                //allUsers.setCount(object.getInt("count"));

                                JSONArray array = new JSONArray(object.getString("users"));
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject allUsersObject = array.getJSONObject(i);
                                    AllUsers allUsers = new AllUsers();
                                    //allUsers.setCount(object.getInt("count"));
                                    allUsers.setId(allUsersObject.getInt("id"));
                                    allUsers.setName(allUsersObject.getString("name"));
                                    allUsers.setProfile_image_url(allUsersObject.getString("profile_image_url"));
                                    allUsers.setEmail(allUsersObject.getString("email"));
                                    allUsers.setRole(allUsersObject.getInt("role"));

                                    arrayAllUsers.add(allUsers);
                                }
                                frameLayout.stopShimmer();
                                frameLayout.setVisibility(View.GONE);
                                recyclerViewAllUsers.setVisibility(View.VISIBLE);
                                allUsersAdapter = new AllUsersAdapter(SetAdminActivity.this, arrayAllUsers);
                                recyclerViewAllUsers.setAdapter(allUsersAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(SetAdminActivity.this, "network unavailable, please try again", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}