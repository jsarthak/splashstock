package com.apps.jsarthak.splashstock.Data;

import android.content.Context;

import com.apps.jsarthak.splashstock.R;

import java.util.Map;

public class User {

    public String id, userName, name, firstName, lastName, twitterUserName, portfolioUrl, bio, location, instagramUserName;
    public String updatedAt;
    public int totalCollections, totalLikes, totalPhotos;
    public Links links = new Links();
    public ProfileImage profileImage = new ProfileImage();

    public class Links {
        public String self, html, photos, likes, portforlio, following, followers;

        public String getSelf() {
            return self;
        }

        public String getHtml() {
            return html;
        }

        public String getPhotos() {
            return photos;
        }

        public String getLikes() {
            return likes;
        }

        public String getPortforlio() {
            return portforlio;
        }

        public String getFollowing() {
            return following;
        }

        public String getFollowers() {
            return followers;
        }
    }

    public class ProfileImage {
        public String small, medium, large;

        public String getSmall() {
            return small;
        }

        public String getMedium() {
            return medium;
        }

        public String getLarge() {
            return large;
        }
    }

    public User(){

    }

    public User(Context context, Map<String, Object> photoMap) {

        this.id = photoMap.get(context.getString(R.string.user_id)).toString();
        this.userName = photoMap.get(context.getString(R.string.username)).toString();
        this.firstName = photoMap.get(context.getString(R.string.first_name)).toString();
        this.lastName = photoMap.get(context.getString(R.string.last_name)).toString();
        this.name = photoMap.get(context.getString(R.string.name)).toString();
        this.twitterUserName = photoMap.get(context.getString(R.string.twitter_username)).toString();
        this.instagramUserName = photoMap.get(context.getString(R.string.instagram_username)).toString();
        this.updatedAt = photoMap.get(context.getString(R.string.user_updated_at)).toString();
        this.portfolioUrl = photoMap.get(context.getString(R.string.portfolio_url)).toString();
        this.bio = photoMap.get(context.getString(R.string.bio)).toString();
        this.location = photoMap.get(context.getString(R.string.location)).toString();
        this.totalCollections = Integer.parseInt(photoMap.get(context.getString(R.string.total_collections)).toString());
        this.totalLikes = Integer.parseInt(photoMap.get(context.getString(R.string.total_likes)).toString());
        this.totalPhotos = Integer.parseInt(photoMap.get(context.getString(R.string.total_photos)).toString());
        this.links.followers = photoMap.get(context.getString(R.string.user_followers)).toString();
        this.links.following = photoMap.get(context.getString(R.string.user_followers)).toString();
        this.links.html = photoMap.get(context.getString(R.string.user_html)).toString();
        this.links.likes = photoMap.get(context.getString(R.string.user_likes)).toString();
        this.links.photos = photoMap.get(context.getString(R.string.user_photos)).toString();
        this.links.portforlio = photoMap.get(context.getString(R.string.user_portfolio)).toString();
        this.links.self = photoMap.get(context.getString(R.string.user_self)).toString();
        this.profileImage.large = photoMap.get(context.getString(R.string.user_large)).toString();
        this.profileImage.small = photoMap.get(context.getString(R.string.user_small)).toString();
        this.profileImage.medium = photoMap.get(context.getString(R.string.user_medium)).toString();
    }


    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTwitterUserName() {
        return twitterUserName;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getInstagramUserName() {
        return instagramUserName;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int getTotalCollections() {
        return totalCollections;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public int getTotalPhotos() {
        return totalPhotos;
    }

    public Links getLinks() {
        return links;
    }

    public ProfileImage getProfileImage() {
        return profileImage;
    }
}
