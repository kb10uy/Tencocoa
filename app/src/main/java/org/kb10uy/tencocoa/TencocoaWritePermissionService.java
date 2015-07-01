package org.kb10uy.tencocoa;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import org.kb10uy.tencocoa.model.TwitterAccountInformation;
import org.kb10uy.tencocoa.model.TwitterHelper;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;


public class TencocoaWritePermissionService extends Service {

    private static final int TENCOCOA_WRITE_PERMISSION_NOTIFICATION_ID = 0xC0C0A3;

    private TwitterAccountInformation currentUser;
    private Twitter mTwitter;
    private NotificationManager mNotificationManager;
    private TencocoaWritePermissionServiceBinder mBinder = new TencocoaWritePermissionServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_name), 0);
        String ck = pref.getString(getString(R.string.preference_twitter_consumer_key), "");
        String cs = pref.getString(getString(R.string.preference_twitter_consumer_secret), "");
        mTwitter = TwitterHelper.getTwitterInstance(ck, cs);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void showNotification(int tickerStringId, int descriptionStringId) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.tencocoa_notify)
                .setTicker(getString(tickerStringId))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(descriptionStringId));
        mNotificationManager.cancelAll();
        mNotificationManager.notify(TENCOCOA_WRITE_PERMISSION_NOTIFICATION_ID, builder.build());
    }

    private void showNotification(String tickerString, String descriptionText) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.tencocoa_notify)
                .setTicker(tickerString)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(descriptionText);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(TENCOCOA_WRITE_PERMISSION_NOTIFICATION_ID, builder.build());
    }

    public void setTargetUser(TwitterAccountInformation info) {
        currentUser = info;
        mTwitter.setOAuthAccessToken(new AccessToken(info.getAccessToken(), info.getAccessTokenSecret()));
    }

    public void updateStatus(String statusText) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                if (currentUser == null) return null;
                try {
                    mTwitter.tweets().updateStatus(params[0]);
                    return "";
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return e.getErrorMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equals("")) {
                    showNotification(R.string.notification_update_status_success, R.string.notification_update_status_success);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.notification_update_status_fail));
                    sb.append(result);
                    showNotification(sb.toString(), sb.toString());
                }
            }
        };
        task.execute(statusText);
    }

    public void favoriteStatus(long id) {
        AsyncTask<Long, Void, String> task = new AsyncTask<Long, Void, String>() {
            @Override
            protected String doInBackground(Long... params) {
                if (currentUser == null) return null;
                try {
                    mTwitter.favorites().createFavorite(params[0]);
                    return "";
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return e.getErrorMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equals("")) {
                    showNotification(R.string.notification_favorite_status_success, R.string.notification_favorite_status_success);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.notification_favorite_status_fail));
                    sb.append(result);
                    showNotification(sb.toString(), sb.toString());
                }
            }
        };
        task.execute(id);
    }

    public void unfavoriteStatus(long id) {
        /*
        AsyncTask<Long, Void, String> task = new AsyncTask<Long, Void, String>() {
            @Override
            protected String doInBackground(Long... params) {
                if (currentUser == null) return null;
                try {
                    mTwitter.favorites().destroyFavorite(params[0].longValue());
                    return "";
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return e.getErrorMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equals("")) {
                    showNotification(R.string.notification_update_status_success, R.string.notification_update_status_success);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.notification_update_status_fail));
                    sb.append(result);
                    showNotification(sb.toString(), sb.toString());
                }
            }
        };
        task.execute(new Long(id));
        */
    }

    public void retweetStatus(long id) {
        AsyncTask<Long, Void, String> task = new AsyncTask<Long, Void, String>() {
            @Override
            protected String doInBackground(Long... params) {
                if (currentUser == null) return null;
                try {
                    mTwitter.retweetStatus(params[0]);
                    return "";
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return e.getErrorMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equals("")) {
                    showNotification(R.string.notification_retweet_status_success, R.string.notification_retweet_status_success);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.notification_retweet_status_fail));
                    sb.append(result);
                    showNotification(sb.toString(), sb.toString());
                }
            }
        };
        task.execute(id);
    }

    public void favrtStatus(long id) {
        favoriteStatus(id);
        retweetStatus(id);
    }

    public class TencocoaWritePermissionServiceBinder extends Binder {
        public TencocoaWritePermissionService getService() {
            return TencocoaWritePermissionService.this;
        }
    }
}
