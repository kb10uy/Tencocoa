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
    private long userID;
    private String screenName;

    public TwitterAccountInformation(AccessToken token) {
        accessToken=token.getToken();
        accessTokenSecret=token.getTokenSecret();
        userID=token.getUserId();
        screenName=token.getScreenName();
    }
}
