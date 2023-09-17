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
import com.sportcityapp.sportsapp.models.User;
import com.sportcityapp.sportsapp.models.Video;

import org.json.JSONException;
import org.json.JSONObject;

public class AddVideoActivity extends AppCompatActivity {

    private EditText edVideoCategory, edVideoID, edVideoDesc;
    private Button btnUploadVideo;
    private SharedPreferences sharedPreferences;
    private ProgressDialog dialog;
    String videoCategory, videoID, videoDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        edVideoCategory = findViewById(R.id.editTextVideoCategory);
        edVideoID = findViewById(R.id.editTextVideoId);
        edVideoDesc = findViewById(R.id.editTextVideoDesc);
        btnUploadVideo = findViewById(R.id.btnUploadVideo);



        btnUploadVideo.setOnClickListener(v -> {
            videoCategory = edVideoCategory.getText().toString();
            videoID = edVideoID.getText().toString();
            videoDesc = edVideoDesc.getText().toString();

            if (videoCategory.isEmpty()) {
                edVideoCategory.setError("video category is required");
                edVideoCategory.requestFocus();
                return;
            }

            if (videoID.isEmpty()) {
                edVideoID.setError("video ID is required");
                edVideoID.requestFocus();
                return;
            }

            if (videoDesc.isEmpty()) {
                edVideoDesc.setError("video description is required");
                edVideoDesc.requestFocus();
                return;
            }

            uploadVideo();
        });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    private void uploadVideo() {
        dialog.setMessage("uploading..");
        dialog.show();

        String token = sharedPreferences.getString("token", "");

        AndroidNetworking.post(Constant.ADD_VIDEO)
                .addHeaders("Authorization", "Bearer "+token)
                .addBodyParameter("video_category", videoCategory)
                .addBodyParameter("video_desc", videoDesc)
                .addBodyParameter("video_id", videoID)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {

                                JSONObject videoObject = object.getJSONObject("video");
                                JSONObject userVideo = videoObject.getJSONObject("user");

                                User user = new User();
                                user.setId(userVideo.getInt("id"));
                                user.setProfile_image_url(userVideo.getString("profile_image_url"));
                                user.setName(userVideo.getString("name"));

                                Video video = new Video();

                                video.setId(videoObject.getInt("id"));
                                video.setUser_id(videoObject.getInt("user_id"));
                                video.setVideo_category(videoObject.getString("video_category"));
                                video.setVideo_desc(videoObject.getString("video_desc"));
                                video.setVideo_id(videoObject.getString("video_id"));

                                VideoFragment.arrayListVideo.add(0, video);
                                VideoFragment.recyclerViewVideo.getAdapter().notifyItemInserted(0);
                                VideoFragment.recyclerViewVideo.getAdapter().notifyDataSetChanged();

                                Toast.makeText(AddVideoActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        anError.printStackTrace();
                        Toast.makeText(AddVideoActivity.this, "network unavailable, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}