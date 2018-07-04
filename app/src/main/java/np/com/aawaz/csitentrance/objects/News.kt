package np.com.aawaz.csitentrance.objects

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class News {

    var title: String? = null
    var author: String? = null
    var message: String? = null
    var excerpt: String? = null
    var time_stamp: Long = 0
}
