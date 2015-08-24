package np.com.aawaz.csitentrance.advance;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.CSITQuery;
import np.com.aawaz.csitentrance.activities.EntranceNews;

public class BackgroundTaskHandler extends GcmTaskService {
    ArrayList<String> messages = new ArrayList<>();
    RequestQueue requestQueue;
    Context context;
    ArrayList<String> topic = new ArrayList<>(),
            subTopic = new ArrayList<>(),
            imageURL = new ArrayList<>(),
            content = new ArrayList<>(),
            author = new ArrayList<>(),
            link = new ArrayList<>(),
            linkTitle = new ArrayList<>();


    public BackgroundTaskHandler() {
        context = this;

        requestQueue = Singleton.getInstance().getRequestQueue();
    }

    public static void notification(String newsTitle, String content, String ticker, int notifyNumber, Intent intent1, Context context) {
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context);
        notificationCompat.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(newsTitle)
                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.new_arrived))
                .setContentText(content);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompat.setContentIntent(pendingIntent);
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(notifyNumber, notificationCompat.build());
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, context.getClass().getName());
        wl.acquire();
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
        }
        wl.release();
    }

    public static void uploadScore() throws Exception {
        if (getTotal() != MyApplication.getAppContext().getSharedPreferences("pre", MODE_PRIVATE).getInt("preScore", 0))
            return;
        String url = MyApplication.getAppContext().getString(R.string.postScoreurl);
        Uri.Builder uri = new Uri.Builder();
        String values = uri.authority("")
                .appendQueryParameter("key", MyApplication.getAppContext().getSharedPreferences("info", Context.MODE_PRIVATE).getString("uniqueID", "trash"))
                .appendQueryParameter("name", MyApplication.getAppContext().getSharedPreferences("info", Context.MODE_PRIVATE).getString("Name", "trash"))
                .appendQueryParameter("score", getTotal() + "")
                .build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + values, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MyApplication.getAppContext().getSharedPreferences("pre", MODE_PRIVATE).edit().putInt("preScore", getTotal()).apply();
            }
        }, null);
        Singleton.getInstance().getRequestQueue().add(jsonObjectRequest);
    }

    public static int getTotal() {
        SharedPreferences pref = MyApplication.getAppContext().getSharedPreferences("values", Context.MODE_PRIVATE);
        int grand = 0;
        for (int i = 1; i <= 8; i++)
            grand = grand + pref.getInt("score" + i, 0);
        return grand;
    }

    @Override
    public int onRunTask(TaskParams taskParams) {

        try {
            uploadScore();

            updateNews();

            if (!CSITQuery.runningStatus())
                updateQuery();

        } catch (Exception ignored) {
        }
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private void updateNews() throws Exception {
        String url = MyApplication.getAppContext().getString(R.string.url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    topic.clear();
                    subTopic.clear();
                    imageURL.clear();
                    content.clear();
                    author.clear();
                    link.clear();
                    linkTitle.clear();
                    JSONArray jsonArray = response.getJSONArray("news");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);
                        topic.add(jo_inside.getString("topic"));
                        subTopic.add(jo_inside.getString("subTopic"));
                        imageURL.add(jo_inside.getString("imageURL"));
                        content.add(jo_inside.getString("content"));
                        author.add(jo_inside.getString("author"));
                        link.add(jo_inside.getString("link"));
                        linkTitle.add(jo_inside.getString("linkTitle"));
                    }
                    int no = jsonArray.length();
                    MyApplication.getAppContext().getSharedPreferences("values", Context.MODE_PRIVATE).edit().putInt("score8", no).apply();
                    if (no > noOfRows()) {
                        if ((no - noOfRows()) > 1)
                            BackgroundTaskHandler.notification(jsonArray.length() - noOfRows() + " news updated.", "Check them out now!", "News updated", 12345, new Intent(MyApplication.getAppContext(), EntranceNews.class), MyApplication.getAppContext());
                        else
                            BackgroundTaskHandler.notification(topic.get(0), content.get(0), "News updated", 12345, new Intent(MyApplication.getAppContext(), EntranceNews.class), MyApplication.getAppContext());
                    }
                    storeToDb();
                } catch (Exception e) {

                }
            }
        }, null);
        Singleton.getInstance().getRequestQueue().add(jsonObjectRequest);
    }

    private void storeToDb() throws Exception {
        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        database.delete("news", null, null);
        ContentValues values = new ContentValues();
        for (int i = 0; i < topic.size(); i++) {
            values.clear();
            values.put("title", topic.get(i));
            values.put("subTopic", subTopic.get(i));
            values.put("content", content.get(i));
            values.put("imageURL", imageURL.get(i));
            values.put("author", author.get(i));
            values.put("link", link.get(i));
            values.put("linkTitle", linkTitle.get(i));
            database.insert("news", null, values);
        }
        database.close();
    }

    public int noOfRows() throws Exception {
        return getSharedPreferences("values", MODE_PRIVATE).getInt("score8", 0);
    }

    private void updateQuery() throws Exception {
        String url = getString(R.string.queryFetchUrl);
        String fbId = getSharedPreferences("details", Context.MODE_PRIVATE).getString("fbId", "0");
        if (fbId == "0")
            return;
        final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url + fbId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray query = response.getJSONArray("query");
                    messages.clear();
                    for (int i = 0; i < query.length(); i++) {
                        JSONObject jo_inside = query.getJSONObject(i);
                        if (!jo_inside.getString("Question").equals("")) {
                            messages.add(jo_inside.getString("Question"));
                        }
                        if (!jo_inside.getString("Answer").equals("")) {
                            messages.add(jo_inside.getString("Answer"));
                        }
                    }
                    if (messages.size() > getSharedPreferences("data", Context.MODE_PRIVATE).getInt("query", 0)) {
                        notification("Reply for your pre-posted query.", messages.get(messages.size() - 1), "New query received.", 54321, new Intent(context, CSITQuery.class), BackgroundTaskHandler.this);
                        getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("query", messages.size()).apply();
                    }
                } catch (JSONException e) {
                }
            }
        }, null);
        requestQueue.add(jsonObjectRequest1);
    }
}
