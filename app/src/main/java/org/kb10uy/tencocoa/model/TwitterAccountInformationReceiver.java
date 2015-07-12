package org.kb10uy.tencocoa.model;


import twitter4j.User;

public interface TwitterAccountInformationReceiver {
    void onTwitterAccountInformationReceived(User info);
    long getTargetAccountId();
}
