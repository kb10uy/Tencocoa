package org.kb10uy.tencocoa.model;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import org.kb10uy.tencocoa.R;

import java.io.File;

public class TencocoaBroadcastReceiver extends BroadcastReceiver {
    private static final int TENCOCOA_BROADCAST_RECEIVER_NOTIFICATION_ID = 0x1771104;


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                long did = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
                DownloadManager dlm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(did);
                Cursor cursor = dlm.query(q);
                cursor.moveToFirst();
                String file = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                showNotification(context, context.getString(R.string.notification_download_finished_ticker), new File(file).getName());
                break;
        }
    }

    private void showNotification(Context ctx, String title, String description) {
        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(ctx)
                .setSmallIcon(R.drawable.tencocoa_notify)
                .setContentTitle(title)
                .setContentText(description);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(TENCOCOA_BROADCAST_RECEIVER_NOTIFICATION_ID, builder.build());
    }
}
