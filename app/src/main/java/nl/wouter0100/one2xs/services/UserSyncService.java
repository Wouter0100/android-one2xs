package nl.wouter0100.one2xs.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import nl.wouter0100.one2xs.adapters.UserSyncAdapter;

public class UserSyncService extends Service {

    private static final Object mSyncAdapterLock = new Object();
    private static UserSyncAdapter mSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (mSyncAdapterLock) {
            if (mSyncAdapter == null)
                mSyncAdapter = new UserSyncAdapter(getApplicationContext(), true);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
