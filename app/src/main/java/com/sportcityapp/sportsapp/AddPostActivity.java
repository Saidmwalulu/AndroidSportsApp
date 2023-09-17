package com.sportcityapp.sportsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.Fragments.HomeFragment;
import com.sportcityapp.sportsapp.models.Post;
import com.sportcityapp.sportsapp.models.User;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AddPostActivity extends AppCompatActivity {

    private ImageView imageViewPost;
    private TextView textViewPickImg;
    private EditText editTextPost;
    private Button buttonPost, buttonVideo, buttonFixture;
    private ProgressDialog dialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        dialog = new ProgressDialog(AddPostActivity.this);
        dialog.setCancelable(false);

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        imageViewPost = findViewById(R.id.postImage);
        textViewPickImg = findViewById(R.id.txtPickImage);
        editTextPost = findViewById(R.id.textDescAddPost);
        buttonPost = findViewById(R.id.btnAddPost);
        buttonVideo = findViewById(R.id.btnAddVideos);
        buttonFixture = findViewById(R.id.btnAddFixtures);

        buttonVideo.setOnClickListener(v -> {
            startActivity(new Intent(AddPostActivity.this, AddVideoActivity.class));
        });

        buttonFixture.setOnClickListener(v -> {
            startActivity(new Intent(AddPostActivity.this, AddFixtureActivity.class));
        });

        textViewPickImg.setOnClickListener(v -> {
            Dexter.withContext(AddPostActivity.this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(AddPostActivity.this);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            if (permissionDeniedResponse.isPermanentlyDenied()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                                builder.setTitle("Permission required")
                                        .setMessage("Please go to settings to allow access to storage permission")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                                startActivityForResult(intent, 51);
                                            }
                                        }).setNegativeButton("Cancel", null)
                                        .show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        });

    }
    public void goBack(View view) {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri imageUri = result.getUri();
                imageViewPost.setImageURI(imageUri);

                buttonPost.setOnClickListener(v -> {
                    dialog.setMessage("posting..");
                    dialog.show();
                    if (!editTextPost.getText().toString().isEmpty()) {
                        String token = preferences.getString("token", "");
                        File imageFile = new File(imageUri.getPath());

                        AndroidNetworking.upload(Constant.ADD_POST)
                                .addHeaders("Authorization", "Bearer "+token)
                                .addMultipartFile("photo", imageFile)
                                .addMultipartParameter("desc", editTextPost.getText().toString().trim())
                                .setPriority(Priority.HIGH)
                                .build()
                                .getAsString(new StringRequestListener() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onResponse(String response) {
                                        dialog.dismiss();
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            if (object.getBoolean("success")) {
                                                JSONObject postObject = object.getJSONObject("post");
                                                JSONObject userObject = postObject.getJSONObject("user");

                                                User user = new User();
                                                user.setId(userObject.getInt("id"));
                                                user.setName(userObject.getString("name"));
                                                user.setProfile_image("profile_image_url");

                                                Post post = new Post();
                                                post.setUser(user);
                                                post.setId(postObject.getInt("id"));
                                                post.setSelfLike(false);
                                                post.setPhoto(postObject.getString("photo"));
                                                post.setDesc(postObject.getString("desc"));
                                                post.setComments(0);
                                                post.setLikes(0);
                                                post.setDate(postObject.getString("created_at"));
                                                post.setDate(postObject.getString("updated_at"));

                                                HomeFragment.arrayList.add(0, post);
                                                HomeFragment.recyclerView.getAdapter().notifyItemInserted(0);
                                                HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                                                Toast.makeText(AddPostActivity.this, "posted", Toast.LENGTH_SHORT).show();

                                                /*Fragment fragment = new HomeFragment();
                                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.fragment_display, fragment).commit();*/
                                                startActivity(new Intent(AddPostActivity.this, WelcomeActivity.class));

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        dialog.dismiss();
                                        anError.printStackTrace();
                                        Toast.makeText(AddPostActivity.this, "network unavailable, please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(AddPostActivity.this, "post description is required", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}