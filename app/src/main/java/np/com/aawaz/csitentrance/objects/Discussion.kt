package np.com.aawaz.csitentrance.objects


import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Discussion {

    lateinit var paper_code: String
    lateinit var question_no: String
    var question_message: String? = null
    var image_url: String? = null
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
}