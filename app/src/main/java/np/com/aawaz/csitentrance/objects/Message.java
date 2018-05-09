package np.com.aawaz.csitentrance.objects;

//user = 0, bot = 1
public class Message {
    public static  int BOT = 1;
    public static  int USER = 0;
    public String text;
    public int messageType;

    public Message(String message, int sender){
        this.text = message;
        this.messageType = sender;
    }
}
