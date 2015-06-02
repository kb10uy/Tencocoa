package org.kb10uy.tencocoa.workers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kb10uy on 2015/06/02.
 */
public class WebDrawableWorker extends AsyncTaskLoader<Drawable> {

    Uri targetUri;

    public WebDrawableWorker(Context context, Uri uri) {
        super(context);
        targetUri = uri;
    }

    @Override
    public Drawable loadInBackground() {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(new URL(targetUri.toString()).openStream(), "profile");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return drawable;
    }
}
