package np.com.aawaz.csitentrance.advance;

import android.database.sqlite.SQLiteDatabase;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmNetworkManager;

import np.com.aawaz.csitentrance.databases.NewsDataBase;

public class Singleton {

    private static Singleton sInstance = null;
    private RequestQueue mRequestQueue;
    private NewsDataBase mDatabase;
    private GcmNetworkManager mScheduler;


    private Singleton() {
        mDatabase = new NewsDataBase(MyApplication.getAppContext());
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
        mScheduler = GcmNetworkManager.getInstance(MyApplication.getAppContext());
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



    public GcmNetworkManager getGcmScheduler(){
        return mScheduler;
    }

}

