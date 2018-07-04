package np.com.aawaz.csitentrance.objects

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

import java.util.HashMap

@IgnoreExtraProperties
class Post(var uid: String, var author: String, var time_stamp: Long, var message: String, var image_url: String) {

    lateinit var key: String
    var comment_count: Int = 0


    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["uid"] = uid
        result["author"] = author
        result["time_stamp"] = time_stamp
        result["comment_count"] = 0
        result["message"] = message
        result["image_url"] = image_url

        return result
    }

}