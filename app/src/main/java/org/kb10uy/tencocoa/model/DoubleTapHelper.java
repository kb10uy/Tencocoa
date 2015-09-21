package org.kb10uy.tencocoa.model;


import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class DoubleTapHelper {
    private Handler mHandler;
    private Toast mShowingToast;
    private int doubleTapTime;
    private Runnable mRunnable;
    private boolean isWaitingForSecondTap;

    public DoubleTapHelper(Context ctx, String toastText, int toastDuration, int doubleTapDuration) {
        mHandler = new Handler();
        mShowingToast = Toast.makeText(ctx, toastText, toastDuration);
        doubleTapTime = doubleTapDuration;
        mRunnable = () -> {
            isWaitingForSecondTap = false;
        };
    }

    public boolean onTap() {
        if (!isWaitingForSecondTap) {
            isWaitingForSecondTap = true;
            mHandler.postDelayed(mRunnable, doubleTapTime);
            mShowingToast.show();
            return false;
        } else {
            mShowingToast.cancel();
            return true;
        }
    }
}
