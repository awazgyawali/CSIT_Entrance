package np.com.aawaz.csitentrance.advance;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class BackgroundTaskHandler extends GcmTaskService {
    ArrayList<String> topic = new ArrayList<>(),
            subTopic = new ArrayList<>(),
            imageURL = new ArrayList<>(),
            content = new ArrayList<>(),
            author = new ArrayList<>(),
            messages = new ArrayList<>(),
            link = new ArrayList<>(),
            linkTitle = new ArrayList<>();
    RequestQueue requestQueue;
    Context context;


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

    @Override
    public int onRunTask(TaskParams taskParams) {

        try {
            if (getTotal() != getSharedPreferences("pre", MODE_PRIVATE).getInt("preScore", 0))
                uploadScore();

            if (!CSITQuery.runningStatus())
                updateQuery();

            uploadReport();

        } catch (Exception ignored) {
        }
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private void uploadReport() {
        final SQLiteDatabase database = Singleton.getInstance().getDatabase();
        final Cursor cursorReport = database.query("report", new String[]{"text"}, null, null, null, null, null);
        String reportText = "";
        int i = 0;
        while (cursorReport.moveToNext()) {
            reportText = reportText + cursorReport.getString(cursorReport.getColumnIndex("text")) + "\n\n";
            i++;
        }
        String url = MyApplication.getAppContext().getString(R.string.uploadReport);
        Uri.Builder uri = new Uri.Builder();
        String values = uri.authority("")
                .appendQueryParameter("text", reportText)
                .build().toString();
        if (i != 0) {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + values, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    database.delete("report", null, null);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    cursorReport.close();
                    database.close();
                }
            });
            Singleton.getInstance().getRequestQueue().add(request);
        }
    }

    private void uploadScore() throws Exception {
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
                getSharedPreferences("pre", MODE_PRIVATE).edit().putInt("preScore", getTotal());
            }
        }, null);
        requestQueue.add(jsonObjectRequest);
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

    private int getTotal() {
        SharedPreferences pref = getSharedPreferences("values", Context.MODE_PRIVATE);
        int grand = 0;
        for (int i = 1; i < 8; i++)
            grand = grand + pref.getInt("score" + i, 0);
        return grand;
    }
}
