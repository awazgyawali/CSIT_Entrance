package np.com.aawaz.csitentrance.misc;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;


public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}