package com.sportcityapp.sportsapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.AboutActivity;
import com.sportcityapp.sportsapp.AddPostActivity;
import com.sportcityapp.sportsapp.Constant;
import com.sportcityapp.sportsapp.ContactActivity;
import com.sportcityapp.sportsapp.LoginActivity;
import com.sportcityapp.sportsapp.R;
import com.sportcityapp.sportsapp.SetAdminActivity;
import com.sportcityapp.sportsapp.SettingsActivity;
import com.sportcityapp.sportsapp.WelcomeActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private ImageView cancel, btnAddPostPlus, btnSetAdmin;
    private TextView txtSettings, txtAbout, txtContact, txtAddPost, txtLogout, txtName, txtEmail, txtSetAdmin;
    private CircleImageView profileUser;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        cancel = view.findViewById(R.id.cancel);
        txtSettings = view.findViewById(R.id.textSettings);
        txtAbout = view.findViewById(R.id.textAbout);
        txtContact = view.findViewById(R.id.textContacts);
        txtAddPost = view.findViewById(R.id.textAddPost);
        btnAddPostPlus = view.findViewById(R.id.btnAddPostPlus);
        txtSetAdmin = view.findViewById(R.id.textAddAdmin);
        btnSetAdmin = view.findViewById(R.id.btnAddAdmin);
        txtLogout = view.findViewById(R.id.textLogout);
        profileUser = view.findViewById(R.id.profile_settings);
        txtName = view.findViewById(R.id.profileName);
        txtEmail = view.findViewById(R.id.profileEmail);

        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String profile_image = sharedPreferences.getString("profile_image_url", "");
        String profile_name = sharedPreferences.getString("name", "");
        String profile_email = sharedPreferences.getString("email", "");

        Picasso.get().load(profile_image).into(profileUser);
        txtName.setText(profile_name + "");
        txtEmail.setText(profile_email + "");

        cancel.setOnClickListener(v -> {
            dismiss();
        });

        txtSettings.setOnClickListener(v -> {
            startActivity(new Intent((WelcomeActivity) getContext(), SettingsActivity.class));
            dismiss();
        });

        txtAbout.setOnClickListener(v -> {
            startActivity(new Intent((WelcomeActivity) getContext(), AboutActivity.class));
            dismiss();
        });

        txtContact.setOnClickListener(v -> {
            startActivity(new Intent((WelcomeActivity) getContext(), ContactActivity.class));
            dismiss();
        });

        if (sharedPreferences.getInt("role", 0) == 0) {
            txtAddPost.setVisibility(View.GONE);
            btnAddPostPlus.setVisibility(View.GONE);

        } else {
            txtAddPost.setVisibility(View.VISIBLE);
            btnAddPostPlus.setVisibility(View.VISIBLE);
            txtAddPost.setOnClickListener(v -> {
                startActivity(new Intent((WelcomeActivity) getContext(), AddPostActivity.class));
                dismiss();
            });
        }
        if (sharedPreferences.getInt("role", 0) == 0 || sharedPreferences.getInt("role", 0) == 1) {
            txtSetAdmin.setVisibility(View.GONE);
            btnSetAdmin.setVisibility(View.GONE);
        } else {
            txtSetAdmin.setVisibility(View.VISIBLE);
            btnSetAdmin.setVisibility(View.VISIBLE);
            txtSetAdmin.setOnClickListener(v -> {
                startActivity(new Intent((WelcomeActivity) getContext(), SetAdminActivity.class));
                dismiss();
            });
        }

        txtLogout.setOnClickListener(v -> {
            dialog.setMessage("logging out..");
            dialog.show();

            String token = sharedPreferences.getString("token", "");

            AndroidNetworking.post(Constant.LOGOUT)
                    .addHeaders("Authorization", "Bearer "+token)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.getBoolean("success")) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    startActivity(new Intent((WelcomeActivity)getContext(), LoginActivity.class));
                                    ((WelcomeActivity)getContext()).finish();

                                    String message = object.getString("message");
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            dialog.dismiss();
                        }
                    });
        });

        return view;
    }
}