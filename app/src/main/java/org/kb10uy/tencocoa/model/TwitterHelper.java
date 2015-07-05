package org.kb10uy.tencocoa.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import twitter4j.Dispatcher;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public final class TwitterHelper {

    public static Twitter getTwitterInstance(String ck, String cs, AccessToken token) {
        Configuration tencocoaConfig = new ConfigurationBuilder()
                .setGZIPEnabled(true)
                .setDispatcherImpl(TencocoaDispatcher.class.getName())
                .setOAuthConsumerKey(ck)
                .setOAuthConsumerSecret(cs)
                .setOAuthAccessToken(token.getToken())
                .setOAuthAccessTokenSecret(token.getTokenSecret())
                .build();
        Twitter tw = new TwitterFactory(tencocoaConfig).getInstance();
        return tw;
    }

    public static TwitterStream getTwitterStreamInstance(String ck, String cs, AccessToken token) {
        Configuration tencocoaConfig = new ConfigurationBuilder()
                .setGZIPEnabled(true)
                .setDispatcherImpl(TencocoaDispatcher.class.getName())
                .setOAuthConsumerKey(ck)
                .setOAuthConsumerSecret(cs)
                .setOAuthAccessToken(token.getToken())
                .setOAuthAccessTokenSecret(token.getTokenSecret())
                .build();
        TwitterStream tw = new TwitterStreamFactory(tencocoaConfig).getInstance();
        return tw;
    }

}
