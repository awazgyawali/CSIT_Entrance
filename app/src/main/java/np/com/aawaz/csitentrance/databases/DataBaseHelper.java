package np.com.aawaz.csitentrance.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context) {
        super(context, "records", null, 8);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE news(title TEXT, message TEXT, imageURL TEXT, time VARCHAR(255));");
        sqLiteDatabase.execSQL("CREATE TABLE forum(id INT, poster TEXT, message TEXT, time TEXT, image_link TEXT, comments INT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS news");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS forum");
        onCreate(sqLiteDatabase);
    }
}
