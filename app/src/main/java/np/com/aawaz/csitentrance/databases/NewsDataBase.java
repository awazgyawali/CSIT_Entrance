package np.com.aawaz.csitentrance.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NewsDataBase extends SQLiteOpenHelper {

    public NewsDataBase(Context context) {
        super(context, "records", null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE news(message TEXT,imageURL TEXT,time VARCHAR(255));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE report;");
        sqLiteDatabase.execSQL("DROP TABLE news;");
        sqLiteDatabase.execSQL("CREATE TABLE news(message TEXT,imageURL TEXT,time VARCHAR(255));");
    }
}
