package org.kb10uy.bhavaagra;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class RhapsodyBuilder {
    static final String INTENT_RHAPSODY = "Rhapsody";
    static final String INTENT_RHAPSODY_RESUME = "RhapsodyResume";
    private Context mContext;
    private Rhapsody rhapsody;
    private Class mTarget = MediaSelectorActivity.class;

    RhapsodyBuilder(Context context) {
        mContext = context;
        rhapsody = new Rhapsody();
    }

    public RhapsodyBuilder count(int min, int max) {
        rhapsody.setCount(min, max);
        return this;
    }

    public RhapsodyBuilder minQuality(int width, int height) {
        rhapsody.setMinQuality(width, height);
        return this;
    }

    public RhapsodyBuilder maxQuality(int width, int height) {
        rhapsody.setMaxQuality(width, height);
        return this;
    }

    public RhapsodyBuilder resume(List<Uri> previous) {
        rhapsody.setPrevious(previous);
        return this;
    }

    public RhapsodyBuilder cameraPath(String pathPrefix) {
        rhapsody.setCameraImagePath(pathPrefix);
        return this;
    }

    public RhapsodyBuilder extendedActivity(Class<? extends MediaSelectorActivity> classObject) {
        mTarget = classObject;
        return this;
    }

    public Intent build() {
        Intent intent = new Intent(mContext, mTarget);
        ArrayList<Uri> r = (ArrayList<Uri>) rhapsody.getPrevious();
        rhapsody.setPrevious(null);
        intent.putExtra(INTENT_RHAPSODY, rhapsody);
        intent.putParcelableArrayListExtra(INTENT_RHAPSODY_RESUME, r);
        return intent;
    }
}
