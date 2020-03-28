package com.apps.jsarthak.splashstock.Data.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.jsarthak.splashstock.Data.Photo;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.UI.PhotoDetailActivity;
import com.apps.jsarthak.splashstock.UI.Profile.ProfileActivity;
import com.apps.jsarthak.splashstock.Widgets.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    Context mContext;
    ArrayList<Photo> photos;
    SharedPreferences sharedPreferences;
    public PhotoAdapter(Context mContext, ArrayList<Photo> photos) {
        this.mContext = mContext;
        this.photos = photos;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_list_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoViewHolder holder, int position) {
        final Photo photo = photos.get(position);
        String photoDisplayUrl;

        switch (sharedPreferences.getString("load_quality", "Regular")){
            case "Raw":
                photoDisplayUrl = photo.urls.raw;
                break;
            case "Full":
                photoDisplayUrl = photo.urls.full;
                break;
            case "Regular":
                photoDisplayUrl = photo.urls.regular;
                break;
            case "Small":
                photoDisplayUrl = photo.urls.small;
                break;
            case "Thumb":
                photoDisplayUrl = photo.urls.thumb;
                break;
            default:
                photoDisplayUrl = photo.urls.regular;
        }

        int height = photo.height;
        int width = photo.width;

        holder.wallpaperImage.setBackgroundColor(Color.parseColor(photo.color));

        Picasso.get().load(photoDisplayUrl).into(holder.wallpaperImage);

        DisplayMetrics displaymetrics = mContext.getResources().getDisplayMetrics();
        float finalHeight = displaymetrics.widthPixels / ((float)width/(float)height);
        holder.wallpaperImage.setMinimumHeight((int) finalHeight);
        holder.userName.setText(photo.user.userName);
        Picasso.get().load(photo.user.profileImage.small).placeholder(R.drawable.ic_user).fit().centerCrop().transform(new CropCircleTransformation()).into(holder.profileImage);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailActivityIntent = new Intent(mContext, PhotoDetailActivity.class);
                detailActivityIntent.putExtra(mContext.getString(R.string.urls), photo.urls.regular);
                detailActivityIntent.putExtra(mContext.getString(R.string.height), photo.height);
                detailActivityIntent.putExtra(mContext.getString(R.string.width), photo.width);
                detailActivityIntent.putExtra(mContext.getString(R.string.id), photo.id);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, holder.wallpaperImage, "wallpaper");
                    mContext.startActivity(detailActivityIntent, options.toBundle());
                } else{
                    mContext.startActivity(detailActivityIntent);
                }
            }
        });

        holder.userContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                profileIntent.putExtra(mContext.getString(R.string.username), photo.user.userName);
                mContext.startActivity(profileIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder{
        ImageView wallpaperImage, profileImage;
        TextView userName;
        LinearLayout userContainer;
        CardView container;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            wallpaperImage = itemView.findViewById(R.id.iv_editorial_item_wallpaper);
            profileImage = itemView.findViewById(R.id.iv_editorial_item_user_image);
            userName = itemView.findViewById(R.id.tv_editorial_item_user_name);
            userContainer = itemView.findViewById(R.id.ll_editorial_user_container);
            container = itemView.findViewById(R.id.editorial_list_item_container);
        }
    }
}
