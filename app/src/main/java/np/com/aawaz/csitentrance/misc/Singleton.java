package np.com.aawaz.csitentrance.misc;

import android.content.Context;
import android.content.res.AssetManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import np.com.aawaz.csitentrance.objects.Question;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


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

    public Question getQuestionAt(Context context, int code, int position) {
        Question q = null;
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("question" + (code + 1) + ".json", context));
            JSONArray m_jArry = obj.getJSONArray("questions");
            JSONObject jo_inside = m_jArry.getJSONObject(position);
            q = new Question(jo_inside.getString("question"), jo_inside.getString("a"), jo_inside.getString("b"), jo_inside.getString("c"), jo_inside.getString("d"), jo_inside.getString("ans"));
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return q;
    }

    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();

        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }


    public void upvoteComment(final String key, final String s, final String uid, final String uidAt, final boolean b) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLikeACommentProcess(key, s,
                            uid, uidAt, !b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void upvoteDiscussionComment(@Nullable final String key, @NotNull final String s, @NotNull final String uid, @Nullable final String uidAt, final boolean b) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLikeADiscussionCommentProcess(key, s,
                            uid, uidAt, !b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void startLikeADiscussionCommentProcess(String post_id, String comment_id, String upvoter_uid, String upvoted_uid, boolean upvote) throws Exception {
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
                .url("https://us-central1-csit-entrance-7d58.cloudfunctions.net/likeADiscussionComment")
                .post(body)
                .build();
        client.newCall(request).execute();
    }

    private void startLikeACommentProcess(String post_id, String comment_id, String upvoter_uid, String upvoted_uid, boolean upvote) throws Exception {
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
        client.newCall(request).execute();
    }

    public void upvoteAPost(final String s, final String uid, final String uidAt, final boolean b) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLikeACommentProcess(s,
                            uid, uidAt, !b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startLikeACommentProcess(String post_id, String upvoter_uid, String upvoted_uid, boolean upvote) throws Exception {
        String jsonBody = "{" + "" +
                "\"post_id\":\"" + post_id + "\"," +
                "\"upvoter_uid\":\"" + upvoter_uid + "\"," +
                "\"upvote\":" + (upvote ? "true" : "false") + "," +
                "\"upvoted_uid\":\"" + upvoted_uid + "\"" +
                "}";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        Request request = new Request.Builder()
                .url("https://us-central1-csit-entrance-7d58.cloudfunctions.net/likeAPost")
                .post(body)
                .build();
        client.newCall(request).execute();
    }


}

