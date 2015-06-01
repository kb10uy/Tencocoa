package org.kb10uy.tencocoa.model;

import java.io.Serializable;

import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;

/**
 * Created by kb10uy on 2015/05/31.
 */
public class TwitterAccountInformation implements Serializable {
    private static final long serialVersionUID = 2591925210205947107L;

    private String accessTokenSecret;
    private String accessToken;
    private long userId;
    private String screenName;

    public TwitterAccountInformation(AccessToken token) {
        accessToken=token.getToken();
        accessTokenSecret=token.getTokenSecret();
        userId=token.getUserId();
        screenName=token.getScreenName();
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUserId() {
        return userId;
    }
}
