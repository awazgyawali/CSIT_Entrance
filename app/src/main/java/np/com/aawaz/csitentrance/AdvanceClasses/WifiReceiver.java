package np.com.aawaz.csitentrance.AdvanceClasses;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;

public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("debug", "Connectivity chnaged.");
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            JobInfo builder = new JobInfo.Builder(200, new ComponentName(context, BackgroundTaskHandler.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPersisted(true)
                    .build();
            JobScheduler mJobScheduler = JobScheduler.getInstance(context);
            mJobScheduler.schedule(builder);
        }
    }
}

