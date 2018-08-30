package np.com.aawaz.csitentrance.objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String author;
    public long time_stamp;
    public String message;
    public String image_url;
    public String attached_image;
    public List<String> likes;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Comment(String uid, String author, long time_stamp, String message, String image_url, String attached_image) {
        this.uid = uid;
        this.author = author;
        this.time_stamp = time_stamp;
        this.message = message;
        this.image_url = image_url;
        this.attached_image = attached_image;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("time_stamp", time_stamp);
        result.put("message", message);
        result.put("image_url", image_url);
        result.put("attached_image", attached_image);
        return result;
    }

}