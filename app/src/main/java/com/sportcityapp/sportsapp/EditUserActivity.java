package com.sportcityapp.sportsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.models.AllUsers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EditUserActivity extends AppCompatActivity {

    private TextView nameUser;
    private EditText editUser;
    private Button updateUser;
    private ProgressDialog dialog;
    private SharedPreferences preferences;
    private int position = 0, id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);

        nameUser = findViewById(R.id.txtNameA);
        editUser = findViewById(R.id.editUser);
        updateUser = findViewById(R.id.btnUpdateUser);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        position = getIntent().getIntExtra("position", 0);
        id = getIntent().getIntExtra("userId", 0);
        String email = getIntent().getStringExtra("email");
        String email2 = "saidmwalulu@gmail.com";
        nameUser.setText(getIntent().getStringExtra("userName"));
        editUser.setText(getIntent().getIntExtra("role", 0)+"");

        if (Objects.equals(email, email2)) {
            Toast.makeText(this, "app admin", Toast.LENGTH_SHORT).show();
            editUser.setVisibility(View.GONE);
            updateUser.setVisibility(View.GONE);
        }

        updateUser.setOnClickListener(v -> {
            if (!editUser.getText().toString().isEmpty()) {
                dialog.setMessage("saving..");
                dialog.show();

                String token = preferences.getString("token", "");

                AndroidNetworking.post(Constant.UPDATE_USERS)
                        .addHeaders("Authorization", "Bearer "+token)
                        .addBodyParameter("id", id+"")
                        .addBodyParameter("role", editUser.getText().toString().trim())
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                dialog.dismiss();
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (object.getBoolean("success")) {
                                        //update recyclerview
                                        AllUsers allUsers = SetAdminActivity.arrayAllUsers.get(position);
                                        allUsers.setRole(Integer.parseInt(editUser.getText().toString()));
                                        SetAdminActivity.arrayAllUsers.set(position, allUsers);
                                        SetAdminActivity.recyclerViewAllUsers.getAdapter().notifyItemChanged(position);
                                        SetAdminActivity.recyclerViewAllUsers.getAdapter().notifyDataSetChanged();
                                        finish();
                                        Toast.makeText(EditUserActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}