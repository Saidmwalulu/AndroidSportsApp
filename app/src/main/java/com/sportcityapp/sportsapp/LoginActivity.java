package com.sportcityapp.sportsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutEmail, inputLayoutPwd;
    private TextInputEditText editTextEmail, editTextPwd;
    private TextView signUp, forgotPassword;
    private Button login;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputLayoutEmail = findViewById(R.id.txtInputLogLayoutEmail);
        inputLayoutPwd = findViewById(R.id.txtInputLayoutLogPassword);
        editTextEmail = findViewById(R.id.txtInputLogEmail);
        editTextPwd = findViewById(R.id.txtInputLogPassword);
        signUp = findViewById(R.id.txtSignUp);
        login = findViewById(R.id.btnSignIn);
        forgotPassword = findViewById(R.id.forgotPwd);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);

        signUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        forgotPassword.setOnClickListener(v -> {
            View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_dialog, null);
            TextInputLayout forgotPwdLay = view.findViewById(R.id.txtLogLayoutForgotEmail);
            TextInputEditText forgotPwdEt = view.findViewById(R.id.txtForgotEmail);
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setView(view)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (forgotPwdEt.getText().toString().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Email address is required", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.setMessage("please wait..");
                        progressDialog.show();

                        AndroidNetworking.post(Constant.FORGOT_PASSWORD)
                                .addBodyParameter("email", forgotPwdEt.getText().toString().trim())
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.dismiss();
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            if (object.getBoolean("success")) {

                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                                builder1.setTitle("Success");
                                                builder1.setMessage(object.getString("message"));
                                                builder1.setCancelable(false);
                                                builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
                                                        finish();
                                                    }
                                                });
                                                builder1.show();


                                            } else {
                                                Toast.makeText(LoginActivity.this, object.getString("fail_message"), Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "network unavailable, try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        });


        login.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                //do something
                login();
            }
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editTextEmail.getText().toString().isEmpty()) {
                    inputLayoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextPwd.getText().toString().length()>5) {
                    inputLayoutPwd.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {
        if (editTextEmail.getText().toString().isEmpty()) {
            inputLayoutEmail.setErrorEnabled(true);
            inputLayoutEmail.setError("Email is required");
            return false;
        }

        if (editTextPwd.getText().toString().length() < 6) {
            inputLayoutPwd.setErrorEnabled(true);
            inputLayoutPwd.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }


    private void login() {
        progressDialog.setMessage("logging in..");
        progressDialog.show();

        AndroidNetworking.post(Constant.LOGIN)
                .addBodyParameter("email", editTextEmail.getText().toString().trim())
                .addBodyParameter("password", editTextPwd.getText().toString())
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("status");
                            if (result.equals("success")) {
                                JSONObject user = object.getJSONObject("user");
                                //make shared preference user
                                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = userPref.edit();
                                editor.putString("token", object.getString("token"));
                                editor.putInt("id", user.getInt("id"));
                                editor.putString("name", user.getString("name"));
                                editor.putString("email", user.getString("email"));
                                editor.putInt("role", user.getInt("role"));
                                editor.putString("profile_photo", user.getString("profile_photo"));
                                editor.putString("profile_image_url", user.getString("profile_image_url"));
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();
                                //if success
                                //Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "wrong email or password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Passing error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //error if connection failed
                        progressDialog.dismiss();
                        anError.printStackTrace();
                        Toast.makeText(LoginActivity.this, "network unavailable, please try again", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

    }
}