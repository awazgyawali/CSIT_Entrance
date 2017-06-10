package np.com.aawaz.csitentrance.services;


import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;

import java.io.IOException;

import np.com.aawaz.csitentrance.interfaces.ResponseListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkRequester {


    private final String url;
    private final ResponseListener listener;

    public NetworkRequester(String url, ResponseListener listener) {
        this.url = url;
        this.listener = listener;
        startRequest();
    }

    private void startRequest() {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful())
                        return response.body().string();
                    else
                        return "fail";

                } catch (IOException e) {
                    return "fail";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equals("fail")) {
                    if (listener != null) {
                        listener.onFailure();
                    }
                } else if (listener != null) {
                    listener.onSuccess(s);
                }
            }
        });
    }


}
