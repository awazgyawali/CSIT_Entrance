package np.com.aawaz.csitentrance.objects;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import np.com.aawaz.csitentrance.misc.MyApplication;

public class EventSender {
    Bundle bundle;

    public EventSender() {
        bundle = new Bundle();
        addValue("data", true);
    }

    public void logEvent(String core_message) {
        FirebaseAnalytics.getInstance(MyApplication.getAppContext()).logEvent("play_radio", bundle);

    }

    public EventSender addValue(String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public EventSender addValue(String key, String value) {
        bundle.putString(key, value);
        return this;
    }
}
