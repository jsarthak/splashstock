package com.apps.jsarthak.splashstock.Helper;


    import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

    import com.apps.jsarthak.splashstock.Data.AccessToken;
    import com.apps.jsarthak.splashstock.Data.User;
    import com.apps.jsarthak.splashstock.R;

public class LoginHelper {

        Context mContext;
        SharedPreferences mSharedPreferences;

        public LoginHelper(Context context){
            this.mContext = context;
            this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        public boolean isUserLogged() {
            if (mSharedPreferences.getString(mContext.getString(R.string.access_token), "").equals(null) || mSharedPreferences.getString(mContext.getString(R.string.access_token), "").equals("")){
                return false;
            }
            return true;
        }

        public void logout(){
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.clear().commit();
        }


        public void storeTokenAndUser(AccessToken accessToken, User user){
            String access_token_code = accessToken.getAccessToken();
            String tokenType = accessToken.getTokenType();
            String refreshToken = accessToken.getRefreshToken();
            String scope = accessToken.getScope();
            long createdAt = accessToken.getCreatedAt();
            String username = user.userName;
            String name = user.name;
            String profilePic = user.profileImage.large;
            String bio = user.bio;
            String insta = user.instagramUserName;
            String twitter = user.twitterUserName;
            String portfolio = user.portfolioUrl;
            int likes = user.totalLikes;
            int collection = user.totalCollections;
            int photos = user.totalPhotos;

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(mContext.getString(R.string.access_token), access_token_code)
                    .putString(mContext.getString(R.string.token_type), tokenType)
                    .putString(mContext.getString(R.string.refresh_token), refreshToken)
                    .putString(mContext.getString(R.string.scope), scope)
                    .putLong(mContext.getString(R.string.created_at), createdAt)
                    .putString(mContext.getString(R.string.username), username)
                    .putString(mContext.getString(R.string.name), name)
                    .putString(mContext.getString(R.string.profile_image), profilePic)
                    .putString(mContext.getString(R.string.bio), bio)
                    .putString(mContext.getString(R.string.instagram_username),insta)
                    .putString(mContext.getString(R.string.twitter_username), twitter)
                    .putString(mContext.getString(R.string.portfolio_url), portfolio)
                    .putInt(mContext.getString(R.string.total_likes), likes)
                    .putInt(mContext.getString(R.string.total_collections), collection)
                    .putInt(mContext.getString(R.string.total_photos), photos)
                    .putString(mContext.getString(R.string.location), user.location)
                    .apply();
        }

}
