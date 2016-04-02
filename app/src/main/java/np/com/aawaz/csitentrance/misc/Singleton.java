package np.com.aawaz.csitentrance.misc;

import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmNetworkManager;

import np.com.aawaz.csitentrance.databases.DataBaseHelper;

public class Singleton {

    private static Singleton sInstance = null;
    private RequestQueue mRequestQueue;
    private DataBaseHelper mDatabase;
    private GcmNetworkManager mScheduler;

    public static boolean isLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private Singleton() {
        mDatabase = new DataBaseHelper(MyApplication.getAppContext());
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


    public static boolean isNwConnected(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
        if (nwInfo != null && nwInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}

