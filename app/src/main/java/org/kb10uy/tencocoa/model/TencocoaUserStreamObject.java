package org.kb10uy.tencocoa.model;

import twitter4j.Twitter;
import twitter4j.TwitterStream;

/**
 * Created by kb10uy on 2016/02/21.
 */
public class TencocoaUserStreamObject {
    private TwitterAccountInformation currentInformation;
    private TencocoaUserStreamLister listener;
    private TwitterStream stream;
    private boolean connected = false;
    private Twitter twitter;
    private String conk, cons;

    public TencocoaUserStreamObject(String ck, String cs) {
        conk = ck;
        cons = cs;
    }

    public void setNewAccount(TwitterAccountInformation info) {
        currentInformation = info;
        twitter = TwitterHelper.getTwitterInstance(conk, cons, currentInformation.getAccessToken());
    }

    public TwitterAccountInformation getAccountInformation() {
        return currentInformation;
    }

    public Twitter getTwitterInstance() {
        return twitter;
    }

    public boolean isRunning() {
        return connected;
    }

    public void start(TencocoaUserStreamLister l) {
        if (connected) {
            stream.clearListeners();
            TencocoaHelper.Run(stream::shutdown);
        }
        listener = l;
        stream = TwitterHelper.getTwitterStreamInstance(conk, cons, currentInformation.getAccessToken());
        stream.addListener(listener);
        stream.user();
        connected = true;
    }

    public void stop() {
        TencocoaHelper.Run(stream::shutdown);
        stream.removeListener(listener);
        connected = false;
    }
}
