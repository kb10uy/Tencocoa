package org.kb10uy.tencocoa.model;

import twitter4j.Status;

/**
 * Created by kb10uy on 2015/06/04.
 */
public interface HomeTimeLineLister {
    public void onHomeTimeLineStatus(Status status);
}
