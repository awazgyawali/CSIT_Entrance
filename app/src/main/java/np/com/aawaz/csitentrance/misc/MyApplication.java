package np.com.aawaz.csitentrance.misc;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;


public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        sInstance = this;
        FacebookSdk.sdkInitialize(this);
        super.onCreate();
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}