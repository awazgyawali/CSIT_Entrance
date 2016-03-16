package np.com.aawaz.csitentrance.misc;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;

public class BackgroundTaskHandler extends GcmTaskService {
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

    public static void uploadScore() throws Exception {
        final SharedPreferences pref = MyApplication.getAppContext().getSharedPreferences("pre", MODE_PRIVATE);
        if (getTotal() == pref.getInt("preScore", 900) && pref.getBoolean("updated", false))
            return;
        String url = MyApplication.getAppContext().getString(R.string.postScoreurl);
        Uri.Builder uri = new Uri.Builder();
        String values = uri.authority("")
                .appendQueryParameter("key", MyApplication.getAppContext().getSharedPreferences("info", Context.MODE_PRIVATE).getString("E-mail", "trash"))
                .appendQueryParameter("name", MyApplication.getAppContext().getSharedPreferences("info", Context.MODE_PRIVATE).getString("Name", "trash"))
                .appendQueryParameter("phone", MyApplication.getAppContext().getSharedPreferences("info", Context.MODE_PRIVATE).getString("PhoneNo", "trash"))
                .appendQueryParameter("totalScore", getTotal() + "")
                .build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + values, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pref.edit().putInt("preScore", getTotal()).putBoolean("updated", true).apply();
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
        } catch (Exception ignored) {
        }

        return GcmNetworkManager.RESULT_SUCCESS;
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
}
