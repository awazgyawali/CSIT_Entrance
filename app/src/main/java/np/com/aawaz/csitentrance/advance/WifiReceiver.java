package np.com.aawaz.csitentrance.advance;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;


public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        String tag = "periodic";

        GcmNetworkManager mScheduler = Singleton.getInstance().getGcmScheduler();

        if (netInfo != null && (netInfo.getType() == ConnectivityManager.TYPE_MOBILE || netInfo.getType() == ConnectivityManager.TYPE_WIFI)) {

            long periodSecs = 1800L;

            PeriodicTask periodic = new PeriodicTask.Builder()
                    .setService(BackgroundTaskHandler.class)
                    .setPeriod(periodSecs)
                    .setTag(tag)
                    .setPersisted(true)
                    .setUpdateCurrent(true)
                    .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED)
                    .build();
            mScheduler.schedule(periodic);
        } else {
            mScheduler.cancelTask(tag, BackgroundTaskHandler.class);
        }
    }
}

