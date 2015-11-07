package org.kb10uy.bhavaagra;

import android.net.Uri;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.List;

public final class Rhapsody implements Serializable {
    public static final int UNLIMITED = -1;
    public static final String CAMERA_DCIM = "/DCIM";
    public static final String CAMERA_DCIM_CAMERA = "/DCIM/Camera";

    private int mMinCount = 0, mMaxCount = 1;
    private int mMinWidth = UNLIMITED, mMaxWidth = UNLIMITED;
    private int mMinHeight = UNLIMITED, mMaxHeight = UNLIMITED;
    private String mCameraImagePath = CAMERA_DCIM_CAMERA;
    private List<Uri> mPrevious;

    public Rhapsody() {

    }

    public void setMinQuality(int width, int height) {
        mMinWidth = width;
        mMinHeight = height;
    }

    public int getMinWidth() {
        return mMinWidth;
    }

    public int getMinHeight() {
        return mMinHeight;
    }

    public void setMaxQuality(int width, int height) {
        mMaxWidth = width;
        mMaxHeight = height;
    }

    public int getMaxWidth() {
        return mMaxWidth;
    }

    public int getMaxHeight() {
        return mMaxHeight;
    }

    public void setCount(int min, int max) {
        mMinCount = min;
        mMaxCount = max;
    }

    public int getMinCount() {
        return mMinCount;
    }

    public int getMaxCount() {
        return mMaxCount;
    }

    public void setPrevious(List<Uri> list) {
        mPrevious = list;
    }

    public List<Uri> getPrevious() {
        return mPrevious;
    }

    public void setCameraImagePath(String path) {
        mCameraImagePath=path;
    }

    public String getCameraImagePath() {
        return mCameraImagePath;
    }
}
