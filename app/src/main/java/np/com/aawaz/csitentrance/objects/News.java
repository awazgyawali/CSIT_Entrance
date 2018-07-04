package np.com.aawaz.csitentrance.objects;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class News {

    public String title, author;
    public String message, excerpt;
    public long time_stamp;

    public News() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
}