package org.kb10uy.tencocoa.views;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.kb10uy.tencocoa.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewerActivity extends AppCompatActivity {
    ImageView mImageView;
    Uri targetUri;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        targetUri = (Uri) savedInstanceState.getSerializable("Uri");
        mImageView = (ImageView) findViewById(R.id.ImageViewerImageView);
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setZoomable(true);
        mAttacher.setMaximumScale(4);
        Glide.with(this).load(targetUri).into(new GlideDrawableImageViewTarget(mImageView) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                mAttacher.update();
            }
        });

        setListeners();
    }

    private void setListeners() {
        findViewById(R.id.ImageViewerButtonDownload).setOnClickListener(v -> {
            DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request req = new DownloadManager.Request(targetUri);
            String[] split = targetUri.toString().split("/");
            String filename = split[split.length - 1].split(":")[0];
            req.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "/" + filename);
        });
        findViewById(R.id.ImageViewerButtonCopyLink).setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Uri", targetUri.toString()));
        });
    }
}
