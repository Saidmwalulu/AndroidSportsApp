package com.sportcityapp.sportsapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.Constant;
import com.sportcityapp.sportsapp.EditFixtureActivity;
import com.sportcityapp.sportsapp.R;
import com.sportcityapp.sportsapp.WelcomeActivity;
import com.sportcityapp.sportsapp.models.Fixture;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FixtureAdapter extends RecyclerView.Adapter<FixtureAdapter.FixturesHolder> {

    private Context context;
    private ArrayList<Fixture> fixtureList;
    private SharedPreferences preferences;

    public FixtureAdapter(Context context, ArrayList<Fixture> fixtureList) {
        this.context = context;
        this.fixtureList = fixtureList;
        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public FixturesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fixture, parent, false);
        return new FixturesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FixturesHolder holder, int position) {
        Fixture fixture = fixtureList.get(position);

        Picasso.get().load(Constant.URL+"uploads/logos/"+fixture.getSponsor_logo()).into(holder.sponsorLogo);
        holder.sponsorName.setText(fixture.getSponsor_name());
        holder.date.setText(fixture.getDate());
        holder.time.setText(fixture.getTime());
        holder.venue.setText(fixture.getVenue());
        holder.teamA.setText(fixture.getTeam_a_name());
        holder.teamB.setText(fixture.getTeam_b_name());


        if (preferences.getInt("role", 0) == 0) {
            //fixture.getUser_id() != preferences.getInt("id", 0)
            holder.btnDeleteFx.setVisibility(View.GONE);
        } else {
            if (fixture.getUser_id() == preferences.getInt("id", 0)) {
                holder.btnDeleteFx.setVisibility(View.VISIBLE);
                holder.btnDeleteFx.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteFixture(fixture.getId(), position);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                });
            } else {
                holder.btnDeleteFx.setVisibility(View.GONE);
            }

        }


        if (preferences.getInt("role", 0) == 0) {
            //fixture.getUser_id() != preferences.getInt("id", 0) && preferences.getInt("role", 0) != 1
            holder.btnEditFx.setVisibility(View.GONE);
        } else {
            if (fixture.getUser_id() == preferences.getInt("id", 0)) {
                holder.btnEditFx.setVisibility(View.VISIBLE);
                holder.btnEditFx.setOnClickListener(v -> {

                Intent intent = new Intent(((WelcomeActivity)context), EditFixtureActivity.class);

                intent.putExtra("fixtureId", fixture.getId());
                intent.putExtra("position", position);
                intent.putExtra("sponsorName", fixture.getSponsor_name());
                intent.putExtra("date", fixture.getDate());
                intent.putExtra("time", fixture.getTime());
                intent.putExtra("venue", fixture.getVenue());
                intent.putExtra("teamA", fixture.getTeam_a_name());
                intent.putExtra("teamB", fixture.getTeam_b_name());

                context.startActivity(intent);
                });
            } else {
                holder.btnEditFx.setVisibility(View.GONE);
            }

        }

    }

    private void deleteFixture(int fixtureId, int position) {
        String token = preferences.getString("token", "");

        AndroidNetworking.post(Constant.DELETE_FIXTURE)
                .addHeaders("Authorization", "Bearer "+token)
                .addBodyParameter("id", fixtureId+"")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                fixtureList.remove(position);
                                notifyDataSetChanged();

                                Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, "network unavailable, try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return fixtureList.size();
    }

    class FixturesHolder extends RecyclerView.ViewHolder {

        private ImageView sponsorLogo;
        private TextView sponsorName, date, time, venue, teamA, teamB;
        private ImageButton btnDeleteFx, btnEditFx;

        public FixturesHolder(@NonNull View itemView) {
            super(itemView);

            sponsorLogo = itemView.findViewById(R.id.rec_sponsor_logo);
            sponsorName = itemView.findViewById(R.id.rec_sponsor_name);
            date = itemView.findViewById(R.id.rec_date);
            time = itemView.findViewById(R.id.rec_time);
            venue = itemView.findViewById(R.id.rec_venue);
            teamA = itemView.findViewById(R.id.rec_team_a);
            teamB = itemView.findViewById(R.id.rec_team_b);
            btnDeleteFx = itemView.findViewById(R.id.deleteFixture);
            btnEditFx = itemView.findViewById(R.id.editFixture);
        }
    }
}
