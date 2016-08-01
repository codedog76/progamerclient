package services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import singletons.NetworkManagerSingleton;

public class SyncService extends Service {

    private static NetworkManagerSingleton sNetworkManagerSingleton;
    private Handler mUserSyncHandler = new Handler();
    private String mClassName = getClass().toString();
    private int REFRESH_INTERVAL = 30 * 1000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final Handler userSyncHandler = new Handler();

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
        sNetworkManagerSingleton = NetworkManagerSingleton.getInstance(getApplicationContext());
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
            sNetworkManagerSingleton.putUserJsonRequest(new NetworkManagerSingleton.BooleanResponseListener() {
                @Override
                public void getResult(Boolean response, String message) {
                    if(response) {
                        Log.e(mClassName, "All data synced");
                    } else {
                        Log.e(mClassName, "Failed to sync data");
                    }
                }
            });
            userSyncHandler.postDelayed(this, REFRESH_INTERVAL);
        }
    };
}
