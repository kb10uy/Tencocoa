package org.kb10uy.tencocoa.model;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

public class TencocoaUserStreamLister implements UserStreamListener {

    HomeTimeLineLister mHomeTimeLineLister;
    TwitterAccountInformationReceiver mSelfInfoReceiver;
    long targetId;

    public TencocoaUserStreamLister(HomeTimeLineLister htl, TwitterAccountInformationReceiver selfInfoReceiver) {
        mHomeTimeLineLister = htl;
        mSelfInfoReceiver = selfInfoReceiver;
    }

    @Override
    public void onDeletionNotice(long directMessageId, long userId) {

    }

    @Override
    public void onFriendList(long[] friendIds) {

    }

    @Override
    public void onFavorite(User source, User target, Status favoritedStatus) {
        mHomeTimeLineLister.onFavorite(favoritedStatus);
    }

    @Override
    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
        mHomeTimeLineLister.onUnfavorite(unfavoritedStatus);
    }

    @Override
    public void onFollow(User source, User followedUser) {

    }

    @Override
    public void onUnfollow(User source, User unfollowedUser) {

    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {

    }

    @Override
    public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

    }

    @Override
    public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

    }

    @Override
    public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

    }

    @Override
    public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

    }

    @Override
    public void onUserListCreation(User listOwner, UserList list) {

    }

    @Override
    public void onUserListUpdate(User listOwner, UserList list) {

    }

    @Override
    public void onUserListDeletion(User listOwner, UserList list) {

    }

    @Override
    public void onUserProfileUpdate(User updatedUser) {
        if (updatedUser.getId() == targetId) mSelfInfoReceiver.onTwitterAccountInformationReceived(updatedUser);
    }

    @Override
    public void onUserSuspension(long suspendedUser) {

    }

    @Override
    public void onUserDeletion(long deletedUser) {

    }

    @Override
    public void onBlock(User source, User blockedUser) {

    }

    @Override
    public void onUnblock(User source, User unblockedUser) {

    }

    @Override
    public void onStatus(Status status) {
        if (targetId == 0) targetId = mSelfInfoReceiver.getTargetAccountId();
        if (status.getUser().getId() == targetId)
            mSelfInfoReceiver.onTwitterAccountInformationReceived(status.getUser());
        mHomeTimeLineLister.onHomeTimeLineStatus(status);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {

    }

    @Override
    public void onStallWarning(StallWarning warning) {

    }

    @Override
    public void onException(Exception ex) {

    }

    public void changeHomeTimeLineLister(HomeTimeLineLister lister) {
        mHomeTimeLineLister = lister;
    }
}
