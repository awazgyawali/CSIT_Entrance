package np.com.aawaz.csitentrance.misc;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;


public class MyApplication extends Application {
    private static MyApplication sInstance;

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Fabric.with(this, new Crashlytics());
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

}