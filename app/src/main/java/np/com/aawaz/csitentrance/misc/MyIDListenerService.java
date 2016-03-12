package np.com.aawaz.csitentrance.misc;

import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import java.io.IOException;

import np.com.aawaz.csitentrance.R;

public class MyIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                InstanceID instanceID = InstanceID.getInstance(MyIDListenerService.this);
                String token;
                try {
                    token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                } catch (IOException e) {
                    token = "Failed" + e;
                }

                return token;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(MyIDListenerService.this, s, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
