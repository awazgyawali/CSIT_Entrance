package np.com.aawaz.csitentrance.objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    public String uid, key;
    public String author;
    public long time_stamp;
    public String message;
    public String image_url;
    public int comment_count;
    public List<String> likes;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, long time_stamp, String message, String image_url) {
        this.uid = uid;
        this.author = author;
        this.time_stamp = time_stamp;
        this.message = message;
        this.image_url = image_url;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("time_stamp", time_stamp);
        result.put("comment_count", 0);
        result.put("message", message);
        result.put("image_url", image_url);

        return result;
    }

}