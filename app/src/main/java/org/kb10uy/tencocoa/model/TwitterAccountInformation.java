package org.kb10uy.tencocoa.model;

import java.io.Serializable;

import twitter4j.auth.AccessToken;

public class TwitterAccountInformation implements Serializable {
    private static final long serialVersionUID = 2591925210205947107L;

    private long userId;
    private String screenName;
    private AccessToken token;

    public TwitterAccountInformation(AccessToken token) {
        userId = token.getUserId();
        screenName = token.getScreenName();
        this.token = token;
    }

    public AccessToken getAccessToken() {
        return token;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUserId() {
        return userId;
    }
}
