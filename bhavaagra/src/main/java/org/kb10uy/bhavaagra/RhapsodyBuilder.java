package org.kb10uy.bhavaagra;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class RhapsodyBuilder {
    public static final int UNLIMITED = -1;

    static final String MIN_COUNT = "MinCount";
    static final String MAX_COUNT = "MaxCount";
    static final String MIN_WIDTH = "MinWidth";
    static final String MIN_HEIGHT = "MinHeight";
    static final String MAX_WIDTH = "MaxWidth";
    static final String MAX_HEIGHT = "MaxHeight";
    static final String PREVIOUS_LIST = "PreviousList";
    static final String RESULT_CODE = "ResultCode";

    private Context mContext;
    private int mResultCode = 0;
    private int mMinCount = 0, mMaxCount = 1;
    private int mMinWidth = UNLIMITED, mMaxWidth = UNLIMITED;
    private int mMinHeight = UNLIMITED, mMaxHeight = UNLIMITED;
    private List<Uri> mPrevious;

    RhapsodyBuilder(Context context, int result) {
        mContext = context;
        mResultCode = result;
    }

    public RhapsodyBuilder count(int min, int max) {
        mMinCount = min;
        mMaxCount = max;
        return this;
    }

    public RhapsodyBuilder qualityMin(int width, int height) {
        mMaxWidth = width;
        mMaxHeight = height;
        return this;
    }

    public RhapsodyBuilder qualityMax(int width, int height) {
        mMaxWidth = width;
        mMaxHeight = height;
        return this;
    }

    public RhapsodyBuilder resume(List<Uri> previous) {
        mPrevious = previous;
        return this;
    }

    public Intent buildIntent() {
        Intent intent = new Intent(mContext, MediaSelectorActivity.class);
        intent.putExtra(RESULT_CODE, mResultCode);
        intent.putExtra(MIN_COUNT, mMinCount);
        intent.putExtra(MAX_COUNT, mMaxCount);
        intent.putExtra(MIN_WIDTH, mMinWidth);
        intent.putExtra(MIN_HEIGHT, mMinWidth);
        intent.putExtra(MAX_WIDTH, mMaxWidth);
        intent.putExtra(MAX_HEIGHT, mMaxHeight);
        intent.putStringArrayListExtra(PREVIOUS_LIST, toStringArrayList(mPrevious));
        return intent;
    }

    static ArrayList<String> toStringArrayList(List<Uri> list) {
        ArrayList<String> result = new ArrayList<>();
        if (list == null) return result;
        for (Uri u : list) result.add(u.toString());
        return result;
    }

    static List<Uri> toUriList(String[] arlist) {
        List<Uri> result = new ArrayList<>();
        for (String u : arlist) result.add(Uri.parse(u));
        return result;
    }
}
