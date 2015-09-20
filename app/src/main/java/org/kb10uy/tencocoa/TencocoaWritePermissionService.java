package org.kb10uy.tencocoa;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.kb10uy.tencocoa.model.TwitterAccountInformation;
import org.kb10uy.tencocoa.model.TwitterHelper;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;


public class TencocoaWritePermissionService extends Service {

    private static final int TENCOCOA_WRITE_PERMISSION_NOTIFICATION_ID = 0xC0C0A3;

    private TwitterAccountInformation currentUser;
    private Twitter mTwitter;
    private NotificationManager mNotificationManager;
    private TencocoaWritePermissionServiceBinder mBinder = new TencocoaWritePermissionServiceBinder();
    SharedPreferences pref;
    boolean showNotificationsAsToast;

    @Override
    public void onCreate() {
        super.onCreate();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        showNotificationsAsToast = pref.getBoolean(getString(R.string.preference_general_behavior_write_as_toast), true);
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

    private void showNotification(String tickerString, String descriptionText) {
        if (showNotificationsAsToast) {
            Toast.makeText(getApplicationContext(), descriptionText, Toast.LENGTH_SHORT).show();
        } else {
            Notification.Builder builder = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.tencocoa_notify)
                    .setTicker(tickerString)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(descriptionText);
            mNotificationManager.cancelAll();
            mNotificationManager.notify(TENCOCOA_WRITE_PERMISSION_NOTIFICATION_ID, builder.build());
        }
    }

    public void setTarget(Twitter twitter, TwitterAccountInformation info) {
        currentUser = info;
        mTwitter = twitter;
    }

    public void updateStatus(String statusText) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                if (currentUser == null)
                    return getString(R.string.notification_network_unavailable);
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
                    showNotification(getString(R.string.notification_update_status_success), getString(R.string.notification_update_status_success));
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

    public void updateStatus(StatusUpdate status) {
        AsyncTask<StatusUpdate, Void, String> task = new AsyncTask<StatusUpdate, Void, String>() {
            @Override
            protected String doInBackground(StatusUpdate... params) {
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
                    showNotification(getString(R.string.notification_update_status_success), getString(R.string.notification_update_status_success));
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.notification_update_status_fail));
                    sb.append(result);
                    showNotification(sb.toString(), sb.toString());
                }
            }
        };
        task.execute(status);
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
                    showNotification(getString(R.string.notification_favorite_status_success), getString(R.string.notification_favorite_status_success));
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
                    showNotification(getString(R.string.notification_unfavorite_status_success), getString(R.string.notification_unfavorite_status_success));
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.notification_unfavorite_status_fail));
                    sb.append(result);
                    showNotification(sb.toString(), sb.toString());
                }
            }
        };
        task.execute(new Long(id));

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
                    showNotification(getString(R.string.notification_retweet_status_success), getString(R.string.notification_retweet_status_success));
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
