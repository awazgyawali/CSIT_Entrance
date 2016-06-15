package np.com.aawaz.csitentrance.objects;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class News {

    public String title;
    public String message;
    public long time_stamp;
    public String image_url;

    public News() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
}
