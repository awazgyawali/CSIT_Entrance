package np.com.aawaz.csitentrance.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotificationDatabase extends SQLiteOpenHelper {
    public NotificationDatabase(Context context) {
        super(context, "notific", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE notification(ID INTEGER AUTO INCREMENT, title VARCHAR(255), text VARCHAR(255), post_id VARCHAR(255), tag VARCHAR(50), time DOUBLE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
