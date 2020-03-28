package com.apps.jsarthak.splashstock.Data;

import android.content.Context;
import android.util.Log;

import com.apps.jsarthak.splashstock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataProcessor {

    private static final String TAG=DataProcessor.class.getSimpleName();
    public Context mContext;

    public DataProcessor(Context mContext) {
        this.mContext = mContext;
    }

    public Photo processPhoto(JSONObject photo) {
        try {
            JSONObject user = photo.getJSONObject(mContext.getString(R.string.user));
            JSONObject urls = photo.getJSONObject(mContext.getString(R.string.urls));
            JSONObject links = photo.getJSONObject(mContext.getString(R.string.links));
            JSONObject user_links = user.getJSONObject(mContext.getString(R.string.links));
            JSONObject profileImage = user.getJSONObject(mContext.getString(R.string.profile_image));
            Map<String, Object> map = new HashMap<>();
            map.put(mContext.getString(R.string.id), photo.get(mContext.getString(R.string.id)));
            map.put(mContext.getString(R.string.created_at), photo.get(mContext.getString(R.string.created_at)));
            map.put(mContext.getString(R.string.updated_at), photo.get(mContext.getString(R.string.updated_at)));
            map.put(mContext.getString(R.string.width), photo.get(mContext.getString(R.string.width)));
            map.put(mContext.getString(R.string.height), photo.get(mContext.getString(R.string.height)));
            map.put(mContext.getString(R.string.color), photo.get(mContext.getString(R.string.color)));
            map.put(mContext.getString(R.string.description), photo.get(mContext.getString(R.string.description)));
            map.put(mContext.getString(R.string.raw), urls.get(mContext.getString(R.string.raw)));
            map.put(mContext.getString(R.string.full), urls.get(mContext.getString(R.string.full)));
            map.put(mContext.getString(R.string.regular), urls.get(mContext.getString(R.string.regular)));
            map.put(mContext.getString(R.string.small), urls.get(mContext.getString(R.string.small)));
            map.put(mContext.getString(R.string.thumb), urls.get(mContext.getString(R.string.thumb)));
            map.put(mContext.getString(R.string.self), links.get(mContext.getString(R.string.self)));
            map.put(mContext.getString(R.string.html), links.get(mContext.getString(R.string.html)));
            map.put(mContext.getString(R.string.download), links.get(mContext.getString(R.string.download)));
            map.put(mContext.getString(R.string.download_location), links.get(mContext.getString(R.string.download_location)));
            map.put(mContext.getString(R.string.categories), photo.get(mContext.getString(R.string.categories)));
            map.put(mContext.getString(R.string.sponsored), photo.get(mContext.getString(R.string.sponsored)));
            map.put(mContext.getString(R.string.likes), photo.get(mContext.getString(R.string.likes)));
            map.put(mContext.getString(R.string.liked_by_user), photo.get(mContext.getString(R.string.liked_by_user)));
            map.put(mContext.getString(R.string.current_user_collections), photo.get(mContext.getString(R.string.current_user_collections)));
            map.put(mContext.getString(R.string.slug), photo.get(mContext.getString(R.string.slug)));
            map.put(mContext.getString(R.string.user_id), user.get(mContext.getString(R.string.id)));
            map.put(mContext.getString(R.string.user_updated_at), user.get(mContext.getString(R.string.updated_at)));
            map.put(mContext.getString(R.string.username), user.get(mContext.getString(R.string.username)));
            map.put(mContext.getString(R.string.name), user.get(mContext.getString(R.string.name)));
            map.put(mContext.getString(R.string.first_name), user.get(mContext.getString(R.string.first_name)));
            map.put(mContext.getString(R.string.last_name), user.get(mContext.getString(R.string.last_name)));
            map.put(mContext.getString(R.string.twitter_username), user.get(mContext.getString(R.string.twitter_username)));
            map.put(mContext.getString(R.string.portfolio_url), user.get(mContext.getString(R.string.portfolio_url)));
            map.put(mContext.getString(R.string.bio), user.get(mContext.getString(R.string.bio)));
            map.put(mContext.getString(R.string.location), user.get(mContext.getString(R.string.location)));
            map.put(mContext.getString(R.string.user_self), user_links.get(mContext.getString(R.string.self)));
            map.put(mContext.getString(R.string.user_html), user_links.get(mContext.getString(R.string.html)));
            map.put(mContext.getString(R.string.user_photos), user_links.get(mContext.getString(R.string.user_photos)));
            map.put(mContext.getString(R.string.user_followers), user_links.get(mContext.getString(R.string.user_followers)));
            map.put(mContext.getString(R.string.user_following), user_links.get(mContext.getString(R.string.user_following)));
            map.put(mContext.getString(R.string.user_likes), user_links.get(mContext.getString(R.string.likes)));
            map.put(mContext.getString(R.string.user_portfolio), user_links.get(mContext.getString(R.string.user_portfolio)));
            map.put(mContext.getString(R.string.user_small), profileImage.get(mContext.getString(R.string.small)));
            map.put(mContext.getString(R.string.user_medium), profileImage.get(mContext.getString(R.string.user_medium)));
            map.put(mContext.getString(R.string.user_large), profileImage.get(mContext.getString(R.string.user_large)));
            map.put(mContext.getString(R.string.instagram_username), user.get(mContext.getString(R.string.instagram_username)));
            map.put(mContext.getString(R.string.total_photos), user.get(mContext.getString(R.string.total_photos)));
            map.put(mContext.getString(R.string.total_likes), user.get(mContext.getString(R.string.total_likes)));
            map.put(mContext.getString(R.string.total_collections), user.get(mContext.getString(R.string.total_collections)));
            Photo p = new Photo(mContext, map, false);
            return p;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Collection processCollection(JSONObject collection) {
        try {
            JSONObject photo = null;
            JSONObject urls = null;
            JSONObject links = null;
            if (!collection.isNull("cover_photo")){
                photo = collection.getJSONObject(("cover_photo"));
                urls = photo.getJSONObject(mContext.getString(R.string.urls));
                links = photo.getJSONObject(mContext.getString(R.string.links));
            }
            else if (collection.isNull("cover_photo")){
                photo = null;
                urls = null;
                links = null;
            }
            JSONObject user = collection.getJSONObject(mContext.getString(R.string.user));
            JSONObject user_links = user.getJSONObject(mContext.getString(R.string.links));
            JSONObject profileImage = user.getJSONObject(mContext.getString(R.string.profile_image));
            JSONArray tagsArray = collection.getJSONArray(mContext.getString(R.string.tags));
            ArrayList<String> tagsList = new ArrayList<>();
            for (int j = 0; j < tagsArray.length(); j++) {
                JSONObject tag = tagsArray.getJSONObject(j);
                tagsList.add(tag.getString(mContext.getString(R.string.title)));
            }
            Map<String, Object> map = new HashMap<>();
            map.put(mContext.getString(R.string.collection_id), collection.get(mContext.getString(R.string.id)));
            map.put(mContext.getString(R.string.title), collection.get(mContext.getString(R.string.title)));
            map.put(mContext.getString(R.string.collection_tags), tagsList);
            map.put(mContext.getString(R.string.collection_description), collection.get(mContext.getString(R.string.description)));
            map.put(mContext.getString(R.string.collection_published_at), collection.get(mContext.getString(R.string.collection_published_at)));
            map.put(mContext.getString(R.string.collection_updated_at), collection.get(mContext.getString(R.string.updated_at)));
            map.put(mContext.getString(R.string.collection_curated), collection.get(mContext.getString(R.string.curated)));
            map.put(mContext.getString(R.string.collection_featured), collection.get(mContext.getString(R.string.featured)));
            map.put(mContext.getString(R.string.collection_photo_count), collection.get(mContext.getString(R.string.total_photos)));
            map.put(mContext.getString(R.string.collection_private), collection.get(mContext.getString(R.string.isprivate)));
            map.put(mContext.getString(R.string.collection_share_key), collection.get(mContext.getString(R.string.share_key)));
            Map<String, Object> photoMap = new HashMap<>();
            if (photo != null) {
                photoMap.put(mContext.getString(R.string.id), photo.get(mContext.getString(R.string.id)));
                photoMap.put(mContext.getString(R.string.created_at), photo.get(mContext.getString(R.string.created_at)));
                photoMap.put(mContext.getString(R.string.updated_at), photo.get(mContext.getString(R.string.updated_at)));
                photoMap.put(mContext.getString(R.string.width), photo.get(mContext.getString(R.string.width)));
                photoMap.put(mContext.getString(R.string.height), photo.get(mContext.getString(R.string.height)));
                photoMap.put(mContext.getString(R.string.color), photo.get(mContext.getString(R.string.color)));
                photoMap.put(mContext.getString(R.string.description), photo.get(mContext.getString(R.string.description)));
                photoMap.put(mContext.getString(R.string.raw), urls.get(mContext.getString(R.string.raw)));
                photoMap.put(mContext.getString(R.string.full), urls.get(mContext.getString(R.string.full)));
                photoMap.put(mContext.getString(R.string.regular), urls.get(mContext.getString(R.string.regular)));
                photoMap.put(mContext.getString(R.string.small), urls.get(mContext.getString(R.string.small)));
                photoMap.put(mContext.getString(R.string.thumb), urls.get(mContext.getString(R.string.thumb)));
                photoMap.put(mContext.getString(R.string.self), links.get(mContext.getString(R.string.self)));
                photoMap.put(mContext.getString(R.string.html), links.get(mContext.getString(R.string.html)));
                photoMap.put(mContext.getString(R.string.download), links.get(mContext.getString(R.string.download)));
                photoMap.put(mContext.getString(R.string.download_location), links.get(mContext.getString(R.string.download_location)));
                photoMap.put(mContext.getString(R.string.categories), photo.get(mContext.getString(R.string.categories)));
                photoMap.put(mContext.getString(R.string.sponsored), photo.get(mContext.getString(R.string.sponsored)));
                photoMap.put(mContext.getString(R.string.likes), photo.get(mContext.getString(R.string.likes)));
                photoMap.put(mContext.getString(R.string.liked_by_user), photo.get(mContext.getString(R.string.liked_by_user)));
                photoMap.put(mContext.getString(R.string.current_user_collections), photo.get(mContext.getString(R.string.current_user_collections)));
                photoMap.put(mContext.getString(R.string.slug), photo.get(mContext.getString(R.string.slug)));
                photoMap.put(mContext.getString(R.string.user_id), user.get(mContext.getString(R.string.id)));
                photoMap.put(mContext.getString(R.string.user_updated_at), user.get(mContext.getString(R.string.updated_at)));
                photoMap.put(mContext.getString(R.string.username), user.get(mContext.getString(R.string.username)));
                photoMap.put(mContext.getString(R.string.name), user.get(mContext.getString(R.string.name)));
                photoMap.put(mContext.getString(R.string.first_name), user.get(mContext.getString(R.string.first_name)));
                photoMap.put(mContext.getString(R.string.last_name), user.get(mContext.getString(R.string.last_name)));
                photoMap.put(mContext.getString(R.string.twitter_username), user.get(mContext.getString(R.string.twitter_username)));
                photoMap.put(mContext.getString(R.string.portfolio_url), user.get(mContext.getString(R.string.portfolio_url)));
                photoMap.put(mContext.getString(R.string.bio), user.get(mContext.getString(R.string.bio)));
                photoMap.put(mContext.getString(R.string.location), user.get(mContext.getString(R.string.location)));
                photoMap.put(mContext.getString(R.string.user_self), user_links.get(mContext.getString(R.string.self)));
                photoMap.put(mContext.getString(R.string.user_html), user_links.get(mContext.getString(R.string.html)));
                photoMap.put(mContext.getString(R.string.user_photos), user_links.get(mContext.getString(R.string.user_photos)));
                photoMap.put(mContext.getString(R.string.user_followers), user_links.get(mContext.getString(R.string.user_followers)));
                photoMap.put(mContext.getString(R.string.user_following), user_links.get(mContext.getString(R.string.user_following)));
                photoMap.put(mContext.getString(R.string.user_likes), user_links.get(mContext.getString(R.string.likes)));
                photoMap.put(mContext.getString(R.string.user_portfolio), user_links.get(mContext.getString(R.string.user_portfolio)));
                photoMap.put(mContext.getString(R.string.user_small), profileImage.get(mContext.getString(R.string.small)));
                photoMap.put(mContext.getString(R.string.user_medium), profileImage.get(mContext.getString(R.string.user_medium)));
                photoMap.put(mContext.getString(R.string.user_large), profileImage.get(mContext.getString(R.string.user_large)));
                photoMap.put(mContext.getString(R.string.instagram_username), user.get(mContext.getString(R.string.instagram_username)));
                photoMap.put(mContext.getString(R.string.total_photos), user.get(mContext.getString(R.string.total_photos)));
                photoMap.put(mContext.getString(R.string.total_likes), user.get(mContext.getString(R.string.total_likes)));
                photoMap.put(mContext.getString(R.string.total_collections), user.get(mContext.getString(R.string.total_collections)));
                Collection c = new Collection(mContext, map, photoMap, true);
                return c;
            } else {
                photoMap.put(mContext.getString(R.string.user_id), user.get(mContext.getString(R.string.id)));
                photoMap.put(mContext.getString(R.string.user_updated_at), user.get(mContext.getString(R.string.updated_at)));
                photoMap.put(mContext.getString(R.string.username), user.get(mContext.getString(R.string.username)));
                photoMap.put(mContext.getString(R.string.name), user.get(mContext.getString(R.string.name)));
                photoMap.put(mContext.getString(R.string.first_name), user.get(mContext.getString(R.string.first_name)));
                photoMap.put(mContext.getString(R.string.last_name), user.get(mContext.getString(R.string.last_name)));
                photoMap.put(mContext.getString(R.string.twitter_username), user.get(mContext.getString(R.string.twitter_username)));
                photoMap.put(mContext.getString(R.string.portfolio_url), user.get(mContext.getString(R.string.portfolio_url)));
                photoMap.put(mContext.getString(R.string.bio), user.get(mContext.getString(R.string.bio)));
                photoMap.put(mContext.getString(R.string.location), user.get(mContext.getString(R.string.location)));
                photoMap.put(mContext.getString(R.string.user_self), user_links.get(mContext.getString(R.string.self)));
                photoMap.put(mContext.getString(R.string.user_html), user_links.get(mContext.getString(R.string.html)));
                photoMap.put(mContext.getString(R.string.user_photos), user_links.get(mContext.getString(R.string.user_photos)));
                photoMap.put(mContext.getString(R.string.user_followers), user_links.get(mContext.getString(R.string.user_followers)));
                photoMap.put(mContext.getString(R.string.user_following), user_links.get(mContext.getString(R.string.user_following)));
                photoMap.put(mContext.getString(R.string.user_likes), user_links.get(mContext.getString(R.string.likes)));
                photoMap.put(mContext.getString(R.string.user_portfolio), user_links.get(mContext.getString(R.string.user_portfolio)));
                photoMap.put(mContext.getString(R.string.user_small), profileImage.get(mContext.getString(R.string.small)));
                photoMap.put(mContext.getString(R.string.user_medium), profileImage.get(mContext.getString(R.string.user_medium)));
                photoMap.put(mContext.getString(R.string.user_large), profileImage.get(mContext.getString(R.string.user_large)));
                photoMap.put(mContext.getString(R.string.instagram_username), user.get(mContext.getString(R.string.instagram_username)));
                photoMap.put(mContext.getString(R.string.total_photos), user.get(mContext.getString(R.string.total_photos)));
                photoMap.put(mContext.getString(R.string.total_likes), user.get(mContext.getString(R.string.total_likes)));
                photoMap.put(mContext.getString(R.string.total_collections), user.get(mContext.getString(R.string.total_collections)));
                Collection c = new Collection(mContext, map, photoMap, false);
                return c;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User processUser(JSONObject user){
        try {
            JSONObject user_links = user.getJSONObject(mContext.getString(R.string.links));
            JSONObject profileImage = user.getJSONObject(mContext.getString(R.string.profile_image));
            Map<String, Object> map = new HashMap<>();
            map.put(mContext.getString(R.string.user_id), user.get(mContext.getString(R.string.id)));
            map.put(mContext.getString(R.string.user_updated_at), user.get(mContext.getString(R.string.updated_at)));
            map.put(mContext.getString(R.string.username), user.get(mContext.getString(R.string.username)));
            map.put(mContext.getString(R.string.name), user.get(mContext.getString(R.string.name)));
            map.put(mContext.getString(R.string.first_name), user.get(mContext.getString(R.string.first_name)));
            map.put(mContext.getString(R.string.last_name), user.get(mContext.getString(R.string.last_name)));
            map.put(mContext.getString(R.string.twitter_username), user.get(mContext.getString(R.string.twitter_username)));
            map.put(mContext.getString(R.string.portfolio_url), user.get(mContext.getString(R.string.portfolio_url)));
            map.put(mContext.getString(R.string.bio), user.get(mContext.getString(R.string.bio)));
            map.put(mContext.getString(R.string.location), user.get(mContext.getString(R.string.location)));
            map.put(mContext.getString(R.string.user_self), user_links.get(mContext.getString(R.string.self)));
            map.put(mContext.getString(R.string.user_html), user_links.get(mContext.getString(R.string.html)));
            map.put(mContext.getString(R.string.user_photos), user_links.get(mContext.getString(R.string.user_photos)));
            map.put(mContext.getString(R.string.user_followers), user_links.get(mContext.getString(R.string.user_followers)));
            map.put(mContext.getString(R.string.user_following), user_links.get(mContext.getString(R.string.user_following)));
            map.put(mContext.getString(R.string.user_likes), user_links.get(mContext.getString(R.string.likes)));
            map.put(mContext.getString(R.string.user_portfolio), user_links.get(mContext.getString(R.string.user_portfolio)));
            map.put(mContext.getString(R.string.user_small), profileImage.get(mContext.getString(R.string.small)));
            map.put(mContext.getString(R.string.user_medium), profileImage.get(mContext.getString(R.string.user_medium)));
            map.put(mContext.getString(R.string.user_large), profileImage.get(mContext.getString(R.string.user_large)));
            map.put(mContext.getString(R.string.instagram_username), user.get(mContext.getString(R.string.instagram_username)));
            map.put(mContext.getString(R.string.total_photos), user.get(mContext.getString(R.string.total_photos)));
            map.put(mContext.getString(R.string.total_likes), user.get(mContext.getString(R.string.total_likes)));
            map.put(mContext.getString(R.string.total_collections), user.get(mContext.getString(R.string.total_collections)));
            User u = new User(mContext, map);
            return u;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
