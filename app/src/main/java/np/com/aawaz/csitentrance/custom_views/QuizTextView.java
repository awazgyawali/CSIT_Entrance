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
                + "     @font-face "
                + "         {   font-family: Tahoma;"
                + "             src:url(\"~/android_asset/fonts/Roboto-Regular.ttf\") "
                + "         }"
                + "     #text"
                + "         {   font-family: Tahoma;"
                + "             font-size:14;"
                + "             text-align: center;"
                + "         }"
                + "     </style>"
                + ""
                + "</head>"
                + "<image_url dir=\"rtl\" id=\"text\">"
                + data
                + " </image_url></html>  ";

        loadDataWithBaseURL(null, strBody, "text/html", "utf-8",
                null);
    }
}
