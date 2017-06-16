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
                + "     #text"
                + "         {  "
                + "             font-size:14;"
                + "         }"
                + "     </style>"
                + "</head>"
                + "<body>"
                + "<image_url dir=\"rtl\" id=\"text\">"
                + data
                + "</image_url>"
                + "</body>"
                + "</html>  ";

        loadDataWithBaseURL(null, strBody, "text/html", "utf-8",
                null);
    }
}
