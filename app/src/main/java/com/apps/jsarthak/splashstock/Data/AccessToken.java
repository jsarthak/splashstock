package com.apps.jsarthak.splashstock.Data;

import android.content.Context;

import com.apps.jsarthak.splashstock.R;

import java.util.Map;

public class AccessToken {

    public String accessToken, refreshToken, scope, tokenType;
    public long createdAt;

    public AccessToken(Context context, Map<String, Object> map) {

        accessToken = map.get(context.getString(R.string.access_token)).toString();
        refreshToken = map.get(context.getString(R.string.refresh_token)).toString();
        scope = map.get(context.getString(R.string.scope)).toString();
        tokenType = map.get(context.getString(R.string.token_type)).toString();
        createdAt = Long.parseLong(map.get(context.getString(R.string.token_created_at)).toString());

    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}