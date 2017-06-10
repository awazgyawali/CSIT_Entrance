package np.com.aawaz.csitentrance.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Singleton {

    private static Singleton sInstance = null;

    private Singleton() {

    }

    public static Singleton getInstance() {
        if (sInstance == null) {
            sInstance = new Singleton();
        }
        return sInstance;
    }


    public static boolean isNwConnected(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
        return nwInfo != null && nwInfo.isConnectedOrConnecting();
    }
}

