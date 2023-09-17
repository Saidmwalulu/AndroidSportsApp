package com.sportcityapp.sportsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sportcityapp.sportsapp.EditUserActivity;
import com.sportcityapp.sportsapp.SetAdminActivity;
import com.sportcityapp.sportsapp.models.AllUsers;
import com.sportcityapp.sportsapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.AllUsersHolder> {
    private Context context;
    private ArrayList<AllUsers> allUsersList;

    public AllUsersAdapter(Context context, ArrayList<AllUsers> allUsersList) {
        this.context = context;
        this.allUsersList = allUsersList;
    }

    @NonNull
    @Override
    public AllUsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_all_users, parent, false);
        return new AllUsersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUsersHolder holder, int position) {
        AllUsers allUsers = allUsersList.get(position);

        Picasso.get().load(allUsers.getProfile_image_url()).into(holder.userImage);
        holder.userName.setText(allUsers.getName());
        int role = allUsers.getRole();
        if (role == 1) {
            holder.isAdmin.setVisibility(View.VISIBLE);
            holder.isAdmin.setText("Admin");
        } else if (role == 2) {
            holder.isAdmin.setVisibility(View.VISIBLE);
            holder.isAdmin.setText("Super Admin");
        }else{
            holder.isAdmin.setVisibility(View.GONE);
        }

        holder.option.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.option);
            popupMenu.inflate(R.menu.menu_super_admin_option);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(((SetAdminActivity)context), EditUserActivity.class);
                    int itemId = item.getItemId();
                    if (itemId == R.id.item_edit_user) {
                        intent.putExtra("position", position);
                        intent.putExtra("userId", allUsers.getId());
                        intent.putExtra("userName", allUsers.getName());
                        intent.putExtra("email", allUsers.getEmail());
                        intent.putExtra("role", allUsers.getRole());
                        context.startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return allUsersList.size();
    }

    class AllUsersHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView userName, isAdmin;
        private ImageButton option;

        public AllUsersHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.username);
            isAdmin = itemView.findViewById(R.id.isAdmin);
            option = itemView.findViewById(R.id.option);
        }
    }
}
