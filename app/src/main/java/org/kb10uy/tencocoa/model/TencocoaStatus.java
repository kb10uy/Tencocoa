package org.kb10uy.tencocoa.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import twitter4j.ExtendedMediaEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;


public class TencocoaStatus implements Serializable {
    private static final long serialVersionUID = 3498526950072675391L;
    private Status sourceStatus;
    private Status showingStatus;
    private boolean isRetweet;
    private boolean isFavorited;
    private boolean isRetweeted;
    private User mRetweeter;
    private String replacedText;
    private ArrayList<Uri> uris;
    private ArrayList<TencocoaUriInfo> medias;
    private boolean hasMedia;


    public TencocoaStatus(Status s) {
        sourceStatus = s;
        showingStatus = (isRetweet = s.isRetweet()) ? s.getRetweetedStatus() : sourceStatus;
        if (isRetweet()) {
            mRetweeter = sourceStatus.getUser();
        }
        uris = new ArrayList<>();
        medias = new ArrayList<>();
        replaceTextElements();
        fetchMediaEntities();
        fetchExternalMedias();
    }

    public List<TencocoaUriInfo> getMedias() {
        return medias;
    }

    public Status getSourceStatus() {
        return sourceStatus;
    }

    public Status getShowingStatus() {
        return showingStatus;
    }

    public boolean hasMedia() {
        return hasMedia;
    }

    public boolean isRetweet() {
        return isRetweet;
    }

    public User getRetweeter() {
        return mRetweeter;
    }

    public void favorite() {
        isFavorited = true;
    }

    public void unfavorite() {
        isFavorited = false;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public String getReplacedText() {
        return replacedText;
    }

    private void replaceTextElements() {
        URLEntity[] urlEntities = showingStatus.getURLEntities();
        String text = showingStatus.getText();
        for (URLEntity e : urlEntities) {
            text = text.replace(e.getURL(), e.getDisplayURL());
            uris.add(Uri.parse(e.getExpandedURL()));
        }
        replacedText = text;
    }

    private void fetchMediaEntities() {
        MediaEntity[] mediaEntities = showingStatus.getMediaEntities();
        if (mediaEntities.length == 0) return;
        hasMedia = true;
        for (MediaEntity e : mediaEntities) {
            TencocoaUriInfo info = new TencocoaUriInfo();
            info.setType(TencocoaUriInfo.IMAGE);
            info.setEmbeddedUri(Uri.parse(e.getURL()));
            info.setDisplayUri(Uri.parse(e.getDisplayURL()));
            info.setExpandedUri(Uri.parse(e.getExpandedURL()));
            String mediaURLHttps = e.getMediaURLHttps();
            info.setThumbnailImageUri(Uri.parse(mediaURLHttps + ":thumb"));
            info.setFullImageUri(Uri.parse(mediaURLHttps + ":orig"));
            medias.add(info);
            replacedText = replacedText.replace(e.getURL(), e.getDisplayURL());
        }
        ExtendedMediaEntity[] extendedMediaEntities = showingStatus.getExtendedMediaEntities();
        if (extendedMediaEntities.length <= 1) return;
        medias.clear();
        for (ExtendedMediaEntity x : extendedMediaEntities) {
            TencocoaUriInfo xinfo = new TencocoaUriInfo();
            xinfo.setType(TencocoaUriInfo.IMAGE);
            xinfo.setEmbeddedUri(Uri.parse(x.getURL()));
            xinfo.setDisplayUri(Uri.parse(x.getDisplayURL()));
            xinfo.setExpandedUri(Uri.parse(x.getExpandedURL()));
            String mediaURLHttps = x.getMediaURLHttps();
            xinfo.setThumbnailImageUri(Uri.parse(mediaURLHttps + ":thumb"));
            xinfo.setFullImageUri(Uri.parse(mediaURLHttps + ":orig"));
            medias.add(xinfo);
        }
    }

    private void fetchExternalMedias() {
        URLEntity[] uris = showingStatus.getURLEntities();
        for (URLEntity e : uris) {
            TencocoaUriInfo info = TencocoaUriResolver.resolveUri(e);
            switch (info.getType()) {
                case TencocoaUriInfo.IMAGE:
                case TencocoaUriInfo.VIDEO:
                    medias.add(info);
                    break;
            }
        }
    }
}
