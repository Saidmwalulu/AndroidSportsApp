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
import com.sportcityapp.sportsapp.Fragments.FixtureFragment;
import com.sportcityapp.sportsapp.models.Fixture;

import org.json.JSONException;
import org.json.JSONObject;

public class EditFixtureActivity extends AppCompatActivity {
    private int position = 0, id = 0;
    private EditText editSponsorName, editDate, editTime, editVenue, editTeamAName, editTeamBName;
    private Button btnEditFixture;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    String e_sponsorName, e_date, e_time, e_venue, e_teamA, e_teamB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fixture);

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        editSponsorName = findViewById(R.id.edit_sponsor_name);
        editDate = findViewById(R.id.edit_date);
        editTime = findViewById(R.id.edit_time);
        editVenue = findViewById(R.id.edit_venue);
        editTeamAName = findViewById(R.id.edit_team_a_name);
        editTeamBName = findViewById(R.id.edit_team_b_name);
        btnEditFixture = findViewById(R.id.btnEditFixture);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        String sponsorName = getIntent().getStringExtra("sponsorName");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String venue = getIntent().getStringExtra("venue");
        String teamA = getIntent().getStringExtra("teamA");
        String teamB = getIntent().getStringExtra("teamB");

        position = getIntent().getIntExtra("position", 0);
        id = getIntent().getIntExtra("fixtureId", 0);
        editSponsorName.setText(sponsorName);
        editDate.setText(date);
        editTime.setText(time);
        editVenue.setText(venue);
        editTeamAName.setText(teamA);
        editTeamBName.setText(teamB);

        btnEditFixture.setOnClickListener(v -> {
            e_sponsorName = editSponsorName.getText().toString();
            e_date = editDate.getText().toString();
            e_time = editTime.getText().toString();
            e_venue = editVenue.getText().toString();
            e_teamA = editTeamAName.getText().toString();
            e_teamB = editTeamBName.getText().toString();

            if (e_sponsorName.isEmpty()) {
                editSponsorName.setError("sponsor name is required");
                editSponsorName.requestFocus();
                return;
            }

            if (e_date.isEmpty()) {
                editDate.setError("date is required");
                editDate.requestFocus();
                return;
            }

            if (e_time.isEmpty()) {
                editTime.setError("time is required");
                editTime.requestFocus();
                return;
            }

            if (e_venue.isEmpty()) {
                editVenue.setError("venue is required");
                editVenue.requestFocus();
                return;
            }

            if (e_teamA.isEmpty()) {
                editTeamAName.setError("team A is required");
                editTeamAName.requestFocus();
                return;
            }

            if (e_teamB.isEmpty()) {
                editTeamBName.setError("team B is required");
                editTeamBName.requestFocus();
                return;
            }

            editFixture();

        });
    }

    private void editFixture() {
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        String token = preferences.getString("token", "");

        AndroidNetworking.post(Constant.EDIT_FIXTURE)
                .addHeaders("Authorization", "Bearer "+token)
                .addBodyParameter("id", id+"")
                .addBodyParameter("sponsor_name", e_sponsorName)
                .addBodyParameter("date", e_date)
                .addBodyParameter("time", e_time)
                .addBodyParameter("venue", e_venue)
                .addBodyParameter("team_a_name", e_teamA)
                .addBodyParameter("team_b_name", e_teamB)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                //update the fixture in recyclerview
                                Fixture fixture = FixtureFragment.arrayListFixture.get(position);
                                fixture.setSponsor_name(e_sponsorName);
                                fixture.setDate(e_date);
                                fixture.setTime(e_time);
                                fixture.setVenue(e_venue);
                                fixture.setTeam_a_name(e_teamA);
                                fixture.setTeam_b_name(e_teamB);
                                FixtureFragment.arrayListFixture.set(position, fixture);
                                FixtureFragment.recyclerViewFixture.getAdapter().notifyItemChanged(position);
                                FixtureFragment.recyclerViewFixture.getAdapter().notifyDataSetChanged();
                                finish();
                                Toast.makeText(EditFixtureActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(EditFixtureActivity.this, "network unavailable, try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}