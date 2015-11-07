package org.kb10uy.bhavaagra;

import android.net.Uri;

public final class RhapsodyImage {
    public static final int SOURCE_STORAGE = 1;
    public static final int SOURCE_INTERNET = 2;
    private Uri mImageUri;
    private int mSourceType;

    RhapsodyImage(Uri uri, int type) {
        mImageUri = uri;
        mSourceType = type;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public int getSourceType() {
        return mSourceType;
    }
}
