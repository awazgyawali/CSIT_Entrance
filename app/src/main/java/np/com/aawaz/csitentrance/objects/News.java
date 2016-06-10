package np.com.aawaz.csitentrance.objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class News {

    public String title;
    public String message;
    public long time_stamp;
    public String image_url;

    public News() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public News(String uid, String author, long time_stamp, String body) {
        this.title = uid;
        this.message = author;
        this.time_stamp = time_stamp;
        this.image_url = body;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("message", message);
        result.put("time_stamp", time_stamp);
        result.put("image_url", image_url);

        return result;
    }


}
