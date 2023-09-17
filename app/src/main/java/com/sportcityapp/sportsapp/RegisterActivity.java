package com.sportcityapp.sportsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPwd, inputLayoutConPwd;
    private TextInputEditText editTextName, editTextEmail, editTextPwd, editTextConPwd;
    private TextView signIn;
    private Button register;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputLayoutName = findViewById(R.id.txtInputLayoutRegName);
        inputLayoutEmail = findViewById(R.id.txtInputLayoutRegEmail);
        inputLayoutPwd = findViewById(R.id.txtInputLayoutRegPassword);
        inputLayoutConPwd = findViewById(R.id.txtInputLayoutConRegPassword);
        editTextName = findViewById(R.id.txtInputRegName);
        editTextEmail = findViewById(R.id.txtInputRegEmail);
        editTextPwd = findViewById(R.id.txtInputRegPassword);
        editTextConPwd = findViewById(R.id.txtInputConRegPassword);
        signIn = findViewById(R.id.txtLogin);
        register = findViewById(R.id.btnRegister);

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setCancelable(false);

        signIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        register.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                //do something
                register();
            }
        });

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editTextName.getText().toString().isEmpty()) {
                    inputLayoutName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                if (editTextPwd.getText().toString().length()>5) {
                    inputLayoutPwd.setErrorEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextConPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextConPwd.getText().toString().equals(editTextPwd.getText().toString())) {
                    inputLayoutConPwd.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {
        if (editTextName.getText().toString().isEmpty()) {
            inputLayoutName.setErrorEnabled(true);
            inputLayoutName.setError("Name is required");
            return false;
        }
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
        if (!editTextConPwd.getText().toString().equals(editTextPwd.getText().toString())) {
            inputLayoutConPwd.setErrorEnabled(true);
            inputLayoutConPwd.setError("Password doesn't match");
            return false;
        }
        return true;
    }

    private void register() {
        progressDialog.setMessage("registering..");
        progressDialog.show();

        AndroidNetworking.post(Constant.REGISTER)
                .addBodyParameter("name", editTextName.getText().toString().trim())
                .addBodyParameter("email", editTextEmail.getText().toString().trim())
                .addBodyParameter("password", editTextPwd.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            String result = object.getString("status");
                            if (result.equals("success")) {
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("Success")
                                        .setIcon(R.drawable.ic_check)
                                        .setMessage("Registered successfully")
                                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        }).show();
                            } else if(result.equals("error")) {
                                Toast.makeText(RegisterActivity.this, "email already exists", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        //error if failed
                        //anError.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "network unavailable, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}