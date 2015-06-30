package org.kb10uy.tencocoa.model;

import twitter4j.Status;


public interface HomeTimeLineLister {
    public void onHomeTimeLineStatus(Status status);
}
