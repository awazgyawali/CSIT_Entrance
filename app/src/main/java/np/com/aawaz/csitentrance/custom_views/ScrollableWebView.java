package np.com.aawaz.csitentrance.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class ScrollableWebView extends WebView {
    public ScrollableWebView(Context context) {
        super(context);
    }

    public ScrollableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollableWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }
}
