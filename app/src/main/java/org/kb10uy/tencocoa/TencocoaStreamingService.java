package org.kb10uy.tencocoa;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import org.kb10uy.tencocoa.model.TencocoaUserStreamLister;
import org.kb10uy.tencocoa.model.TwitterAccountInformation;
import org.kb10uy.tencocoa.model.TwitterHelper;

import twitter4j.Twitter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TencocoaStreamingService extends Service {

    private static final String ACTION_SET_USER = "SetUser";
    private static final String ACTION_START_USERSTREAM = "StartUserStream";
    private static final String ACTION_STOP_USERSTREAM = "StopUserStream";
    private static final int TENCOCOA_STREAMING_NOTIFICATION_ID = 0xC0C0A;

    private NotificationManager mNotificationManager;

    private TwitterAccountInformation currentUser;
    private Twitter mTwitter;
    private TwitterStream mUserStream;
    private boolean isUserStreamRunning = false;
    private TencocoaStreamingServiceBinder mBinder = new TencocoaStreamingServiceBinder();
    private String mConsumerKey, mConsumerSecret;
    private TencocoaUserStreamLister mUserStreamListener;
    //helper methods


    //general

    private void showNotification(int tickerStringId, int descriptionStringId) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.tencocoa_notify)
                .setTicker(getString(tickerStringId))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(descriptionStringId));
        mNotificationManager.notify(TENCOCOA_STREAMING_NOTIFICATION_ID, builder.build());
    }

    //handler methods

    private void handleStreamingTargetUser(TwitterAccountInformation info) {
        currentUser = info;
        stopCurrentUserStream();
    }

    private void handleStartUserStream() {
        startCurrentUserStream();
    }

    private void handleStopUserStream() {
        startCurrentUserStream();
    }


    //service methods
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_name), 0);
        mConsumerKey = pref.getString(getString(R.string.preference_twitter_consumer_key), "");
        mConsumerSecret = pref.getString(getString(R.string.preference_twitter_consumer_secret), "");
        mTwitter = TwitterHelper.getTwitterInstance(mConsumerKey, mConsumerSecret);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isUserStreamRunning) stopCurrentUserStream();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //action methods

    public void setStreamingUser(TwitterAccountInformation info) {
        currentUser = info;
        stopCurrentUserStream();
        mUserStream = new TwitterStreamFactory().getInstance();
        mUserStream.setOAuthConsumer(mConsumerKey, mConsumerSecret);
        mUserStream.setOAuthAccessToken(new AccessToken(currentUser.getAccessToken(), currentUser.getAccessTokenSecret()));
    }

    public void startCurrentUserStream() {
        showNotification(R.string.notification_streaming_userstream_started_ticker, R.string.notification_streaming_userstream_started_text);
        mUserStream.user();
    }

    public void stopCurrentUserStream() {
        if (mUserStream != null) {
            AsyncTask<TwitterStream, Void, Void> task = new AsyncTask<TwitterStream, Void, Void>() {
                @Override
                protected Void doInBackground(TwitterStream... params) {
                    params[0].cleanUp();
                    return null;
                }
            };
            task.execute(mUserStream);
            mUserStream = null;
        }
    }

    public void addUserStreamListener(TencocoaUserStreamLister listener) {
        if (mUserStream != null) mUserStream.addListener(listener);
        mUserStreamListener = listener;
    }

    public void removeUserStreamListener(UserStreamListener listener) {
        if (mUserStream != null) mUserStream.removeListener(listener);
        mUserStreamListener = null;
    }

    public TencocoaUserStreamLister getCurrentUserStreamListener() {
        return mUserStreamListener;
    }

    public class TencocoaStreamingServiceBinder extends Binder {
        public TencocoaStreamingService getService() {
            return TencocoaStreamingService.this;
        }
    }

}
