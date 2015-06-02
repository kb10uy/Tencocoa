package org.kb10uy.tencocoa.views;

import android.app.LoaderManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.widget.ImageView;


import org.kb10uy.tencocoa.workers.WebDrawableWorker;

/**
 * Created by kb10uy on 2015/06/02.
 */
public class AsyncImageView extends ImageView implements LoaderCallbacks<Drawable> {

    Uri targetUri;

    public AsyncImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AsyncImageView(Context context) {
        super(context);
    }

    public void setImageUri(Uri uri) {
        targetUri = uri;
    }

    @Override
    public Loader<Drawable> onCreateLoader(int id, Bundle args) {
        WebDrawableWorker worker = new WebDrawableWorker(getContext(), targetUri);
        worker.forceLoad();
        return worker;
    }

    @Override
    public void onLoadFinished(Loader<Drawable> loader, Drawable drawable) {
        setImageDrawable(drawable);
    }

    @Override
    public void onLoaderReset(Loader<Drawable> loader) {

    }
}
