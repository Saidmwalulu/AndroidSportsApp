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
import com.sportcityapp.sportsapp.Adapters.VideoAdapter;
import com.sportcityapp.sportsapp.Constant;
import com.sportcityapp.sportsapp.R;
import com.sportcityapp.sportsapp.models.User;
import com.sportcityapp.sportsapp.models.Video;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerViewVideo;
    public static ArrayList<Video> arrayListVideo;
    private SwipeRefreshLayout refreshLayout;
    private VideoAdapter videoAdapter;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;
    private ShimmerFrameLayout frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        recyclerViewVideo = view.findViewById(R.id.recycler_videos);
        recyclerViewVideo.setHasFixedSize(true);
        frameLayout = view.findViewById(R.id.shimmer_video);
        frameLayout.startShimmer();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerViewVideo.setLayoutManager(manager);

        refreshLayout = view.findViewById(R.id.swipe_video);
        toolbar = view.findViewById(R.id.toolbar_video);


        getVideos();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getVideos();
            }
        });
    }

    private void getVideos() {
        arrayListVideo = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        String token = sharedPreferences.getString("token", "");

        AndroidNetworking.get(Constant.VIDEOS)
                .addHeaders("Authorization", "Bearer "+token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("videos"));
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject videosObject = array.getJSONObject(i);
                                    JSONObject userObject = videosObject.getJSONObject("user");

                                    User user = new User();
                                    user.setId(userObject.getInt("id"));
                                    user.setProfile_image_url(userObject.getString("profile_image_url"));
                                    user.setName(userObject.getString("name"));

                                    Video video = new Video();

                                    video.setId(videosObject.getInt("id"));
                                    video.setUser_id(videosObject.getInt("user_id"));
                                    video.setVideo_category(videosObject.getString("video_category"));
                                    video.setVideo_desc(videosObject.getString("video_desc"));
                                    video.setVideo_id(videosObject.getString("video_id"));

                                    arrayListVideo.add(video);
                                }
                                frameLayout.stopShimmer();
                                frameLayout.setVisibility(View.GONE);
                                recyclerViewVideo.setVisibility(View.VISIBLE);
                                videoAdapter = new VideoAdapter(getContext(), arrayListVideo);
                                recyclerViewVideo.setAdapter(videoAdapter);
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