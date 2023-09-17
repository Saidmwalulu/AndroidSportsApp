package com.sportcityapp.sportsapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.Adapters.CommentAdapter;
import com.sportcityapp.sportsapp.Fragments.HomeFragment;
import com.sportcityapp.sportsapp.models.Comment;
import com.sportcityapp.sportsapp.models.Post;
import com.sportcityapp.sportsapp.models.User;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewC;
    private ArrayList<Comment> list;
    private CommentAdapter commentAdapter;
    private EditText editTextComment;
    private Button btnAddComment;
    private SharedPreferences sharedPreferences;
    private ShimmerFrameLayout frameLayout;
    private ProgressDialog dialog;
    private int postId = 0;
    public static int postPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        init();

    }

    private void init() {
        postPosition = getIntent().getIntExtra("postPosition", -1);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerViewC = findViewById(R.id.recycler_comments);
        recyclerViewC.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewC.setHasFixedSize(true);
        frameLayout = findViewById(R.id.shimmer_comment);
        frameLayout.startShimmer();
        editTextComment = findViewById(R.id.editTextComment);
        btnAddComment = findViewById(R.id.btnAddComment);
        postId = getIntent().getIntExtra("postId", 0);

        getComments();
    }

    private void getComments() {
        list = new ArrayList<>();

        String token = sharedPreferences.getString("token", "");

        AndroidNetworking.post(Constant.COMMENTS)
                .addHeaders("Authorization", "Bearer "+token)
                .addBodyParameter("id", postId+"")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray comments = new JSONArray(object.getString("comments"));
                                for (int i = 0; i < comments.length(); i++) {
                                    JSONObject comment = comments.getJSONObject(i);
                                    JSONObject user = comment.getJSONObject("user");

                                    User mUser = new User();
                                    mUser.setId(user.getInt("id"));
                                    mUser.setProfile_image_url(user.getString("profile_image_url"));
                                    mUser.setName(user.getString("name"));

                                    Comment mComment = new Comment();
                                    mComment.setId(comment.getInt("id"));
                                    mComment.setUser(mUser);
                                    mComment.setDate(comment.getString("created_at"));
                                    mComment.setComment(comment.getString("comment"));
                                    list.add(mComment);
                                }
                                frameLayout.stopShimmer();
                                frameLayout.setVisibility(View.GONE);
                                recyclerViewC.setVisibility(View.VISIBLE);
                                commentAdapter = new CommentAdapter(getApplicationContext(), list);
                                recyclerViewC.setAdapter(commentAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Log.e(TAG, "onError: ", anError.fillInStackTrace());
                        Log.d(TAG, "onError response " + anError.getResponse());
                        Toast.makeText(CommentActivity.this, "network unavailable, please try again", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    public void addComment(View view) {
        String edComments = editTextComment.getText().toString();
        dialog.setMessage("Adding comment..");
        dialog.show();
        if (edComments.length() > 0) {

            String token = sharedPreferences.getString("token", "");

            AndroidNetworking.post(Constant.ADD_COMMENTS)
                    .addHeaders("Authorization", "Bearer "+token)
                    .addBodyParameter("id", postId+"")
                    .addBodyParameter("comment", edComments)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.getBoolean("success")) {
                                    JSONObject comment = object.getJSONObject("comment");
                                    JSONObject user = comment.getJSONObject("user");

                                    Comment comm = new Comment();
                                    User us = new User();

                                    us.setId(user.getInt("id"));
                                    us.setName(user.getString("name"));
                                    us.setProfile_image_url(user.getString("profile_image_url"));

                                    comm.setUser(us);
                                    comm.setId(comment.getInt("id"));
                                    comm.setDate(comment.getString("created_at"));
                                    comm.setComment(comment.getString("comment"));

                                    Post post = HomeFragment.arrayList.get(postPosition);
                                    post.setComments(post.getComments()+1);
                                    HomeFragment.arrayList.set(postPosition, post);
                                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                                    list.add(comm);
                                    recyclerViewC.getAdapter().notifyDataSetChanged();
                                    editTextComment.setText("");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(ANError anError) {
                            dialog.dismiss();
                        }
                    });
        }
    }
}