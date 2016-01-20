package org.kb10uy.tencocoa.model;

import android.net.Uri;

public class TencocoaUriInfo {
    public static final int NORMAL = 1;
    public static final int IMAGE = 2;
    public static final int VIDEO = 3;
    public static final int TEXT = 4;
    public static final int OTHER = 5;

    private int type;

    private Uri embeddedUri;
    private Uri displayUri;
    private Uri expandedUri;
    private Uri thumbnailImageUri;
    private Uri fullImageUri;

    public TencocoaUriInfo() {
        type = NORMAL;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Uri getThumbnailImageUri() {
        return thumbnailImageUri;
    }

    public void setThumbnailImageUri(Uri thumbnailImageUri) {
        this.thumbnailImageUri = thumbnailImageUri;
    }

    public Uri getDisplayUri() {

        return displayUri;
    }

    public void setDisplayUri(Uri displayUri) {
        this.displayUri = displayUri;
    }

    public Uri getEmbeddedUri() {
        return embeddedUri;
    }

    public void setEmbeddedUri(Uri embeddedUri) {
        this.embeddedUri = embeddedUri;
    }

    public Uri getExpandedUri() {
        return expandedUri;
    }

    public void setExpandedUri(Uri expandedUri) {
        this.expandedUri = expandedUri;
    }

    public Uri getFullImageUri() {
        return fullImageUri;
    }

    public void setFullImageUri(Uri fullImageUri) {
        this.fullImageUri = fullImageUri;
    }
}
