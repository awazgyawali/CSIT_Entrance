package np.com.aawaz.csitentrance.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.os.AsyncTaskCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.SPHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScoreUploader extends Service {

    String url;
    Receiver receiver;

    public ScoreUploader() {
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        if (receiver == null)
            receiver = new Receiver();
        registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        uploader();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void uploader() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        url = getString(R.string.uploadScore) + "?name=" + user.getDisplayName()
                + ",email=" + user.getEmail()
                + ",score=" + SPHandler.getInstance().getTotalScore()
                + ",image_link=" + user.getPhotoUrl().toString()
                + ",instance_id=" + FirebaseInstanceId.getInstance().getToken();

        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful())
                        SPHandler.getInstance().setScoreChanged(false);
                    stopSelf();
                } catch (IOException e) {
                    ScoreUploader.this.stopSelf();
                }
                return null;
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ||
                        activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    uploader();
            }
        }
    }
}
