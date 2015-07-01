package org.kb10uy.tencocoa.model;


import android.content.Context;

import io.realm.Realm;

public final class TencocoaDatabaseHelper {
    public static boolean checkFavoritedStatus(Realm realm, long id) {
        TencocoaStatusCache statusCache = realm.where(TencocoaStatusCache.class).equalTo("statusId", id).findFirst();
        if (statusCache == null) return false;
        return statusCache.isFavorited();
    }

    public static boolean checkRetweetedStatus(Realm realm, long id) {
        TencocoaStatusCache statusCache = realm.where(TencocoaStatusCache.class).equalTo("statusId", id).findFirst();
        if (statusCache == null) return false;
        return statusCache.isRetweeted();
    }
}
