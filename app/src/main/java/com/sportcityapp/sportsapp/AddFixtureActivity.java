package com.sportcityapp.sportsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.sportcityapp.sportsapp.Fragments.FixtureFragment;
import com.sportcityapp.sportsapp.models.Fixture;
import com.sportcityapp.sportsapp.models.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFixtureActivity extends AppCompatActivity {

    private CircleImageView sponsorLogo;
    private EditText etSponsorName, etDate, etTime, etVenue, etTeamAName, etTeamBName;
    private Button btnUploadFixture;
    private SharedPreferences preferences;
    private ProgressDialog dialog;
    String sponsorName, date, time, venue, teamA, teamB;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri imageUri = result.getUri();
                sponsorLogo.setImageURI(imageUri);

                btnUploadFixture.setOnClickListener(v -> {

                    sponsorName = etSponsorName.getText().toString();
                    date = etDate.getText().toString();
                    time = etTime.getText().toString();
                    venue = etVenue.getText().toString();
                    teamA = etTeamAName.getText().toString();
                    teamB = etTeamBName.getText().toString();

                    if (sponsorName.isEmpty()) {
                        etSponsorName.setError("sponsor name is required");
                        etSponsorName.requestFocus();
                        return;
                    }

                    if (date.isEmpty()) {
                        etDate.setError("date is required");
                        etDate.requestFocus();
                        return;
                    }

                    if (time.isEmpty()) {
                        etTime.setError("time is required");
                        etTime.requestFocus();
                        return;
                    }

                    if (venue.isEmpty()) {
                        etVenue.setError("venue is required");
                        etVenue.requestFocus();
                        return;
                    }

                    if (teamA.isEmpty()) {
                        etTeamAName.setError("team A is required");
                        etTeamAName.requestFocus();
                        return;
                    }

                    if (teamB.isEmpty()) {
                        etTeamBName.setError("team B is required");
                        etTeamBName.requestFocus();
                        return;
                    }

                        dialog.show();
                        String token = preferences.getString("token", "");
                        File imageFile = new File(imageUri.getPath());

                        AndroidNetworking.upload(Constant.ADD_FIXTURE)
                                .addHeaders("Authorization", "Bearer "+token)
                                .addMultipartFile("sponsor_logo", imageFile)
                                .addMultipartParameter("sponsor_name", sponsorName)
                                .addMultipartParameter("date", date)
                                .addMultipartParameter("time", time)
                                .addMultipartParameter("venue", venue)
                                .addMultipartParameter("team_a_name", teamA)
                                .addMultipartParameter("team_b_name", teamB)
                                .setPriority(Priority.HIGH)
                                .build()
                                .setUploadProgressListener(new UploadProgressListener() {
                                    @Override
                                    public void onProgress(long bytesUploaded, long totalBytes) {
                                        float progress = (float) bytesUploaded / totalBytes * 100;
                                        dialog.setProgress((int)progress);
                                    }
                                }).getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        dialog.dismiss();
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            if (object.getBoolean("success")) {
                                                JSONObject fixtureObject = object.getJSONObject("fixture");
                                                JSONObject userObject = fixtureObject.getJSONObject("user");

                                                User user = new User();
                                                user.setId(userObject.getInt("id"));

                                                Fixture fixture = new Fixture();
                                                fixture.setUser(user);
                                                fixture.setId(fixtureObject.getInt("id"));
                                                fixture.setUser_id(fixtureObject.getInt("user_id"));
                                                fixture.setSponsor_logo(fixtureObject.getString("sponsor_logo"));
                                                fixture.setSponsor_name(fixtureObject.getString("sponsor_name"));
                                                fixture.setDate(fixtureObject.getString("date"));
                                                fixture.setTime(fixtureObject.getString("time"));
                                                fixture.setVenue(fixtureObject.getString("venue"));
                                                fixture.setTeam_a_name(fixtureObject.getString("team_a_name"));
                                                fixture.setTeam_b_name(fixtureObject.getString("team_b_name"));

                                                FixtureFragment.arrayListFixture.add(0, fixture);
                                                FixtureFragment.recyclerViewFixture.getAdapter().notifyItemInserted(0);
                                                FixtureFragment.recyclerViewFixture.getAdapter().notifyDataSetChanged();

                                                Toast.makeText(AddFixtureActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        dialog.dismiss();
                                        anError.printStackTrace();
                                        Toast.makeText(AddFixtureActivity.this, "network unavailable, please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fixture);

        sponsorLogo = findViewById(R.id.sponsor_logo);
        etSponsorName = findViewById(R.id.sponsor_name);
        etDate = findViewById(R.id.date);
        etTime = findViewById(R.id.time);
        etVenue = findViewById(R.id.venue);
        etTeamAName = findViewById(R.id.team_a_name);
        etTeamBName = findViewById(R.id.team_b_name);
        btnUploadFixture = findViewById(R.id.btnUploadFixture);

        dialog = new ProgressDialog(AddFixtureActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading. Please wait..");
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        sponsorLogo.setOnClickListener(v -> {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(AddFixtureActivity.this);
        });

    }

    private boolean validation() {

        return true;
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}