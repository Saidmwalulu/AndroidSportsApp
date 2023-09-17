package com.sportcityapp.sportsapp.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sportcityapp.sportsapp.CommentActivity;
import com.sportcityapp.sportsapp.Constant;
import com.sportcityapp.sportsapp.EditActivity;
import com.sportcityapp.sportsapp.R;
import com.sportcityapp.sportsapp.WelcomeActivity;
import com.sportcityapp.sportsapp.models.Post;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostsHolder>{

    private Context context;
    private ArrayList<Post> list;
    private ArrayList<Post> listAll;
    private SharedPreferences sharedPreferences;

    public PostAdapter(Context context, ArrayList<Post> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new PostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsHolder holder, int position) {

        Post post = list.get(position);
        Picasso.get().load(post.getUser().getProfile_image_url()).into(holder.profileImage);
        Picasso.get().load(Constant.URL+"uploads/posts/"+post.getPhoto()).into(holder.imgPost);
        holder.txtName.setText(post.getUser().getName());
        holder.txtComments.setText("View all "+post.getComments()+" comments");
        holder.txtLikes.setText(post.getLikes()+" Likes");
        String timeAgo = calculateTimeAgo(post.getDate());
        holder.txtDate.setText(timeAgo);  //here we will deal with date //*************//
        holder.txtDesc.setText(post.getDesc());


        holder.btnLike.setImageResource(
                post.isSelfLike()?R.drawable.ic_thumb_up:R.drawable.ic_thumb_up_off
        );

        holder.btnLike.setOnClickListener(v -> {
            holder.btnLike.setImageResource(
                    post.isSelfLike()?R.drawable.ic_thumb_up_off:R.drawable.ic_thumb_up
            );

            String token = sharedPreferences.getString("token", "");

            AndroidNetworking.post(Constant.LIKE_POST)
                    .addHeaders("Authorization", "Bearer "+token)
                    .addBodyParameter("id", post.getId()+"")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Post mPost = list.get(position);
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.getBoolean("success")) {
                                    mPost.setSelfLike(!post.isSelfLike());
                                    mPost.setLikes(mPost.isSelfLike()?post.getLikes()+1:post.getLikes()-1);
                                    list.set(position, mPost);
                                    notifyItemChanged(position);
                                    notifyDataSetChanged();
                                } else {
                                    holder.btnLike.setImageResource(
                                            post.isSelfLike()?R.drawable.ic_thumb_up:R.drawable.ic_thumb_up_off
                                    );
                                    notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        });

        int changeRole = sharedPreferences.getInt("role", 0);

        if (post.getUser().getId() == sharedPreferences.getInt("id", 0) || changeRole == 2) {
            holder.btnOption.setVisibility(View.VISIBLE);
        } else {
            holder.btnOption.setVisibility(View.GONE);
        }

        holder.txtComments.setOnClickListener(v -> {
            Intent intent = new Intent(((WelcomeActivity)context), CommentActivity.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("postPosition", position);
            context.startActivity(intent);
        });

        holder.btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(((WelcomeActivity)context), CommentActivity.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("postPosition", position);
            context.startActivity(intent);
        });

        holder.btnOption.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnOption);
            popupMenu.inflate(R.menu.menu_post_option);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_edit: {
                            Intent intent = new Intent(((WelcomeActivity)context), EditActivity.class);
                            /*--------------------------------------------------------*/
                            /*--------------------------------------------------------*/
                            intent.putExtra("postId", post.getId());
                            intent.putExtra("position", position);
                            intent.putExtra("desc", post.getDesc());
                            context.startActivity(intent);
                            return true;
                        }
                        case R.id.item_delete: {
                            deletePost(post.getId(), position);
                            return true;
                        }
                    }
                    return false;
                }
            });
            popupMenu.show();
        });
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

    private void deletePost(int postId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm");
        builder.setMessage("Delete post?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String token = sharedPreferences.getString("token", "");

                AndroidNetworking.post(Constant.DELETE_POST)
                        .addHeaders("Authorization", "Bearer "+token)
                        .addBodyParameter("id", postId+"")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (object.getBoolean("success")) {
                                        list.remove(position);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
                                        listAll.clear();
                                        listAll.addAll(list);
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
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Post> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(listAll);
            } else {
                for (Post post : listAll) {
                    if (post.getDesc().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                    post.getUser().getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(post);
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends Post>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    class PostsHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtDate, txtDesc, txtLikes, txtComments;
        private CircleImageView profileImage;
        private ImageView imgPost;
        private ImageButton btnOption, btnLike, btnComment;

        public PostsHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtPostName);
            txtDate = itemView.findViewById(R.id.txtPostDate);
            txtDesc = itemView.findViewById(R.id.txtPostDesc);
            txtLikes = itemView.findViewById(R.id.txt_likes);
            txtComments = itemView.findViewById(R.id.txt_comments);
            profileImage = itemView.findViewById(R.id.imgPostProfile);
            imgPost = itemView.findViewById(R.id.imgPostPhoto);
            btnOption = itemView.findViewById(R.id.postOption);
            btnLike = itemView.findViewById(R.id.postLikeButton);
            btnComment = itemView.findViewById(R.id.postCommentButton);
            btnOption.setVisibility(View.GONE);

        }
    }
}
