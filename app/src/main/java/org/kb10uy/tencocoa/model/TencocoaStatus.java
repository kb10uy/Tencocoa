package org.kb10uy.tencocoa.model;

import java.io.Serializable;

import twitter4j.Status;
import twitter4j.User;


public class TencocoaStatus implements Serializable {
    private static final long serialVersionUID = 3498526950072675391L;
    private Status sourceStatus;
    private Status showingStatus;
    private boolean isRetweet;
    private User retweeter;

    public TencocoaStatus(Status s) {
        sourceStatus = s;
        showingStatus = (isRetweet = s.isRetweet()) ? s.getRetweetedStatus() : sourceStatus;
        if (isRetweet()) {
            retweeter = sourceStatus.getUser();
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
        return retweeter;
    }
}
