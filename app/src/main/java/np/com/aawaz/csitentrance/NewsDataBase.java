package np.com.aawaz.csitentrance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NewsDataBase extends SQLiteOpenHelper {

    public NewsDataBase(Context context) {
        super(context, "records", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE news(title VARCHAR(255),subTopic VARCHAR(255),content TEXT,imageURL VARCHAR(255),author VARCHAR(255));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
