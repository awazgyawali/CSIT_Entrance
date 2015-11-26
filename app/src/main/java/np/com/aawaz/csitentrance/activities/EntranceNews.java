package np.com.aawaz.csitentrance.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.NewsAdapter;
import np.com.aawaz.csitentrance.advance.Singleton;


public class EntranceNews extends AppCompatActivity  {

    ArrayList<String> time = new ArrayList<>(),
            imageUrl = new ArrayList<>(),
            messages = new ArrayList<>();

    RecyclerView recy;
    RequestQueue requestQueue;
    NewsAdapter newsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance_news);

        //Setting the data
        setDataToArrayListFromDb();
    }

    public void fillData() {
        recy = (RecyclerView) findViewById(R.id.newsFeedRecy);
        newsAdapter = new NewsAdapter(this, messages, time, imageUrl);
        recy.setAdapter(newsAdapter);
    }

    public void setDataToArrayListFromDb() {
        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        Cursor cursor = database.query("news", new String[]{"message","time","imageURL"}, null, null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
            time.add(cursor.getString(cursor.getColumnIndex("time")));
            messages.add(cursor.getString(cursor.getColumnIndex("message")));
            imageUrl.add(cursor.getString(cursor.getColumnIndex("imageURL")));
        }
        if (i == 0) {
            fetchNewsForFirstTime();
        } else {
            fillData();
        }
        cursor.close();
        database.close();
    }

    public void fetchNewsForFirstTime() {
        //Reading from json file and insillizing inside arrayList
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Please wait")
                .progress(true, 0)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        requestQueue.cancelAll("news");
                        finish();
                    }
                })
                .show();

            requestQueue = Singleton.getInstance().getRequestQueue();
            getAccessToken(dialog);
        }

    private void storeToDb() {
        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        database.delete("news", null, null);
        ContentValues values = new ContentValues();
        for (int i = 0; i < messages.size(); i++) {
            values.clear();
            values.put("time", time.get(i));
            values.put("message", messages.get(i));
            values.put("imageURL", imageUrl.get(i));
            database.insert("news", null, values);
        }
        database.close();
    }


    String accessToken;
    StringRequest request;

    private void getAccessToken(final MaterialDialog dialog){
        String accessLink = "https://graph.facebook.com/oauth/access_token?client_id="
                + getString(R.string.facebook_app_id) + "&client_secret=" + getString(R.string.facebook_app_secret) + "&grant_type=client_credentials";

        request = new StringRequest(accessLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                accessToken = response;
                fetchFromInternet(dialog);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(EntranceNews.this,"Unable to connect.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        requestQueue.add(request);
    }

    private void fetchFromInternet(final MaterialDialog dialog) {
        String link = "https://graph.facebook.com/CSITentrance/feed?fields=id,full_picture,story,message,created_time,from&limit=100&";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, link + accessToken,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        parseTheResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(EntranceNews.this,"Unable to connect.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
        });
        requestQueue.add(jsonRequest);
    }

    private void parseTheResponse(JSONObject response) {
        try {
            JSONArray array = response.getJSONArray("data");
            for (int i=0 ; i<array.length() ;i++){
                JSONObject object = array.getJSONObject(i);
                if(object.getJSONObject("from").getString("id").equals("933435523387727")){
                    try {
                        messages.add(object.getString("message"));
                        try{
                            imageUrl.add(object.getString("full_picture"));
                        } catch (Exception e){
                            imageUrl.add("null");
                        }
                        time.add(convertToSimpleDate(object.getString("created_time")).toString());
                    }catch (Exception ignored){}
                }
            }
            fillData();
            storeToDb();
        } catch (Exception ignored){}
    }

    private CharSequence convertToSimpleDate(String created_time) {

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ");
        try {
            Date date = simpleDateFormat.parse(created_time);
            return DateUtils.getRelativeDateTimeString(this,date.getTime(),DateUtils.SECOND_IN_MILLIS,DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_NO_MONTH_DAY);
        } catch (ParseException ignored) {}
        return "Unknown Time";
    }
}