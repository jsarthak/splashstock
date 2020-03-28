package com.apps.jsarthak.splashstock.Data;

import android.content.Context;

import com.apps.jsarthak.splashstock.R;

import java.util.Map;

public class Photo {


    public String id, color, description, slug;
    public String createdAt, updatedAt;
    public int width, height, likes;
    public String[] categories;
    public String[] currentUserCollections;
    public boolean sponsored, likedByUser;
    public Urls urls = new Urls();
    public Links links = new Links();
    public User user = new User();
    public int views, downloads;
    public Exif exif = new Exif();
    public PhotoLocation location = new PhotoLocation();


    public Photo(Context context, Map<String, Object> photoMap, boolean detail) {
        if (detail){
            this.id = photoMap.get(context.getString(R.string.id)).toString();
            this.createdAt = photoMap.get(context.getString(R.string.created_at)).toString();
            this.updatedAt = photoMap.get(context.getString(R.string.updated_at)).toString();
            this.color = photoMap.get(context.getString(R.string.color)).toString();
            this.slug = photoMap.get(context.getString(R.string.slug)).toString();
            this.height = Integer.parseInt(photoMap.get(context.getString(R.string.height)).toString());
            this.width = Integer.parseInt(photoMap.get(context.getString(R.string.width)).toString());
            this.likes = Integer.parseInt(photoMap.get(context.getString(R.string.likes)).toString());
            this.description = photoMap.get(context.getString(R.string.description)).toString();
//        this.categories = (String[]) photoMap.get(context.getString(R.string.categories));
            this.sponsored = (boolean) photoMap.get(context.getString(R.string.sponsored));
            this.likedByUser = (boolean) photoMap.get(context.getString(R.string.liked_by_user));
            //     this.currentUserCollections = (String[]) photoMap.get(context.getString(R.string.current_user_collections));
            this.urls.raw = photoMap.get(context.getString(R.string.raw)).toString();
            this.urls.full = photoMap.get(context.getString(R.string.full)).toString();
            this.urls.regular = photoMap.get(context.getString(R.string.regular)).toString();
            this.urls.small = photoMap.get(context.getString(R.string.small)).toString();
            this.urls.thumb = photoMap.get(context.getString(R.string.thumb)).toString();
            this.links.html = photoMap.get(context.getString(R.string.html)).toString();
            this.links.download = photoMap.get(context.getString(R.string.download)).toString();
            this.links.self = photoMap.get(context.getString(R.string.self)).toString();
            this.links.downloadLocation = photoMap.get(context.getString(R.string.download_location)).toString();

            this.user.id = photoMap.get(context.getString(R.string.user_id)).toString();
            this.user.userName = photoMap.get(context.getString(R.string.username)).toString();
            this.user.firstName = photoMap.get(context.getString(R.string.first_name)).toString();
            this.user.lastName = photoMap.get(context.getString(R.string.last_name)).toString();
            this.user.name = photoMap.get(context.getString(R.string.name)).toString();
            this.user.twitterUserName = photoMap.get(context.getString(R.string.twitter_username)).toString();
            this.user.instagramUserName = photoMap.get(context.getString(R.string.instagram_username)).toString();
            this.user.updatedAt = photoMap.get(context.getString(R.string.user_updated_at)).toString();
            this.user.portfolioUrl = photoMap.get(context.getString(R.string.portfolio_url)).toString();
            this.user.bio = photoMap.get(context.getString(R.string.bio)).toString();
            this.user.location = photoMap.get(context.getString(R.string.location)).toString();
            this.user.totalCollections = Integer.parseInt(photoMap.get(context.getString(R.string.total_collections)).toString());
            this.user.totalLikes = Integer.parseInt(photoMap.get(context.getString(R.string.total_likes)).toString());
            this.user.totalPhotos = Integer.parseInt(photoMap.get(context.getString(R.string.total_photos)).toString());
            this.user.links.followers = photoMap.get(context.getString(R.string.user_followers)).toString();
            this.user.links.following = photoMap.get(context.getString(R.string.user_followers)).toString();
            this.user.links.html = photoMap.get(context.getString(R.string.user_html)).toString();
            this.user.links.likes = photoMap.get(context.getString(R.string.user_likes)).toString();
            this.user.links.photos = photoMap.get(context.getString(R.string.user_photos)).toString();
            this.user.links.portforlio = photoMap.get(context.getString(R.string.user_portfolio)).toString();
            this.user.links.self = photoMap.get(context.getString(R.string.user_self)).toString();
            this.user.profileImage.large = photoMap.get(context.getString(R.string.user_large)).toString();
            this.user.profileImage.small = photoMap.get(context.getString(R.string.user_small)).toString();
            this.user.profileImage.medium = photoMap.get(context.getString(R.string.user_medium)).toString();

            this.location.city = photoMap.get(context.getString(R.string.city)).toString();
            this.location.country = photoMap.get(context.getString(R.string.country)).toString();
            this.location.name = photoMap.get(context.getString(R.string.photo_location)).toString();
            this.location.title = photoMap.get(context.getString(R.string.location_title)).toString();
            this.exif.aperture = photoMap.get(context.getString(R.string.aperture)).toString();
            this.exif.exposureTime = photoMap.get(context.getString(R.string.exposure_time)).toString();
            this.exif.focalLength = photoMap.get(context.getString(R.string.focal_length)).toString();
            this.exif.iso = photoMap.get(context.getString(R.string.iso)).toString();
            this.exif.make = photoMap.get(context.getString(R.string.make)).toString();
            this.exif.model = photoMap.get(context.getString(R.string.model)).toString();
            this.views = Integer.parseInt(photoMap.get(context.getString(R.string.views)).toString());
            this.downloads = Integer.parseInt(photoMap.get(context.getString(R.string.downloads)).toString());
        } else{
            this.id = photoMap.get(context.getString(R.string.id)).toString();
            this.createdAt = photoMap.get(context.getString(R.string.created_at)).toString();
            this.updatedAt = photoMap.get(context.getString(R.string.updated_at)).toString();
            this.color = photoMap.get(context.getString(R.string.color)).toString();
            this.slug = photoMap.get(context.getString(R.string.slug)).toString();
            this.height = Integer.parseInt(photoMap.get(context.getString(R.string.height)).toString());
            this.width = Integer.parseInt(photoMap.get(context.getString(R.string.width)).toString());
            this.likes = Integer.parseInt(photoMap.get(context.getString(R.string.likes)).toString());
            this.description = photoMap.get(context.getString(R.string.description)).toString();
//        this.categories = (String[]) photoMap.get(context.getString(R.string.categories));
            this.sponsored = (boolean) photoMap.get(context.getString(R.string.sponsored));
            this.likedByUser = (boolean) photoMap.get(context.getString(R.string.liked_by_user));
            //     this.currentUserCollections = (String[]) photoMap.get(context.getString(R.string.current_user_collections));
            this.urls.raw = photoMap.get(context.getString(R.string.raw)).toString();
            this.urls.full = photoMap.get(context.getString(R.string.full)).toString();
            this.urls.regular = photoMap.get(context.getString(R.string.regular)).toString();
            this.urls.small = photoMap.get(context.getString(R.string.small)).toString();
            this.urls.thumb = photoMap.get(context.getString(R.string.thumb)).toString();
            this.links.html = photoMap.get(context.getString(R.string.html)).toString();
            this.links.download = photoMap.get(context.getString(R.string.download)).toString();
            this.links.self = photoMap.get(context.getString(R.string.self)).toString();
            this.links.downloadLocation = photoMap.get(context.getString(R.string.download_location)).toString();
            this.user.id = photoMap.get(context.getString(R.string.user_id)).toString();
            this.user.userName = photoMap.get(context.getString(R.string.username)).toString();
            this.user.firstName = photoMap.get(context.getString(R.string.first_name)).toString();
            this.user.lastName = photoMap.get(context.getString(R.string.last_name)).toString();
            this.user.name = photoMap.get(context.getString(R.string.name)).toString();
            this.user.twitterUserName = photoMap.get(context.getString(R.string.twitter_username)).toString();
            this.user.instagramUserName = photoMap.get(context.getString(R.string.instagram_username)).toString();
            this.user.updatedAt = photoMap.get(context.getString(R.string.user_updated_at)).toString();
            this.user.portfolioUrl = photoMap.get(context.getString(R.string.portfolio_url)).toString();
            this.user.bio = photoMap.get(context.getString(R.string.bio)).toString();
            this.user.location = photoMap.get(context.getString(R.string.location)).toString();
            this.user.totalCollections = Integer.parseInt(photoMap.get(context.getString(R.string.total_collections)).toString());
            this.user.totalLikes = Integer.parseInt(photoMap.get(context.getString(R.string.total_likes)).toString());
            this.user.totalPhotos = Integer.parseInt(photoMap.get(context.getString(R.string.total_photos)).toString());
            this.user.links.followers = photoMap.get(context.getString(R.string.user_followers)).toString();
            this.user.links.following = photoMap.get(context.getString(R.string.user_followers)).toString();
            this.user.links.html = photoMap.get(context.getString(R.string.user_html)).toString();
            this.user.links.likes = photoMap.get(context.getString(R.string.user_likes)).toString();
            this.user.links.photos = photoMap.get(context.getString(R.string.user_photos)).toString();
            this.user.links.portforlio = photoMap.get(context.getString(R.string.user_portfolio)).toString();
            this.user.links.self = photoMap.get(context.getString(R.string.user_self)).toString();
            this.user.profileImage.large = photoMap.get(context.getString(R.string.user_large)).toString();
            this.user.profileImage.small = photoMap.get(context.getString(R.string.user_small)).toString();
            this.user.profileImage.medium = photoMap.get(context.getString(R.string.user_medium)).toString();
        }
    }

    public Photo(Context context, Map<String, Object> photoMap, int type) {

    }

    public class Urls {
        public String raw, full, regular, small, thumb;

        public String getRaw() {
            return raw;
        }

        public String getFull() {
            return full;
        }

        public String getRegular() {
            return regular;
        }

        public String getSmall() {
            return small;
        }

        public String getThumb() {
            return thumb;
        }
    }

    public class Links {
        public String self, html, download, downloadLocation;

        public String getSelf() {
            return self;
        }

        public String getHtml() {
            return html;
        }

        public String getDownload() {
            return download;
        }

        public String getDownloadLocation() {
            return downloadLocation;
        }
    }

    public class Exif {
        public String make, model, exposureTime, aperture, focalLength, iso;
    }

    public class PhotoLocation {
        public String title, name, city, country;
        long latitude, longitude;
    }
}
