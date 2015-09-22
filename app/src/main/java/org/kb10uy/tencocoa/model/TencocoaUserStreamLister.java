package org.kb10uy.tencocoa.model;

import java.util.ArrayList;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamAdapter;
import twitter4j.UserStreamListener;

public class TencocoaUserStreamLister implements UserStreamListener {
    List<UserStreamAdapter> mAdapters;

    public TencocoaUserStreamLister() {
        mAdapters = new ArrayList<>();
    }

    public void addAdapter(UserStreamAdapter adapter) {
        mAdapters.add(adapter);
    }

    public void removeAdapter(UserStreamAdapter adapter) {
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
