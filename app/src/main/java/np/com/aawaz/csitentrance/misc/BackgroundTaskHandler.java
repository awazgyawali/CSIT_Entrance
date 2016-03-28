package np.com.aawaz.csitentrance.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.json.JSONObject;

import np.com.aawaz.csitentrance.R;

public class BackgroundTaskHandler extends GcmTaskService {
    RequestQueue requestQueue;
    Context context;

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
}
