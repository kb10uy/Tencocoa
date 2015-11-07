package org.kb10uy.bhavaagra;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class RhapsodyBuilder {
    static final String INTENT_RHAPSODY = "Rhapsody";
    private Context mContext;
    private Rhapsody rhapsody;

    RhapsodyBuilder(Context context, int result) {
        mContext = context;
        rhapsody = new Rhapsody(result);
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

    public Intent buildIntent() {
        Intent intent = new Intent(mContext, MediaSelectorActivity.class);
        intent.putExtra(INTENT_RHAPSODY, rhapsody);
        return intent;
    }
}
