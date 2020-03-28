package com.apps.jsarthak.splashstock.Data;

import android.content.Context;

import com.apps.jsarthak.splashstock.R;

import java.util.ArrayList;
import java.util.Map;

public class Collection {


    public long id;
    public String title, description, publishedAt, updatedAt, shareKey;
    public boolean isCurated, isFeatured, isPrivate;
    public int totalPhotos;
    public ArrayList<String> tags;
    public User user;
    public Photo photo;

    public Collection(Context context, Map<String, Object> map, Map<String, Object> photoMap, boolean hasCover) {

        if (hasCover){
            this.id = Long.parseLong(map.get(context.getString(R.string.collection_id)).toString());
            this.title = map.get(context.getString(R.string.title)).toString();
            this.description = map.get(context.getString(R.string.collection_description)).toString();
            this.publishedAt = map.get(context.getString(R.string.collection_published_at)).toString();
            this.updatedAt = map.get(context.getString(R.string.collection_updated_at)).toString();
            this.shareKey = map.get(context.getString(R.string.collection_share_key)).toString();
            this.isCurated = Boolean.parseBoolean(map.get(context.getString(R.string.collection_curated)).toString());
            this.isFeatured = Boolean.parseBoolean(map.get(context.getString(R.string.collection_featured)).toString());
            this.isPrivate = Boolean.parseBoolean(map.get(context.getString(R.string.collection_private)).toString());
            this.totalPhotos = Integer.parseInt(map.get(context.getString(R.string.collection_photo_count)).toString());
            this.photo = new Photo(context, photoMap, false);
            this.tags = (ArrayList<String>) map.get(context.getString(R.string.collection_tags));
        }

        else{
            this.id = Long.parseLong(map.get(context.getString(R.string.collection_id)).toString());
            this.title = map.get(context.getString(R.string.title)).toString();
            this.description = map.get(context.getString(R.string.collection_description)).toString();
            this.publishedAt = map.get(context.getString(R.string.collection_published_at)).toString();
            this.updatedAt = map.get(context.getString(R.string.collection_updated_at)).toString();
            this.shareKey = map.get(context.getString(R.string.collection_share_key)).toString();
            this.isCurated = Boolean.parseBoolean(map.get(context.getString(R.string.collection_curated)).toString());
            this.isFeatured = Boolean.parseBoolean(map.get(context.getString(R.string.collection_featured)).toString());
            this.isPrivate = Boolean.parseBoolean(map.get(context.getString(R.string.collection_private)).toString());
            this.totalPhotos = Integer.parseInt(map.get(context.getString(R.string.collection_photo_count)).toString());
            this.tags = (ArrayList<String>) map.get(context.getString(R.string.collection_tags));
            this.user = new User(context, photoMap);
        }
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getShareKey() {
        return shareKey;
    }

    public boolean isCurated() {
        return isCurated;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public int getTotalPhotos() {
        return totalPhotos;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public User getUser() {
        return user;
    }

    public Photo getPhoto() {
        return photo;
    }
}
