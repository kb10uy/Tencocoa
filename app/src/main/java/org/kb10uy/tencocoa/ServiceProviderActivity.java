package org.kb10uy.tencocoa;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.kb10uy.tencocoa.views.TencocoaServiceProvider;

import java.util.concurrent.CountDownLatch;

public class ServiceProviderActivity extends AppCompatActivity
implements TencocoaServiceProvider {

    protected TencocoaStreamingService StreamingService;
    protected TencocoaWritePermissionService WritePermissionService;
    protected TencocoaReadPermissionService ReadPermissionService;
    private ServiceConnection mStreamingConnection, mWritePermissionConnection, mReadPermissionConnection;
    private boolean mStreamingBound, mWritePermissionBound, mReadPermissionBound;
    private CountDownLatch mServiceLatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void startServices() {
        Intent tss = new Intent(this, TencocoaStreamingService.class);
        Intent twps = new Intent(this, TencocoaWritePermissionService.class);
        Intent trps = new Intent(this, TencocoaReadPermissionService.class);
        startService(tss);
        startService(twps);
        startService(trps);
        //Log.d(getString(R.string.app_name), "Services started now");
        createServiceConnections();
    }

    protected void stopServices() {
        stopService(new Intent(this, TencocoaStreamingService.class));
        stopService(new Intent(this, TencocoaWritePermissionService.class));
        stopService(new Intent(this, TencocoaReadPermissionService.class));
        //Log.d(getString(R.string.app_name), "Services stopped now");
    }

    protected void bindServices() {
        if (mServiceLatch == null) mServiceLatch = new CountDownLatch(3);
        if (!mStreamingBound)
            mStreamingBound = this.bindService(new Intent(this, TencocoaStreamingService.class), mStreamingConnection, 0);
        if (!mWritePermissionBound)
            mWritePermissionBound = this.bindService(new Intent(this, TencocoaWritePermissionService.class), mWritePermissionConnection, 0);
        if (!mReadPermissionBound)
            mReadPermissionBound = this.bindService(new Intent(this, TencocoaReadPermissionService.class), mReadPermissionConnection, 0);
        //Log.d(getString(R.string.app_name), "Services are now bound");
    }

    protected void unbindServices() {
        if (mStreamingBound) {
            this.unbindService(mStreamingConnection);
            mStreamingBound = false;
            StreamingService = null;
        }
        if (mWritePermissionBound) {
            this.unbindService(mWritePermissionConnection);
            mWritePermissionBound = false;
            WritePermissionService = null;
        }
        if (mReadPermissionBound) {
            this.unbindService(mReadPermissionConnection);
            mReadPermissionBound = false;
            ReadPermissionService = null;
        }
        //Log.d(getString(R.string.app_name), "Services are now unbound");
        mServiceLatch = null;
        createServiceConnections();
    }

    private void createServiceConnections() {
        mStreamingConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                StreamingService = ((TencocoaStreamingService.TencocoaStreamingServiceBinder) service).getService();
                mServiceLatch.countDown();
                onStreamingServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                StreamingService = null;
                mStreamingBound = false;
                unbindService(mStreamingConnection);
            }
        };
        mWritePermissionConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                WritePermissionService = ((TencocoaWritePermissionService.TencocoaWritePermissionServiceBinder) service).getService();
                mServiceLatch.countDown();
                onWritePermissionServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                WritePermissionService = null;
                mWritePermissionBound = false;
                unbindService(mWritePermissionConnection);
            }
        };
        mReadPermissionConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ReadPermissionService = ((TencocoaReadPermissionService.TencocoaReadPermissionServiceBinder) service).getService();
                mServiceLatch.countDown();
                onReadPermissionServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                ReadPermissionService = null;
                mReadPermissionBound = false;
                unbindService(mReadPermissionConnection);
            }
        };
    }

    @Override
    public TencocoaWritePermissionService getWritePermissionService() {
        return WritePermissionService;
    }

    @Override
    public TencocoaReadPermissionService getReadPermissionService() {
        return ReadPermissionService;
    }

    @Override
    public TencocoaStreamingService getStreamingService() {
        return StreamingService;
    }

    @Override
    public boolean isWriteBound() {
        return mWritePermissionBound;
    }

    @Override
    public boolean isReadBound() {
        return mReadPermissionBound;
    }

    @Override
    public boolean isStreamingBound() {
        return mStreamingBound;
    }

    protected void onStreamingServiceConnected() {

    }

    protected void onWritePermissionServiceConnected() {

    }

    protected void onReadPermissionServiceConnected() {

    }

    protected void waitAllServiceConnection() throws InterruptedException {
        mServiceLatch.await();
    }
}
