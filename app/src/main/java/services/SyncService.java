package services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class SyncService extends Service {

    private NetworkManagerSingleton mNetworkManagerSingleton;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private Handler mUserSyncHandler = new Handler();
    private String mClassName = getClass().toString();
    private int REFRESH_INTERVAL = 30 * 1000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNetworkManagerSingleton = NetworkManagerSingleton.getInstance(getApplicationContext());
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(getApplicationContext());
        startUserSync();
        return Service.START_NOT_STICKY;
    }

    private void startUserSync() {
        mUserSyncHandler.removeCallbacks(mSyncUserRunnable);
        mUserSyncHandler.postDelayed(mSyncUserRunnable, 1000);
    }

    private Runnable mSyncUserRunnable = new Runnable() {
        @Override
        public void run() {
            mNetworkManagerSingleton.putUserJsonRequest(mDatabaseHandlerSingleton.getLoggedUser(), new NetworkManagerSingleton.BooleanResponseListener() {
                @Override
                public void getResult(Boolean response, String message) {
                    if (response) {
                        Log.e(mClassName, "All data synced");
                    } else {
                        Log.e(mClassName, "Failed to sync data");
                    }
                }
            });
            mUserSyncHandler.postDelayed(this, REFRESH_INTERVAL);
        }
    };
}
