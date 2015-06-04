package org.kb10uy.tencocoa.model;

import twitter4j.Status;


public class TencocoaStatus {
    private Status sourceStatus;

    public TencocoaStatus(Status s) {
        sourceStatus=s;
    }

    public Status getSourceStatus() {
        return sourceStatus;
    }
}
