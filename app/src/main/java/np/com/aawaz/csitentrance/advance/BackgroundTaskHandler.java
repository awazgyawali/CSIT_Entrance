package np.com.aawaz.csitentrance.advance;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
    ArrayList<String> topic = new ArrayList<>(),
            subTopic = new ArrayList<>(),
            imageURL = new ArrayList<>(),
            content = new ArrayList<>(),
            author = new ArrayList<>(),
            messages = new ArrayList<>();
    RequestQueue requestQueue;
    Context context;


    public BackgroundTaskHandler() {
        context = this;

        requestQueue = Singleton.getInstance().getRequestQueue();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {

        uploadScore();

        updateQuery();

        updateNews();

        return GcmNetworkManager.RESULT_SUCCESS;
    }


    private void uploadScore() {
        String url = getString(R.string.postScoreurl);
        Uri.Builder uri = new Uri.Builder();
        String values = uri.authority("")
                .appendQueryParameter("key", getSharedPreferences("info", Context.MODE_PRIVATE).getString("uniqueID", "trash"))
                .appendQueryParameter("name", getSharedPreferences("info", Context.MODE_PRIVATE).getString("Name", "trash"))
                .appendQueryParameter("score", getTotal() + "")
                .build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + values, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void updateNews() {
        String url = getString(R.string.url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    topic.clear();
                    subTopic.clear();
                    imageURL.clear();
                    content.clear();
                    author.clear();
                    JSONArray jsonArray = response.getJSONArray("news");
                    int no = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);
                        topic.add(jo_inside.getString("topic"));
                        subTopic.add(jo_inside.getString("subTopic"));
                        imageURL.add(jo_inside.getString("imageURL"));
                        content.add(jo_inside.getString("content"));
                        author.add(jo_inside.getString("author"));
                        no++;
                    }
                    context.getSharedPreferences("values",Context.MODE_PRIVATE).edit().putInt("score8",no).apply();
                    if (no > noOfRows()) {
                        if (no - noOfRows() > 1)
                            notification(no - noOfRows() + " news updated.", "Check them out now!", "News updated", 12345, new Intent(context, EntranceNews.class));
                        else
                            notification(topic.get(0), content.get(0), "News updated", 12345, new Intent(context, EntranceNews.class));
                    }
                    storeToDb();
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void updateQuery() {
        String url = getString(R.string.queryFetchUrl);
        final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url + getSharedPreferences("details", Context.MODE_PRIVATE).getString("fbId", "0"), new Response.Listener<JSONObject>() {
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
                        notification("Reply for your pre-posted query.", messages.get(messages.size() - 1), "New query received.", 54321, new Intent(context, CSITQuery.class));
                        getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("query", messages.size()).apply();
                    }
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest1);
    }


    private void notification(String newsTitle, String content, String ticker, int notifyNumber, Intent intent1) {
        MediaPlayer.create(this, R.raw.notif_sound).start();
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this);
        notificationCompat.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(newsTitle)
                .setContentText(content);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompat.setContentIntent(pendingIntent);
        NotificationManager notificationManagerCompat = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(notifyNumber, notificationCompat.build());
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, getClass().getName());
        wl.acquire();
        try {
            Thread.sleep(7000);//or however long this is one second
        } catch (InterruptedException e) {
        }
        wl.release();
    }

    private void storeToDb() {
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
            database.insert("news", null, values);
        }
        database.close();
    }

    public int noOfRows() {
        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        Cursor cursor = database.query("news", new String[]{"title"}, null, null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
        }
        cursor.close();
        database.close();
        return i;
    }

    private int getTotal() {
        SharedPreferences pref = getSharedPreferences("values", Context.MODE_PRIVATE);
        int grand = 0;
        for (int i = 1; i < 8; i++)
            grand = grand + pref.getInt("score" + i, 0);
        return grand;
    }
}
