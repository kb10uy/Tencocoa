package org.kb10uy.tencocoa.model;

import twitter4j.Status;
import twitter4j.User;


public class TencocoaStatus {
    private Status sourceStatus;
    private Status showingStatus;
    private boolean isRetweet;
    private User retweeter;

    public TencocoaStatus(Status s) {
        sourceStatus = s;
        showingStatus = (isRetweet = s.isRetweet()) ? s.getRetweetedStatus() : sourceStatus;
    }

    public Status getShowingStatus() {
        return showingStatus;
    }

    public boolean isRetweet() {
        return isRetweet;
    }

    public User getRetweeter() {
        return retweeter;
    }
}
