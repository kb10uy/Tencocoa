package org.kb10uy.tencocoa;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.kb10uy.tencocoa.model.TencocoaHelper;
import org.kb10uy.tencocoa.model.TencocoaUserStreamLister;
import org.kb10uy.tencocoa.model.TwitterAccountInformation;
import org.kb10uy.tencocoa.model.TwitterHelper;

import java.util.concurrent.CountDownLatch;

import twitter4j.Twitter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;


public class TencocoaStreamingService extends Service {

    private static final int TENCOCOA_STREAMING_NOTIFICATION_ID = 0xC0C0A;

    private NotificationManager mNotificationManager;

    private TwitterAccountInformation currentUser;
    private Twitter mTwitter;
    private TwitterStream mUserStream;
    private boolean isUserStreamRunning = false;
    private TencocoaStreamingServiceBinder mBinder = new TencocoaStreamingServiceBinder();
    private String mConsumerKey, mConsumerSecret;
    private TencocoaUserStreamLister mUserStreamListener;
    private CountDownLatch mStreamStopperLatch;
    //helper methods


    //general

    private void showNotification(int tickerStringId, int descriptionStringId) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.tencocoa_notify)
                .setTicker(getString(tickerStringId))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(descriptionStringId));
        mNotificationManager.cancelAll();
        mNotificationManager.notify(TENCOCOA_STREAMING_NOTIFICATION_ID, builder.build());
    }


    //service methods
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        mConsumerKey = pref.getString(getString(R.string.preference_twitter_consumer_key), "");
        mConsumerSecret = pref.getString(getString(R.string.preference_twitter_consumer_secret), "");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mStreamStopperLatch = new CountDownLatch(1);
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

    public void setTargetUser(TwitterAccountInformation info) {
        currentUser = info;
        mTwitter = TwitterHelper.getTwitterInstance(mConsumerKey, mConsumerSecret, currentUser.getAccessToken());
    }

    public TwitterAccountInformation getTargetUserInformation() {
        return currentUser;
    }

    public Twitter getTargetUserTwitterInstance() {
        return mTwitter;
    }

    public void startCurrentUserStream(TencocoaUserStreamLister listener) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i("Tencocoa", "--------------------start streaming!!");
                if (isUserStreamRunning) {
                    mUserStream.clearListeners();
                    TencocoaHelper.Run(mUserStream::shutdown);
                    Log.i("Tencocoa", "--------------------removed listener");
                }
                mUserStreamListener = listener;
                mUserStream = TwitterHelper.getTwitterStreamInstance(mConsumerKey, mConsumerSecret, currentUser.getAccessToken());
                mUserStream.addListener(mUserStreamListener);
                mUserStream.user();
                isUserStreamRunning = true;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mStreamStopperLatch = new CountDownLatch(1);
                showNotification(R.string.notification_streaming_userstream_started_ticker, R.string.notification_streaming_userstream_started_text);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void stopCurrentUserStream() {
        if (mUserStream == null) return;
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TencocoaHelper.Run(mUserStream::shutdown);
                mUserStream.removeListener(mUserStreamListener);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                showNotification(R.string.notification_streaming_userstream_finished_ticker, R.string.notification_streaming_userstream_finished_text);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean isUserStreamRunning() {
        return isUserStreamRunning;
    }

    public class TencocoaStreamingServiceBinder extends Binder {
        public TencocoaStreamingService getService() {
            return TencocoaStreamingService.this;
        }
    }

}
