package np.com.aawaz.csitentrance.objects


import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Discussion {

    lateinit var paper_code: String
    lateinit var question_no: String
    lateinit var question_message: String
    lateinit var image_url: String
    var time_stamp: Long = 0
    var comment_count: Int = 0

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    constructor(paper_code: String, question_no: String, time_stamp: Long, comment_count: Int) {
        this.paper_code = paper_code
        this.question_no = question_no
        this.time_stamp = time_stamp
        this.comment_count = comment_count
    }


    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["paper_code"] = paper_code
        result["question_no"] = question_no
        result["time_stamp"] = time_stamp
        result["comment_count"] = comment_count
        return result
    }
}