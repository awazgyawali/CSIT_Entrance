package np.com.aawaz.csitentrance.objects;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;
    public long time_stamp;
    public String body;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, long time_stamp, String body) {
        this.uid = uid;
        this.author = author;
        this.time_stamp = time_stamp;
        this.body = body;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", uid);
        result.put("message", author);
        result.put("time_stamp", time_stamp);
        result.put("comments", "");
        result.put("image_url", body);

        return result;
    }

}