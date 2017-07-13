package np.com.aawaz.csitentrance.objects;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;

import np.com.aawaz.csitentrance.misc.MyApplication;
import np.com.aawaz.csitentrance.services.NotificationDatabase;

public class Notification {
   public String title,text, post_id, tag;
    public  long time;

    public void addToDatabase(){
        SQLiteDatabase database=new NotificationDatabase(MyApplication.getAppContext()).getWritableDatabase();

        ContentValues values=new ContentValues();

        values.put("title",title);
        values.put("text",text);
        values.put("post_id",post_id);
        values.put("tag",tag);
        values.put("time", System.currentTimeMillis());

        database.insert("notification",null,values);
    }

}
