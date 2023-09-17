package com.sportcityapp.sportsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordResetActivity extends AppCompatActivity {

    private TextInputLayout forgotEmail, forgotOTP, forgotPwd, forgotConPwd;
    private TextInputEditText eTforgotEmail, eTforgotOTP, eTforgotPwd, eTforgotConPwd;
    private Button resetPwd;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        forgotEmail = findViewById(R.id.txtInputLayoutForgotEmail);
        forgotOTP = findViewById(R.id.txtInputLayoutForgotOTP);
        forgotPwd = findViewById(R.id.txtInputLayoutForgotPwd);
        forgotConPwd = findViewById(R.id.txtInputLayoutForgotConPwd);
        eTforgotEmail = findViewById(R.id.txtInputForgotEmail);
        eTforgotOTP = findViewById(R.id.txtInputForgotOTP);
        eTforgotPwd = findViewById(R.id.txtInputForgotPwd);
        eTforgotConPwd = findViewById(R.id.txtInputForgotConPwd);
        resetPwd = findViewById(R.id.btnResetPwd);

        dialog = new ProgressDialog(PasswordResetActivity.this);
        dialog.setCancelable(false);

        resetPwd.setOnClickListener(v -> {
            if (validate()) {
                resetPassword();
            }
        });

        eTforgotEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eTforgotEmail.getText().toString().length()>5) {
                    forgotEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eTforgotOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eTforgotOTP.getText().toString().length()>5) {
                    forgotOTP.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eTforgotPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eTforgotPwd.getText().toString().length()>5) {
                    forgotPwd.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eTforgotConPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eTforgotConPwd.getText().toString().length()>5) {
                    forgotConPwd.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void resetPassword() {
        dialog.setMessage("please wait..");
        dialog.show();

        AndroidNetworking.post(Constant.RESET_PASSWORD)
                .addBodyParameter("email", eTforgotEmail.getText().toString().trim())
                .addBodyParameter("otp", eTforgotOTP.getText().toString().trim())
                .addBodyParameter("password", eTforgotPwd.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                Toast.makeText(PasswordResetActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                                startActivity(new Intent(PasswordResetActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(PasswordResetActivity.this, object.getString("fail_message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Toast.makeText(PasswordResetActivity.this, "network unavailable, try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    private boolean validate() {
        if (eTforgotEmail.getText().toString().isEmpty()) {
            forgotEmail.setErrorEnabled(true);
            forgotEmail.setError("Email is required");
            return false;
        }

        if (eTforgotOTP.getText().toString().isEmpty()) {
            forgotOTP.setErrorEnabled(true);
            forgotOTP.setError("OTP Code is required");
            return false;
        }

        if (eTforgotPwd.getText().toString().length() < 6) {
            forgotPwd.setErrorEnabled(true);
            forgotPwd.setError("Password must be at least 6 characters");
            return false;
        }

        if (!eTforgotConPwd.getText().toString().equals(eTforgotPwd.getText().toString())) {
            forgotConPwd.setErrorEnabled(true);
            forgotConPwd.setError("Password doesn't match");
            return false;
        }
        return true;
    }
}