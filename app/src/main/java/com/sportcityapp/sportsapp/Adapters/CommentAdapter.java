package com.sportcityapp.sportsapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.CommentActivity;
import com.sportcityapp.sportsapp.Constant;
import com.sportcityapp.sportsapp.Fragments.HomeFragment;
import com.sportcityapp.sportsapp.R;
import com.sportcityapp.sportsapp.models.Comment;
import com.sportcityapp.sportsapp.models.Post;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentsHolder>{
    private Context context;
    private ArrayList<Comment> list;
    private SharedPreferences preferences;

    public CommentAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new CommentsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsHolder holder, int position) {
        Comment comment = list.get(position);
        Picasso.get().load(comment.getUser().getProfile_image_url()).into(holder.circleImageProfileComment);
        holder.nameComment.setText(comment.getUser().getName());
        String timeAgo = calculateTimeAgo(comment.getDate());
        holder.dateComment.setText(timeAgo);  //set the date in time ago
        holder.comment.setText(comment.getComment());

        int changeRole = preferences.getInt("role", 0);

        if (comment.getUser().getId()  == preferences.getInt("id", 0) || changeRole == 2) {
            holder.imgBtnDelComment.setVisibility(View.VISIBLE);
            holder.imgBtnDelComment.setOnClickListener(v -> {

                deleteComment(comment.getId(), position);
            });
        } else {
            holder.imgBtnDelComment.setVisibility(View.GONE);
        }

    }

    private String calculateTimeAgo(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void deleteComment(int commentId, int position) {
        String token = preferences.getString("token", "");

        AndroidNetworking.post(Constant.DELETE_COMMENTS)
                .addHeaders("Authorization", "Bearer "+token)
                .addBodyParameter("id", commentId+"")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        //dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                list.remove(position);
                                Post post = HomeFragment.arrayList.get(CommentActivity.postPosition);
                                post.setComments(post.getComments()-1);
                                HomeFragment.arrayList.set(CommentActivity.postPosition, post);
                                HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                                notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //dialog.dismiss();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CommentsHolder extends RecyclerView.ViewHolder{

        private CircleImageView circleImageProfileComment;
        private TextView nameComment, dateComment, comment;
        private ImageButton imgBtnDelComment;

        public CommentsHolder(@NonNull View itemView) {
            super(itemView);

            circleImageProfileComment = itemView.findViewById(R.id.imgCommentProfile);
            nameComment = itemView.findViewById(R.id.commentName);
            dateComment = itemView.findViewById(R.id.commentDate);
            comment = itemView.findViewById(R.id.txtAllComments);
            imgBtnDelComment = itemView.findViewById(R.id.btnDeleteComment);
        }
    }
}
