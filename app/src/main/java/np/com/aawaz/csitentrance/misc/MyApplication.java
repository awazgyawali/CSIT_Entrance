package np.com.aawaz.csitentrance.misc;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;


public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(this);
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(SPHandler.getInstance().getID());
        Crashlytics.setUserEmail(SPHandler.getInstance().getEmail());
        Crashlytics.setUserName(SPHandler.getInstance().getFullName());
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}