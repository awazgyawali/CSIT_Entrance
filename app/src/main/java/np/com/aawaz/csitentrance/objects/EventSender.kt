package np.com.aawaz.csitentrance.objects

import android.os.Bundle

import com.google.firebase.analytics.FirebaseAnalytics

import np.com.aawaz.csitentrance.misc.MyApplication

class EventSender {
    private val bundle: Bundle = Bundle()

    init {
        addValue("data", true)
    }

    fun logEvent(core_message: String) {
        FirebaseAnalytics.getInstance(MyApplication.getAppContext()).logEvent(core_message, bundle)

    }

    private fun addValue(key: String, value: Boolean): EventSender {
        bundle.putBoolean(key, value)
        return this
    }
}
