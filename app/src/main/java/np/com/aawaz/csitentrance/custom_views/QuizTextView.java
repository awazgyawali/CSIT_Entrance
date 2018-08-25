package np.com.aawaz.csitentrance.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class QuizTextView extends WebView {
    public QuizTextView(Context context) {
        super(context);
    }

    public QuizTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuizTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScript(String data) {
        String strBody = "<html>"
                + "<head>"
                + "     <style type=\"text/css\">"
                + "     <link href=\"https://fonts.googleapis.com/css?family=Work+Sans:500,700\" rel=\"stylesheet\">"
                + "     #text"
                + "         {  "
                + "             font-size:14;"
                + "         }"
                + "    p {" +
                "      font-size: 18px;" +
                "      line-height: 1.5em;" +
                "      text-align: justify;" +
                "      font-family: 'Work Sans', sans-serif" +
                "    }"
                + "     </style>"
                + "</head>"
                + "<body>"
                + "<p>"
                + data
                + "</p>"
                + "</body>"
                + "</html>  ";

        loadDataWithBaseURL(null, strBody, "text/html", "utf-8",
                null);
    }
}
