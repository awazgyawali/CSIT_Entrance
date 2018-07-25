package np.com.aawaz.csitentrance.objects

import android.content.ContentValues
import np.com.aawaz.csitentrance.misc.MyApplication
import np.com.aawaz.csitentrance.services.NotificationDatabase

class Notification {
    var title: String? = null
    var uid: String? = null
    var text: String? = null
    var post_id: String? = null
    var tag: String? = null
    var time: Long = 0
    var result_published = false

    fun addToDatabase() {
        val database = NotificationDatabase(MyApplication.getAppContext()).writableDatabase

        val values = ContentValues()

        values.put("title", title)
        values.put("text", text)
        values.put("post_id", post_id)
        values.put("tag", tag!!.toUpperCase())
        values.put("time", System.currentTimeMillis())

        database.insert("notification", null, values)
    }

}
