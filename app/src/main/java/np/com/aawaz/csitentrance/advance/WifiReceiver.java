package np.com.aawaz.csitentrance.advance;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;


public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {

            long startSecs = 0L;

            long endSecs = startSecs + 3600L;

            int taskID=120;

            String tag = "oneoff  | " + taskID++ + ": [" + startSecs + "," + endSecs + "]";

            OneoffTask oneoff = new OneoffTask.Builder()
                    .setService(BackgroundTaskHandler.class)
                    .setTag(tag)
                    .setExecutionWindow(startSecs, endSecs)
                    .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .build();

            GcmNetworkManager mScheduler=Singleton.getInstance().getGcmScheduler();

            mScheduler.schedule(oneoff);
        }
    }
}

