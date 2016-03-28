package np.com.aawaz.csitentrance.misc;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;


public class MyApplication extends Application {
    private static MyApplication sInstance;

    private static final String TWITTER_KEY = "uOjHlNbqyh9XRso56VNEqw8nq";
    private static final String TWITTER_SECRET = "G7RafnCSDGmm9I8yCTvvUCBKusLEwHrM6ImjS4P0IVsga3Sy5Y";


    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        FacebookSdk.sdkInitialize(this);
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}