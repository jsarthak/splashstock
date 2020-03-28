package com.apps.jsarthak.splashstock.Data.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.jsarthak.splashstock.Data.Collection;
import com.apps.jsarthak.splashstock.Data.Photo;
import com.apps.jsarthak.splashstock.R;
import com.apps.jsarthak.splashstock.UI.Collection.CollectionActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    Context mContext;
    ArrayList<Collection> collectionArrayList;

    SharedPreferences sharedPreferences;

    public CollectionAdapter(Context mContext, ArrayList<Collection> photos) {
        this.mContext = mContext;
        this.collectionArrayList = photos;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.collection_list_item, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        final Collection collection = collectionArrayList.get(position);
        holder.title.setText(collection.title);
        holder.photoCount.setText(collection.totalPhotos + " Photos");

        if (collection.photo == null){
            holder.curator.setText("Curated by " + collection.user.name);
            return;
        } else{
            holder.curator.setText("Curated by " + collection.photo.user.name);
            String photoDisplayUrl;

            switch (sharedPreferences.getString("load_quality", "Regular")){
                case "Raw":
                    photoDisplayUrl = collection.photo.urls.raw;
                    break;
                case "Full":
                    photoDisplayUrl = collection.photo.urls.full;
                    break;
                case "Regular":
                    photoDisplayUrl = collection.photo.urls.regular;
                    break;
                case "Small":
                    photoDisplayUrl = collection.photo.urls.small;
                    break;
                case "Thumb":
                    photoDisplayUrl = collection.photo.urls.thumb;
                    break;
                default:
                    photoDisplayUrl = collection.photo.urls.regular;
            }

            holder.coverImage.setBackgroundColor(Color.parseColor(collection.photo.color));

            holder.coverImage.setBackgroundColor(Color.parseColor(collection.photo.color));
            Picasso.get().load(photoDisplayUrl).into(holder.coverImage);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CollectionActivity.class);
                intent.putExtra(mContext.getString(R.string.collection_id), collection.id);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collectionArrayList.size();
    }

    class CollectionViewHolder extends RecyclerView.ViewHolder{
        ImageView coverImage;
        TextView title, photoCount, curator;
        CardView container;
        public CollectionViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_collection_title);
            photoCount = itemView.findViewById(R.id.tv_collection_photo_count);
            curator = itemView.findViewById(R.id.tv_collection_curated_by);
            coverImage = itemView.findViewById(R.id.iv_collection_item_photo);
            container = itemView.findViewById(R.id.collection_item_container);
        }
    }
}
