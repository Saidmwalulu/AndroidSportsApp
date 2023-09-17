package com.sportcityapp.sportsapp;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.Fragments.HomeFragment;
import com.sportcityapp.sportsapp.models.Post;

import org.json.JSONException;
import org.json.JSONObject;

public class EditActivity extends AppCompatActivity {

    private int position = 0, id = 0;
    //private TextView textView; error in code.. this was checking
    private EditText txtEditDesc;
    private Button btnEditSave;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences, preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        sharedPreferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);

        txtEditDesc = findViewById(R.id.textDescEditPost);
        btnEditSave = findViewById(R.id.btnEditPost);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        position = getIntent().getIntExtra("position", 0);
        id = getIntent().getIntExtra("postId", 0);
        txtEditDesc.setText(getIntent().getStringExtra("desc"));

        btnEditSave.setOnClickListener(v -> {
            if (!txtEditDesc.getText().toString().isEmpty()) {
                progressDialog.setMessage("saving..");
                progressDialog.show();

                String token = sharedPreferences.getString("token", "");


                AndroidNetworking.post(Constant.UPDATE_POST)
                        .addHeaders("Authorization", "Bearer "+token)
                        .addBodyParameter("id", id+"")
                        .addBodyParameter("desc", txtEditDesc.getText().toString())
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (object.getBoolean("success")) {
                                        //update the post in recyclerview
                                        Post post = HomeFragment.arrayList.get(position);
                                        post.setDesc(txtEditDesc.getText().toString());
                                        HomeFragment.arrayList.set(position, post);
                                        HomeFragment.recyclerView.getAdapter().notifyItemChanged(position);
                                        HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                anError.printStackTrace();
                                Log.e(TAG, "onError: ", anError.fillInStackTrace());
                                Log.d(TAG, "onError response " + anError.getResponse());
                            }
                        });
            }
        });
    }

    public void turnBack(View view) {
        super.onBackPressed();
    }
}
