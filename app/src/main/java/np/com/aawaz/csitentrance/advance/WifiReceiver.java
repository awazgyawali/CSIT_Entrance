package np.com.aawaz.csitentrance.advance;


import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.EntranceNews;


public class WifiReceiver extends BroadcastReceiver {
    ArrayList<String> topic = new ArrayList<>(),
            subTopic = new ArrayList<>(),
            imageURL = new ArrayList<>(),
            content = new ArrayList<>(),
            author = new ArrayList<>(),
            link = new ArrayList<>(),
            linkTitle = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        String tag = "periodic";

        GcmNetworkManager mScheduler = Singleton.getInstance().getGcmScheduler();

        if (netInfo != null && (netInfo.getType() == ConnectivityManager.TYPE_MOBILE || netInfo.getType() == ConnectivityManager.TYPE_WIFI)) {

            long periodSecs = 180L;

            PeriodicTask periodic = new PeriodicTask.Builder()
                    .setService(BackgroundTaskHandler.class)
                    .setPeriod(periodSecs)
                    .setTag(tag)
                    .setFlex(periodSecs)
                    .setPersisted(true)
                    .setUpdateCurrent(true)
                    .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED)
                    .build();
            mScheduler.schedule(periodic);

            updateNews();

        } else {
            mScheduler.cancelTask(tag, BackgroundTaskHandler.class);
        }
    }

    private void updateNews() {
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
                    int no = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);
                        topic.add(jo_inside.getString("topic"));
                        subTopic.add(jo_inside.getString("subTopic"));
                        imageURL.add(jo_inside.getString("imageURL"));
                        content.add(jo_inside.getString("content"));
                        author.add(jo_inside.getString("author"));
                        link.add(jo_inside.getString("link"));
                        linkTitle.add(jo_inside.getString("linkTitle"));
                        no++;
                    }
                    MyApplication.getAppContext().getSharedPreferences("values", Context.MODE_PRIVATE).edit().putInt("score8", no).apply();
                    if (no > noOfRows()) {
                        if ((no - noOfRows()) >= 1)
                            BackgroundTaskHandler.notification(no - noOfRows() + " news updated.", "Check them out now!", "News updated", 12345, new Intent(MyApplication.getAppContext(), EntranceNews.class), MyApplication.getAppContext());
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
        SQLiteDatabase databaseForNews = Singleton.getInstance().getDatabase();
        Cursor cursor = databaseForNews.query("news", new String[]{"title"}, null, null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
        }
        cursor.close();
        databaseForNews.close();
        return i;
    }


}

