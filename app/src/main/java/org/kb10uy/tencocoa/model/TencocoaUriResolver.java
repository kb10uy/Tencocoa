package org.kb10uy.tencocoa.model;


import android.net.Uri;

import com.google.common.base.Optional;

import twitter4j.MediaEntity;
import twitter4j.URLEntity;

public final class TencocoaUriResolver {

    private static final TencocoaUriResolverInfo[] imageResolvers = {
            new TencocoaUriResolverInfo("^(https?://)(?:www\\.)?gyazo\\.com/(\\w+)(?:\\.png)?(?:\\?.*)?$", "$1i.gyazo.com/$2.png", "$1i.gyazo.com/$2.png"),
            new TencocoaUriResolverInfo("^http://www\\.nicovideo\\.jp/watch/(?:\\w+)?(\\d+)$", "http://tn-skr2.smilevideo.jp/smile?i=$1", "http://tn-skr2.smilevideo.jp/smile?i=$1"),
    };

    private static final TencocoaUriResolverInfo[] videoResolvers = {

    };

    private static final TencocoaUriResolverInfo[] textResolvers = {

    };

    private static final TencocoaUriResolverInfo[] otherResolvers = {

    };

    public static TencocoaUriInfo resolveUri(URLEntity entity) {
        TencocoaUriInfo info = new TencocoaUriInfo();
        info.setEmbeddedUri(Uri.parse(entity.getURL()));
        info.setDisplayUri(Uri.parse(entity.getDisplayURL()));
        info.setExpandedUri(Uri.parse(entity.getExpandedURL()));

        if (resolveAsImage(info)) return info;
        if (resolveAsVideo(info)) return info;
        if (resolveAsText(info)) return info;
        if (resolveAsOther(info)) return info;
        return info;
    }

    private static boolean resolveAsOther(TencocoaUriInfo info) {
        for (TencocoaUriResolverInfo resolver : otherResolvers) {
            Optional<Uri[]> uri = resolver.tryResolve(info.getExpandedUri());
            if (!uri.isPresent()) continue;
            info.setThumbnailImageUri(uri.get()[0]);
            info.setFullImageUri(uri.get()[1]);
            info.setType(TencocoaUriInfo.OTHER);
            return true;
        }
        return false;
    }

    private static boolean resolveAsText(TencocoaUriInfo info) {
        for (TencocoaUriResolverInfo resolver : textResolvers) {
            Optional<Uri[]> uri = resolver.tryResolve(info.getExpandedUri());
            if (!uri.isPresent()) continue;
            info.setThumbnailImageUri(uri.get()[0]);
            info.setFullImageUri(uri.get()[1]);
            info.setType(TencocoaUriInfo.TEXT);
            return true;
        }
        return false;
    }

    private static boolean resolveAsVideo(TencocoaUriInfo info) {
        for (TencocoaUriResolverInfo resolver : videoResolvers) {
            Optional<Uri[]> uri = resolver.tryResolve(info.getExpandedUri());
            if (!uri.isPresent()) continue;
            info.setThumbnailImageUri(uri.get()[0]);
            info.setFullImageUri(uri.get()[1]);
            info.setType(TencocoaUriInfo.VIDEO);
            return true;
        }
        return false;
    }

    private static boolean resolveAsImage(TencocoaUriInfo info) {
        for (TencocoaUriResolverInfo resolver : imageResolvers) {
            Optional<Uri[]> uri = resolver.tryResolve(info.getExpandedUri());
            if (!uri.isPresent()) continue;
            info.setThumbnailImageUri(uri.get()[0]);
            info.setFullImageUri(uri.get()[1]);
            info.setType(TencocoaUriInfo.IMAGE);
            return true;
        }
        return false;
    }

}
