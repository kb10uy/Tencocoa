package org.kb10uy.tencocoa.model;


import android.net.Uri;

import com.google.common.base.Optional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TencocoaUriResolverInfo {
    private Pattern uriPattern;
    private String displayReplacePattern;
    private String fullReplacePattern;

    public TencocoaUriResolverInfo(String uri, String thumb, String full) {
        uriPattern = Pattern.compile(uri);
        displayReplacePattern = thumb;
        fullReplacePattern = full;
    }

    public Optional<Uri[]> tryResolve(Uri expandedUri) {
        Matcher matcher = uriPattern.matcher(expandedUri.toString());
        if (!matcher.find()) return Optional.<Uri[]>absent();
        Uri ret[] = new Uri[2];
        ret[0] = Uri.parse(matcher.replaceFirst(displayReplacePattern));
        ret[1] = Uri.parse(matcher.replaceFirst(fullReplacePattern));
        return Optional.of(ret);
    }
}
