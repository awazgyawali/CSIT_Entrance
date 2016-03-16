package np.com.aawaz.csitentrance.misc;

import android.content.Context;
import android.content.res.Configuration;
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

    public static boolean isLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

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

    public static String getImageLini() {
        return MyApplication.getAppContext().getSharedPreferences("info", Context.MODE_PRIVATE).getString("ImageLink", "");
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase.getWritableDatabase();
    }

    public GcmNetworkManager getGcmScheduler() {
        return mScheduler;
    }

    public static String getName() {
        return MyApplication.getAppContext().getSharedPreferences("info", Context.MODE_PRIVATE).getString("Name", "");
    }
    public static String getEmail() {
        return MyApplication.getAppContext().getSharedPreferences("info", Context.MODE_PRIVATE).getString("E-mail", "");
    }
}

