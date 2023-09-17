package com.sportcityapp.sportsapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.Adapters.FixtureAdapter;
import com.sportcityapp.sportsapp.Constant;
import com.sportcityapp.sportsapp.R;
import com.sportcityapp.sportsapp.models.Fixture;
import com.sportcityapp.sportsapp.models.User;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FixtureFragment extends Fragment {

    private View view;
    public static RecyclerView recyclerViewFixture;
    public static ArrayList<Fixture> arrayListFixture;
    private SwipeRefreshLayout refreshLayout;
    private FixtureAdapter fixtureAdapter;
    private SharedPreferences preferences;
    private ShimmerFrameLayout frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fixture, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        init();

        return view;
    }

    private void init() {
        preferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        recyclerViewFixture = view.findViewById(R.id.recycler_fixture);
        recyclerViewFixture.setHasFixedSize(true);
        frameLayout = view.findViewById(R.id.shimmer_fixture);
        frameLayout.startShimmer();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewFixture.setLayoutManager(layoutManager);

        refreshLayout = view.findViewById(R.id.swipe_fixture);

        getFixtures();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFixtures();
            }
        });
    }

    private void getFixtures() {
        arrayListFixture = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        String token = preferences.getString("token", "");

        AndroidNetworking.get(Constant.FIXTURES)
                .addHeaders("Authorization", "Bearer "+token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("fixtures"));
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject fixturesObject = array.getJSONObject(i);
                                    JSONObject userObject = fixturesObject.getJSONObject("user");

                                    User user = new User();
                                    user.setId(userObject.getInt("id"));

                                    Fixture fixture = new Fixture();

                                    fixture.setId(fixturesObject.getInt("id"));
                                    fixture.setUser_id(fixturesObject.getInt("user_id"));
                                    fixture.setSponsor_logo(fixturesObject.getString("sponsor_logo"));
                                    fixture.setSponsor_name(fixturesObject.getString("sponsor_name"));
                                    fixture.setDate(fixturesObject.getString("date"));
                                    fixture.setTime(fixturesObject.getString("time"));
                                    fixture.setVenue(fixturesObject.getString("venue"));
                                    fixture.setTeam_a_name(fixturesObject.getString("team_a_name"));
                                    fixture.setTeam_b_name(fixturesObject.getString("team_b_name"));

                                    arrayListFixture.add(fixture);
                                }

                                frameLayout.stopShimmer();
                                frameLayout.setVisibility(View.GONE);
                                recyclerViewFixture.setVisibility(View.VISIBLE);
                                fixtureAdapter = new FixtureAdapter(getContext(), arrayListFixture);
                                recyclerViewFixture.setAdapter(fixtureAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "network unavailable, please try again", Toast.LENGTH_LONG).show();
                    }
                });
    }
}