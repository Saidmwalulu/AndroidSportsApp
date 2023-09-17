package com.sportcityapp.sportsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.Fragments.VideoFragment;
import com.sportcityapp.sportsapp.models.Video;

import org.json.JSONException;
import org.json.JSONObject;

public class EditVideoActivity extends AppCompatActivity {
    private int position = 0, id = 0;
    private EditText edVideoCategory, edVideoID, edVideoDesc;
    private Button btnEditVideo;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    String vidCategory, vidID, vidDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        edVideoCategory = findViewById(R.id.edTextVideoCategory);
        edVideoID = findViewById(R.id.edTextVideoId);
        edVideoDesc = findViewById(R.id.edTextVideoDesc);
        btnEditVideo = findViewById(R.id.btnEditVideo);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        String videoCategory = getIntent().getStringExtra("videoCategory");
        String videoID = getIntent().getStringExtra("videoID");
        String videoDesc = getIntent().getStringExtra("videoDesc");

        position = getIntent().getIntExtra("position", 0);
        id = getIntent().getIntExtra("vidId", 0);
        edVideoCategory.setText(videoCategory);
        edVideoID.setText(videoID);
        edVideoDesc.setText(videoDesc);

        btnEditVideo.setOnClickListener(v -> {
            vidCategory = edVideoCategory.getText().toString();
            vidID = edVideoID.getText().toString();
            vidDesc = edVideoDesc.getText().toString();

            if (vidCategory.isEmpty()) {
                edVideoCategory.setError("video category is required");
                edVideoCategory.requestFocus();
                return;
            }

            if (vidID.isEmpty()) {
                edVideoID.setError("video ID is required");
                edVideoID.requestFocus();
                return;
            }

            if (vidDesc.isEmpty()) {
                edVideoDesc.setError("video description is required");
                edVideoDesc.requestFocus();
                return;
            }

            editVideo();
        });
    }

    private void editVideo() {
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        String token = preferences.getString("token", "");

        AndroidNetworking.post(Constant.EDIT_VIDEO)
                .addHeaders("Authorization", "Bearer "+token)
                .addBodyParameter("id", id+"")
                .addBodyParameter("video_category", vidCategory)
                .addBodyParameter("video_id", vidID)
                .addBodyParameter("video_desc", vidDesc)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                //update the video in recyclerview
                                Video video = VideoFragment.arrayListVideo.get(position);
                                video.setVideo_category(vidCategory);
                                video.setVideo_id(vidID);
                                video.setVideo_desc(vidDesc);
                                VideoFragment.arrayListVideo.set(position, video);
                                VideoFragment.recyclerViewVideo.getAdapter().notifyItemChanged(position);
                                VideoFragment.recyclerViewVideo.getAdapter().notifyDataSetChanged();
                                finish();
                                Toast.makeText(EditVideoActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(EditVideoActivity.this, "network unavailable, try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}