package np.com.aawaz.csitentrance.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NewsDataBase extends SQLiteOpenHelper {

    public NewsDataBase(Context context) {
        super(context, "records", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        sqLiteDatabase.execSQL("CREATE TABLE news(title VARCHAR(255),subTopic VARCHAR(255),content TEXT,imageURL VARCHAR(255),author VARCHAR(255),link VARCHAR(255),linkTitle VARCHAR(255));");

        sqLiteDatabase.execSQL("CREATE TABLE report(ID INTEGER PRIMARY KEY AUTOINCREMENT,year VARCHAR(255),qno VARCHAR(255),bug VARCHAR(255));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL("DROP TABLE news;");

            sqLiteDatabase.execSQL("DROP TABLE report;");

        } finally {

            sqLiteDatabase.execSQL("CREATE TABLE news(title VARCHAR(255),subTopic VARCHAR(255),content TEXT,imageURL VARCHAR(255),author VARCHAR(255),link VARCHAR(255),linkTitle VARCHAR(255));");

            sqLiteDatabase.execSQL("CREATE TABLE report(ID INTEGER PRIMARY KEY AUTOINCREMENT,year VARCHAR(255),qno VARCHAR(255),bug VARCHAR(255));");
        }
    }
}
