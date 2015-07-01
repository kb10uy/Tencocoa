package org.kb10uy.tencocoa.model;

import twitter4j.Status;


public interface HomeTimeLineLister {
    void onHomeTimeLineStatus(Status status);

    void onFavorite(Status status);
    void onUnfavorite(Status status);
}
