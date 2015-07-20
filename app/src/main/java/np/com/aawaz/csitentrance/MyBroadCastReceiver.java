package np.com.aawaz.csitentrance;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyBroadCastReceiver extends BroadcastReceiver {
    ArrayList<String> topic = new ArrayList<>(),
            subTopic = new ArrayList<>(),
            imageURL = new ArrayList<>(),
            content = new ArrayList<>(),
            author = new ArrayList<>(),
            messages = new ArrayList<>();
    RequestQueue requestQueue;
    Context context;


    @Override
    public void onReceive(final Context context, Intent intent) {
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        this.context = context;
        updateNews();

        updateQuery();

        uploadScore();

    }

    private void uploadScore() {
        String url = context.getString(R.string.postScoreurl);
        Uri.Builder uri = new Uri.Builder();
        String values = uri.authority("")
                .appendQueryParameter("key", context.getSharedPreferences("info", Context.MODE_PRIVATE).getString("uniqueID", "trash"))
                .appendQueryParameter("name", context.getSharedPreferences("info", Context.MODE_PRIVATE).getString("Name", "trash"))
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
        String url = context.getString(R.string.url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
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
                    if (no > noOfRows(context)) {
                        notification(context, topic.get(0), content.get(0), topic.get(0), 12345, new Intent(context, EntranceNews.class));
                    }
                    storeToDb(context);
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
        String url = context.getString(R.string.queryFetchUrl);
        final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url + context.getSharedPreferences("details", Context.MODE_PRIVATE).getString("fbId", "0"), new Response.Listener<JSONObject>() {
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
                    if (messages.size() > context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("query", 0)) {
                        notification(context, "Reply for your pre-posted query.", messages.get(messages.size() - 1), "New query received.", 54321, new Intent(context, CSITQuery.class));
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("query", messages.size()).apply();
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

    private void notification(Context context, String newsTitle, String content, String ticker, int notifyNumber, Intent intent1) {
        Uri alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context);
        notificationCompat.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(ticker)
                .setSound(alertSound)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(newsTitle)
                .setContentText(content);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompat.setContentIntent(pendingIntent);
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(notifyNumber, notificationCompat.build());

    }

    private void storeToDb(Context context) {
        NewsDataBase dataBase = new NewsDataBase(context);
        SQLiteDatabase database = dataBase.getWritableDatabase();
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

    public int noOfRows(Context context) {
        NewsDataBase dataBase = new NewsDataBase(context);
        SQLiteDatabase database = dataBase.getWritableDatabase();
        Cursor cursor = database.query("news", new String[]{"title"}, null, null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
        }
        Log.d("Debug", i + "");
        return i;

    }

    private int getTotal() {
        SharedPreferences pref = context.getSharedPreferences("values", Context.MODE_PRIVATE);
        int grand = 0;
        for (int i = 1; i < 8; i++)
            grand = grand + pref.getInt("score" + i, 0);
        return grand;
    }


}
