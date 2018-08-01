package np.com.aawaz.csitentrance.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Singleton {

    private static Singleton sInstance = null;

    private Singleton() {

    }

    public static Singleton getInstance() {
        if (sInstance == null) {
            sInstance = new Singleton();
        }
        return sInstance;
    }

    public void upvoteComment(final String key, final String s, final String uid, final String uidAt, final boolean b) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    startLikeProcess(key, s,
                            uid, uidAt, !b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private String startLikeProcess(String post_id, String comment_id, String upvoter_uid, String upvoted_uid, boolean upvote) throws Exception {
        String jsonBody = "{" + "" +
                "\"post_id\":\"" + post_id + "\"," +
                "\"comment_id\":\"" + comment_id + "\"," +
                "\"upvote\":" + (upvote ? "true" : "false") + "," +
                "\"upvoter_uid\":\"" + upvoter_uid + "\"," +
                "\"upvoted_uid\":\"" + upvoted_uid + "\"" +
                "}";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        Request request = new Request.Builder()
                .url("https://us-central1-csit-entrance-7d58.cloudfunctions.net/likeAComment")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}

