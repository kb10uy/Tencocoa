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

import org.kb10uy.tencocoa.model.TencocoaUserStreamLister;
import org.kb10uy.tencocoa.model.TencocoaUserStreamObject;
import org.kb10uy.tencocoa.model.TwitterAccountInformation;

import java.util.ArrayList;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamAdapter;
import twitter4j.UserStreamListener;


public class TencocoaStreamingService extends Service {

    private static final int TENCOCOA_STREAMING_NOTIFICATION_ID = 0xC0C0A;

    private NotificationManager mNotificationManager;
    private TencocoaStreamingServiceBinder mBinder = new TencocoaStreamingServiceBinder();
    private String mConsumerKey, mConsumerSecret;

    private TencocoaUserStreamObject mUserStream;
    private TencocoaUserStreamPassiveAdapterManager mPassiveAdapterManager = new TencocoaUserStreamPassiveAdapterManager();
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
        mUserStream = new TencocoaUserStreamObject(mConsumerKey, mConsumerSecret);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUserStream.isRunning()) stopCurrentUserStream();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //action methods

    public void setTargetUser(TwitterAccountInformation info) {
        mUserStream.setNewAccount(info);
    }

    public TwitterAccountInformation getTargetUserInformation() {
        return mUserStream.getAccountInformation();
    }

    public Twitter getTargetUserTwitterInstance() {
        return mUserStream.getTwitterInstance();
    }

    public void startCurrentUserStream(TencocoaUserStreamLister listener) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listener.addAdapter(mPassiveAdapterManager);
                mUserStream.start(listener);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mPassiveAdapterManager.onUserStreamReset(mUserStream.getAccountInformation());
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
                mUserStream.stop();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                showNotification(R.string.notification_streaming_userstream_finished_ticker, R.string.notification_streaming_userstream_finished_text);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //mUserStream.shutdown();
        //mUserStream.removeListener(mUserStreamListener);
    }

    public boolean isUserStreamRunning() {
        return mUserStream.isRunning();
    }

    public void addUserStreamPassiveAdapter(TencocoaUserStreamPassiveAdapter adapter) {
        mPassiveAdapterManager.addAdapter(adapter);
    }
    public void removeUserStreamPassiveAdapter(TencocoaUserStreamPassiveAdapter adapter) {
        mPassiveAdapterManager.removeAdapter(adapter);
    }


    public class TencocoaStreamingServiceBinder extends Binder {
        public TencocoaStreamingService getService() {
            return TencocoaStreamingService.this;
        }
    }

    public static class TencocoaUserStreamPassiveAdapter extends UserStreamAdapter {
        public void onUserStreamReset(TwitterAccountInformation info) {

        }
    }

    public static class TencocoaUserStreamPassiveAdapterManager extends UserStreamAdapter {
        protected List<TencocoaUserStreamPassiveAdapter> mAdapters;

        public void onUserStreamReset(TwitterAccountInformation info) {
            for (TencocoaUserStreamPassiveAdapter i : mAdapters) i.onUserStreamReset(info);
        }

        public TencocoaUserStreamPassiveAdapterManager() {
            mAdapters = new ArrayList<>();
        }

        public void addAdapter(TencocoaUserStreamPassiveAdapter adapter) {
            mAdapters.add(adapter);
        }

        public void removeAdapter(TencocoaUserStreamPassiveAdapter adapter) {
            mAdapters.remove(adapter);
        }

        public void clearAdapter() {
            mAdapters.clear();
        }

        @Override
        public void onDeletionNotice(long directMessageId, long userId) {
            for (UserStreamAdapter i : mAdapters) i.onDeletionNotice(directMessageId, userId);
        }

        @Override
        public void onFriendList(long[] friendIds) {
            for (UserStreamAdapter i : mAdapters) i.onFriendList(friendIds);
        }

        @Override
        public void onFavorite(User source, User target, Status favoritedStatus) {
            for (UserStreamAdapter i : mAdapters) i.onFavorite(source, target, favoritedStatus);
        }

        @Override
        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
            for (UserStreamAdapter i : mAdapters) i.onUnfavorite(source, target, unfavoritedStatus);
        }

        @Override
        public void onFollow(User source, User followedUser) {
            for (UserStreamAdapter i : mAdapters) i.onFollow(source, followedUser);
        }

        @Override
        public void onUnfollow(User source, User unfollowedUser) {
            for (UserStreamAdapter i : mAdapters) i.onUnfollow(source, unfollowedUser);
        }

        @Override
        public void onDirectMessage(DirectMessage directMessage) {
            for (UserStreamAdapter i : mAdapters) i.onDirectMessage(directMessage);
        }

        @Override
        public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
            for (UserStreamAdapter i : mAdapters)
                onUserListMemberAddition(addedMember, listOwner, list);
        }

        @Override
        public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
            for (UserStreamAdapter i : mAdapters)
                i.onUserListMemberDeletion(deletedMember, listOwner, list);
        }

        @Override
        public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
            for (UserStreamAdapter i : mAdapters) i.onUserListSubscription(subscriber, listOwner, list);
        }

        @Override
        public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
            for (UserStreamAdapter i : mAdapters)
                i.onUserListUnsubscription(subscriber, listOwner, list);
        }

        @Override
        public void onUserListCreation(User listOwner, UserList list) {
            for (UserStreamAdapter i : mAdapters) i.onUserListCreation(listOwner, list);
        }

        @Override
        public void onUserListUpdate(User listOwner, UserList list) {
            for (UserStreamAdapter i : mAdapters) i.onUserListUpdate(listOwner, list);
        }

        @Override
        public void onUserListDeletion(User listOwner, UserList list) {
            for (UserStreamAdapter i : mAdapters) i.onUserListDeletion(listOwner, list);
        }

        @Override
        public void onUserProfileUpdate(User updatedUser) {
            for (UserStreamAdapter i : mAdapters) i.onUserProfileUpdate(updatedUser);
        }

        @Override
        public void onUserSuspension(long suspendedUser) {
            for (UserStreamAdapter i : mAdapters) i.onUserSuspension(suspendedUser);
        }

        @Override
        public void onUserDeletion(long deletedUser) {
            for (UserStreamAdapter i : mAdapters) i.onUserDeletion(deletedUser);
        }

        @Override
        public void onBlock(User source, User blockedUser) {
            for (UserStreamAdapter i : mAdapters) i.onBlock(source, blockedUser);
        }

        @Override
        public void onUnblock(User source, User unblockedUser) {
            for (UserStreamAdapter i : mAdapters) i.onUnblock(source, unblockedUser);
        }

        @Override
        public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
            for (UserStreamAdapter i : mAdapters) i.onRetweetedRetweet(source, target, retweetedStatus);
        }

        @Override
        public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {
            for (UserStreamAdapter i : mAdapters) i.onFavoritedRetweet(source, target, favoritedRetweet);
        }

        @Override
        public void onQuotedTweet(User source, User target, Status quotingTweet) {
            for (UserStreamAdapter i : mAdapters) i.onRetweetedRetweet(source, target, quotingTweet);
        }

        @Override
        public void onStatus(Status status) {
            for (UserStreamAdapter i : mAdapters) i.onStatus(status);
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            for (UserStreamAdapter i : mAdapters) i.onDeletionNotice(statusDeletionNotice);
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            for (UserStreamAdapter i : mAdapters) i.onTrackLimitationNotice(numberOfLimitedStatuses);
        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {
            for (UserStreamAdapter i : mAdapters) i.onScrubGeo(userId, upToStatusId);
        }

        @Override
        public void onStallWarning(StallWarning warning) {
            for (UserStreamAdapter i : mAdapters) i.onStallWarning(warning);
        }

        @Override
        public void onException(Exception ex) {
            for (UserStreamAdapter i : mAdapters) i.onException(ex);
        }
    }

}
