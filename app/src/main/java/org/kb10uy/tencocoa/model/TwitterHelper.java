package org.kb10uy.tencocoa.model;

import android.content.SharedPreferences;

import org.kb10uy.tencocoa.R;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

/**
 * Created by kb10uy on 2015/06/01.
 */
public final class TwitterHelper {
    public static Twitter getTwitterInstance(String ck, String cs) {
        Twitter tw = new TwitterFactory().getInstance();
        tw.setOAuthConsumer(ck, cs);
        return tw;
    }
}
