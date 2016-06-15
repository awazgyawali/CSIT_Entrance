package np.com.aawaz.csitentrance.objects;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FAQ {

    public String question;
    public String answer;

    public FAQ() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
}
