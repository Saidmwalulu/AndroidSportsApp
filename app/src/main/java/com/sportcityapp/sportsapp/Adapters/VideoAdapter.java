package com.sportcityapp.sportsapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.sportcityapp.sportsapp.EditVideoActivity;
import com.sportcityapp.sportsapp.R;
import com.sportcityapp.sportsapp.WelcomeActivity;
import com.sportcityapp.sportsapp.models.Video;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideosHolder> {

    private Context context;
    private ArrayList<Video> videoList;
    private SharedPreferences preferences;

    public VideoAdapter(Context context, ArrayList<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public VideosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video, parent, false);
        return new VideosHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosHolder holder, int position) {
        Video video = videoList.get(position);

        String videoId = video.getVideo_id();
        String videoPlay = "<iframe width=\"100%\" height=\"220\" src=\"https://www.youtube.com/embed/"+videoId+"\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        holder.videoView.loadData(videoPlay, "text/html", "utf-8");
        holder.videoView.getSettings().setJavaScriptEnabled(true);
        holder.videoView.setWebChromeClient(new WebChromeClient());

        holder.textCategory.setText(video.getVideo_category());
        holder.textDesc.setText(video.getVideo_desc());

        if (preferences.getInt("role", 0) == 0) {
            //video.getUser_id() != preferences.getInt("id", 0) &&
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            if (video.getUser_id() == preferences.getInt("id", 0)) {
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("confirm");
                    builder.setMessage("delete video?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteVideo(video.getId(), position);
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
                holder.btnDelete.setVisibility(View.GONE);
            }

        }

        if (preferences.getInt("role", 0) == 0) {
            //video.getUser_id() != preferences.getInt("id", 0) &&
            holder.btnEdit.setVisibility(View.GONE);
        } else {
            if (video.getUser_id() == preferences.getInt("id", 0)) {
                holder.btnEdit.setVisibility(View.VISIBLE);
                holder.btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(((WelcomeActivity)context), EditVideoActivity.class);

                    intent.putExtra("vidId", video.getId());
                    intent.putExtra("position", position);
                    intent.putExtra("videoCategory", video.getVideo_category());
                    intent.putExtra("videoID", video.getVideo_id());
                    intent.putExtra("videoDesc", video.getVideo_desc());

                    context.startActivity(intent);
                });
            } else {
                holder.btnEdit.setVisibility(View.GONE);
            }

        }

    }

    private void deleteVideo(int videoId, int position) {
        String token = preferences.getString("token", "");

        AndroidNetworking.post(Constant.DELETE_VIDEO)
                .addHeaders("Authorization", "Bearer "+token)
                .addBodyParameter("id", videoId+"")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                videoList.remove(position);
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
        return videoList.size();
    }

    class VideosHolder extends RecyclerView.ViewHolder {

        private WebView videoView;
        private TextView textCategory, textDesc;
        private ImageButton btnDelete, btnEdit;
        private FrameLayout fullScreen;

        public VideosHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.video_view);
            textCategory = itemView.findViewById(R.id.video_category);
            btnDelete = itemView.findViewById(R.id.video_delete);
            btnEdit = itemView.findViewById(R.id.video_edit);
            textDesc = itemView.findViewById(R.id.video_description);

        }
    }
}
