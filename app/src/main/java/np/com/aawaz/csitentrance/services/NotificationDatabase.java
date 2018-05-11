package np.com.aawaz.csitentrance.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotificationDatabase extends SQLiteOpenHelper {
    public NotificationDatabase(Context context) {
        super(context, "notific", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE notification(ID INTEGER AUTO INCREMENT," +
                " title VARCHAR(255)," +
                " text VARCHAR(255)," +
                " post_id VARCHAR(255)," +
                " tag VARCHAR(50)," +
                " time DOUBLE);");

        ContentValues values = new ContentValues();
        values.put("text", "Welcome to CSIT Entrance");
        values.put("title", "You can start learning today by playing a quiz or ask any questions to Entoo or on the forum.");
        values.put("tag", "");
        values.put("post_id", "nothing");
        values.put("time", System.currentTimeMillis());
        sqLiteDatabase.insert("notification", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
