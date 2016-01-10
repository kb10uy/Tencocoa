package org.kb10uy.tencocoa.views;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
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

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewerActivity extends AppCompatActivity {
    ImageView mImageView;
    Uri targetUri;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Intent intent = getIntent();
        targetUri = Uri.parse(intent.getStringExtra("Uri"));
        mImageView = (ImageView) findViewById(R.id.ImageViewerImageView);
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setZoomable(true);
        mAttacher.setMaximumScale(4);
        Glide
                .with(this)
                .load(targetUri)
                /*.placeholder(R.drawable.placeholder)*/
                .into(new GlideDrawableImageViewTarget(mImageView) {
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
            String dls = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File dest = new File(dls, filename);
            req.setDestinationUri(Uri.fromFile(dest));
            manager.enqueue(req);
        });
        findViewById(R.id.ImageViewerButtonCopyLink).setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Uri", targetUri.toString()));
        });
    }
}
