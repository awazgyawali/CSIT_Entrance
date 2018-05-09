package np.com.aawaz.csitentrance.services;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import np.com.aawaz.csitentrance.interfaces.ResponseListener;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkRequester {


    private final String url;
    private final ResponseListener listener;

    public NetworkRequester(String url, ResponseListener listener) {
        this.url = url;
        this.listener = listener;
        startRequest();
    }

    public NetworkRequester(String url, HashMap<String, String> map, ResponseListener listener) {
        this.listener = listener;
        this.url = url;

        new AsyncTask<HashMap<String, String>, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(HashMap<String, String>... hashMaps) {
                OkHttpClient client = new OkHttpClient();

                MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);

                Iterator it = hashMaps[0].entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    requestBodyBuilder.addFormDataPart(pair.getKey().toString(), pair.getValue().toString());
                    it.remove();
                }

                RequestBody requestBody = requestBodyBuilder.build();

                Request request = new Request.Builder()
                        .url(NetworkRequester.this.url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("message",response.message());

                    if (response.isSuccessful())
                        return response.body().string();
                    else
                        return "fail";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "fail";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equals("fail")) {
                    if (NetworkRequester.this.listener != null) {
                        NetworkRequester.this.listener.onFailure();
                    }
                } else if (NetworkRequester.this.listener != null) {
                    NetworkRequester.this.listener.onSuccess(s);
                }
            }
        }.execute(map);


    }

    private void startRequest() {
        new AsyncTask<Void, Void, String>() {
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
        }.execute();
    }


}
