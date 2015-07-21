package np.com.aawaz.csitentrance.AdvanceClasses;

import android.database.sqlite.SQLiteDatabase;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import np.com.aawaz.csitentrance.Databases.NewsDataBase;

public class Singleton {

    private static Singleton sInstance = null;
    private RequestQueue mRequestQueue;
    private NewsDataBase mDatabase;

    private Singleton() {
        mDatabase = new NewsDataBase(MyApplication.getAppContext());
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
    }

    public static Singleton getInstance() {
        if (sInstance == null) {
            sInstance = new Singleton();
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase.getWritableDatabase();
    }
}

