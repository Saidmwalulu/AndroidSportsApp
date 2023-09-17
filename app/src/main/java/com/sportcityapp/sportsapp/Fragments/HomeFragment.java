package com.sportcityapp.sportsapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.Adapters.PostAdapter;
import com.sportcityapp.sportsapp.Constant;
import com.sportcityapp.sportsapp.R;
import com.sportcityapp.sportsapp.models.Post;
import com.sportcityapp.sportsapp.models.User;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Post> arrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter postAdapter;
    private MaterialToolbar toolbar;
    private CircleImageView profileHome;
    private SharedPreferences sharedPreferences;
    private ShimmerFrameLayout frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recycler_home);
        recyclerView.setHasFixedSize(true);
        frameLayout = view.findViewById(R.id.shimmer_home);
        frameLayout.startShimmer();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = view.findViewById(R.id.swipe_home);
        toolbar = view.findViewById(R.id.toolbar_home);
        profileHome = view.findViewById(R.id.profile_home);

        String profile_image = sharedPreferences.getString("profile_image_url", "");
        Picasso.get().load(profile_image).into(profileHome);

        //((WelcomeActivity)getContext()).setSupportActionBar(toolbar);
        //setHasOptionsMenu(true);

        profileHome.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            bottomSheetFragment.show(getActivity().getSupportFragmentManager(),bottomSheetFragment.getTag());
        });

        
        getPosts();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });

    }

    private void getPosts() {
        arrayList = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);

        String token = sharedPreferences.getString("token", "");

        AndroidNetworking.get(Constant.POSTS)
                .addHeaders("Authorization", "Bearer "+token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("posts"));
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject postsObject = array.getJSONObject(i);
                                    JSONObject userObject = postsObject.getJSONObject("user");

                                    User user = new User();
                                    user.setId(userObject.getInt("id"));
                                    user.setName(userObject.getString("name"));
                                    user.setProfile_image_url(userObject.getString("profile_image_url"));

                                    Post post = new Post();
                                    post.setId(postsObject.getInt("id"));
                                    post.setUser(user);
                                    post.setLikes(postsObject.getInt("likesCount"));
                                    post.setComments(postsObject.getInt("commentsCount"));

                                    //String dateTime = postsObject.getString("created_at"); //*********************//
                                    //post.setDate(dateTime.substring(0,10)+"    "+dateTime.substring(11,16)); //*********************//
                                    post.setDate(postsObject.getString("created_at"));
                                    post.setDesc(postsObject.getString("desc"));
                                    post.setPhoto(postsObject.getString("photo"));
                                    post.setSelfLike(postsObject.getBoolean("selfLike"));

                                    arrayList.add(post);
                                }
                                frameLayout.stopShimmer();
                                frameLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                postAdapter = new PostAdapter(getContext(), arrayList);
                                recyclerView.setAdapter(postAdapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(getContext(), "network unavailable, please try again", Toast.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                postAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}