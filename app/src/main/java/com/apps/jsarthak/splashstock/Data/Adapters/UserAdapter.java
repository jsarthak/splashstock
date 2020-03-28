package com.apps.jsarthak.splashstock.Data.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.jsarthak.splashstock.Data.User;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.UI.Profile.ProfileActivity;
import com.apps.jsarthak.splashstock.Widgets.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{


    Context mContext;
    ArrayList<User> userArrayList;

    public UserAdapter(Context mContext, ArrayList<User> userArrayList) {
        this.mContext = mContext;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(v);    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        final User user = userArrayList.get(position);

        Picasso.get().load(user.profileImage.medium).fit().centerCrop().transform(new CropCircleTransformation()).into(holder.profileImage);
        holder.name.setText(user.name);
        holder.username.setText(user.userName);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(mContext.getString(R.string.username), user.userName);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, holder.profileImage, mContext.getString(R.string.profile_image));
                    mContext.startActivity(intent, options.toBundle());
                } else{
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        CardView container;
        ImageView profileImage;
        TextView name, username;

        public UserViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.iv_user_item_userimage);
            container = itemView.findViewById(R.id.user_item_container);
            name = itemView.findViewById(R.id.tv_user_item_user_fullname);
            username = itemView.findViewById(R.id.tv_user_item_username);

        }
    }
}
