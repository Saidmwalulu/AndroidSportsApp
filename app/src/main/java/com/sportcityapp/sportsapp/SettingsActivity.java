package com.sportcityapp.sportsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton buttonSelect;
    private CircleImageView circleImageViewProfile;
    private Button btnUploadProfile, btnChangePwd;
    private ProgressDialog progressDialog;
    private TextInputLayout changeName, changeCurrentPwd, changeNewPwd, changeConNewPwd;
    private TextInputEditText edChangeName, edChangeCurrentPwd, edChangeNewPwd, edChangeConNewPwd;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        buttonSelect = findViewById(R.id.btnSelect);
        circleImageViewProfile = findViewById(R.id.profile_set);
        btnUploadProfile = findViewById(R.id.btnUploadProfile);
        btnChangePwd = findViewById(R.id.btnChangePwd);
        changeName = findViewById(R.id.txtInputLayoutChangeName);
        edChangeName = findViewById(R.id.txtInputChangeName);
        changeCurrentPwd = findViewById(R.id.txtInputLayoutCurrentChangePwd);
        edChangeCurrentPwd = findViewById(R.id.txtInputCurrentChangePwd);
        changeNewPwd = findViewById(R.id.txtInputLayoutChangePwd);
        edChangeNewPwd = findViewById(R.id.txtInputChangePwd);
        changeConNewPwd = findViewById(R.id.txtInputLayoutChangeConPwd);
        edChangeConNewPwd = findViewById(R.id.txtInputChangeConPwd);

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("updating. Please wait..");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        Picasso.get().load(preferences.getString("profile_image_url", "")).into(circleImageViewProfile);
        String profile_name = preferences.getString("name", "");
        edChangeName.setText(profile_name);

        edChangeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edChangeName.getText().toString().isEmpty()) {
                    changeName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edChangeCurrentPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edChangeCurrentPwd.getText().toString().isEmpty()) {
                    changeCurrentPwd.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edChangeNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edChangeNewPwd.getText().toString().isEmpty()) {
                    changeNewPwd.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edChangeConNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edChangeConNewPwd.getText().toString().isEmpty()) {
                    changeConNewPwd.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        circleImageViewProfile.setOnClickListener(v -> {
            Dexter.withContext(SettingsActivity.this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(SettingsActivity.this);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            if (permissionDeniedResponse.isPermanentlyDenied()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
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
                    })
                    .check();
        });

        btnUploadProfile.setOnClickListener(v -> {
            if (validateName()) {
                updateName();
            }
        });

        btnChangePwd.setOnClickListener(v -> {
            if (validatePwd()) {
                changePassword();
            }
        });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    private void updateName() {
        progressDialog.show();
        String token = preferences.getString("token", "");

        AndroidNetworking.post(Constant.USER_NAME)
                .addHeaders("Authorization", "Bearer "+token)
                .addBodyParameter("name", edChangeName.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        float progress = (float) bytesUploaded / totalBytes * 100;
                        progressDialog.setProgress((int) progress);
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                SharedPreferences.Editor editor = preferences.edit();
                                JSONObject user = object.getJSONObject("user");
                                editor.putString("profile_image_url", user.getString("profile_image_url")); //for name sake
                                editor.putString("name", user.getString("name"));
                                editor.apply();
                                Toast.makeText(SettingsActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void changePassword() {
        progressDialog.show();;
        String token = preferences.getString("token", "");
        AndroidNetworking.post(Constant.CHANGE_PASSWORD)
                .addHeaders("Authorization", "Bearer "+token)
                .addBodyParameter("current_password", edChangeCurrentPwd.getText().toString().trim())
                .addBodyParameter("password", edChangeNewPwd.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        float progress = (float) bytesUploaded / totalBytes * 100;
                        progressDialog.setProgress((int) progress);
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                String message = object.getString("message");
                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                String message = object.getString("fail_message");
                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                    }
                });
    }

    private boolean validateName() {
        if (edChangeName.getText().toString().isEmpty()) {
            changeName.setErrorEnabled(true);
            changeName.setError("Name is required");
            return false;
        }
        return true;
    }

    private boolean validatePwd() {
        if (edChangeCurrentPwd.getText().toString().isEmpty()) {
            changeCurrentPwd.setErrorEnabled(true);
            changeCurrentPwd.setError("current password is required");
            return false;
        }

        if (edChangeNewPwd.getText().toString().isEmpty()) {
            changeNewPwd.setErrorEnabled(true);
            changeNewPwd.setError("new password is required");
            return false;
        }

        if (!edChangeConNewPwd.getText().toString().equals(edChangeNewPwd.getText().toString())) {
            changeConNewPwd.setErrorEnabled(true);
            changeConNewPwd.setError("Password doesn't match");
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                circleImageViewProfile.setImageURI(resultUri);
                buttonSelect.setVisibility(View.VISIBLE);

                buttonSelect.setOnClickListener(v -> {
                    progressDialog.show();

                        String token = preferences.getString("token", "");
                        File imageFile = new File(resultUri.getPath());

                        AndroidNetworking.upload(Constant.USER_PROFILE)
                                .addHeaders("Authorization", "Bearer " + token)
                                .addMultipartFile("profile_photo", imageFile)
                                .setPriority(Priority.HIGH)
                                .build()
                                .setUploadProgressListener(new UploadProgressListener() {
                                    @Override
                                    public void onProgress(long bytesUploaded, long totalBytes) {
                                        float progress = (float) bytesUploaded / totalBytes * 100;
                                        progressDialog.setProgress((int) progress);
                                    }
                                }).getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.dismiss();
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            if (object.getBoolean("success")) {
                                                JSONObject user = object.getJSONObject("user");
                                                //make shared preference user

                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("profile_image_url", user.getString("profile_image_url"));
                                                //editor.putString("name", user.getString("name"));
                                                editor.apply();

                                                String message = object.getString("message");
                                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(SettingsActivity.this, "failed to upload", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(SettingsActivity.this, "Passing error", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        progressDialog.dismiss();
                                        anError.printStackTrace();
                                        Toast.makeText(SettingsActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                                    }
                                });

                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}