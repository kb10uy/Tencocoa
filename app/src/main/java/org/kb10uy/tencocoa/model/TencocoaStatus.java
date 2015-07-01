package org.kb10uy.tencocoa.model;

import java.io.Serializable;

import twitter4j.Status;
import twitter4j.User;


public class TencocoaStatus implements Serializable {
    private static final long serialVersionUID = 3498526950072675391L;
    private Status sourceStatus;
    private Status showingStatus;
    private boolean isRetweet;
    private boolean isFavorited;
    private boolean isRetweeted;
    private User mRetweeter;

    public TencocoaStatus(Status s) {
        sourceStatus = s;
        showingStatus = (isRetweet = s.isRetweet()) ? s.getRetweetedStatus() : sourceStatus;
        if (isRetweet()) {
            mRetweeter = sourceStatus.getUser();
        }
    }

    public Status getSourceStatus() {
        return sourceStatus;
    }

    public Status getShowingStatus() {
        return showingStatus;
    }

    public boolean isRetweet() {
        return isRetweet;
    }

    public User getRetweeter() {
        return mRetweeter;
    }

    public void favorite() {
        isFavorited = true;
    }

    public void unfavorite() {
        isFavorited = false;
    }

    public boolean isFavorited() {
        return isFavorited;
    }
}
