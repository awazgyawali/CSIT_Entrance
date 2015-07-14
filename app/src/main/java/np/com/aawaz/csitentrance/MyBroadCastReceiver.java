package np.com.aawaz.csitentrance;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyBroadCastReceiver extends BroadcastReceiver {
    public MyBroadCastReceiver() {


    }
    ArrayList<String> topic=new ArrayList<>(),
            subTopic=new ArrayList<>(),
            imageURL=new ArrayList<>(),
            content=new ArrayList<>(),
            author=new ArrayList<>();

    @Override
    public void onReceive(final Context context, Intent intent) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Log.d("Debug","Trying");
        String url=context.getString(R.string.url);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("news");
                    int no=0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);
                        topic.add(jo_inside.getString("topic"));
                        subTopic.add(jo_inside.getString("subTopic"));
                        imageURL.add(jo_inside.getString("imageURL"));
                        content.add(jo_inside.getString("content"));
                        author.add(jo_inside.getString("author"));
                        no++;
                    }
                    Log.d("Debug",no+">"+noOfRows(context));
                    if(no>noOfRows(context)) {
                        notification(context, topic.get(0), content.get(0),topic.get(0));
                        storeToDb(context);
                    }

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

    private void notification(Context context,String newsTitle,String content,String ticker){
        NotificationCompat.Builder notificationCompat=new NotificationCompat.Builder(context);
        notificationCompat.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(newsTitle)
                .setContentText(content);
        Intent intent1=new Intent(context,EntranceNews.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompat.setContentIntent(pendingIntent);

        NotificationManager notificationManagerCompat= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(12345, notificationCompat.build());

    }

    private void storeToDb(Context context) {
        NewsDataBase dataBase=new NewsDataBase(context);
        SQLiteDatabase database= dataBase.getWritableDatabase();
        database.delete("news", null, null);
        ContentValues values=new ContentValues();
        for(int i=0;i<topic.size();i++) {
            values.clear();
            values.put("title", topic.get(i));
            values.put("subTopic", subTopic.get(i));
            values.put("content", content.get(i));
            values.put("imageURL", imageURL.get(i));
            values.put("author", author.get(i));
            database.insert("news",null,values);
        }
        database.close();
    }

    public int noOfRows(Context context){
        NewsDataBase dataBase=new NewsDataBase(context);
        SQLiteDatabase database= dataBase.getWritableDatabase();
        Cursor cursor=database.query("news", new String[]{"title"}, null, null, null, null, null);
        int i=0;
        while(cursor.moveToNext()){
            i++;
        }
        Log.d("Debug",i+"");
        return i;

    }
}
