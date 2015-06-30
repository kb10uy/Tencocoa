package org.kb10uy.tencocoa.model;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;


public final class TwitterHelper {
    public static Twitter getTwitterInstance(String ck, String cs) {
        Twitter tw = new TwitterFactory().getInstance();
        tw.setOAuthConsumer(ck, cs);
        return tw;
    }
}
