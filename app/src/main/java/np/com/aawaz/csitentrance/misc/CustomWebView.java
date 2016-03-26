package np.com.aawaz.csitentrance.misc;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

public class CustomWebView extends WebView {
    public CustomWebView(Context context) {
        super(context);
        initializer();
    }

    private void initializer() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setBuiltInZoomControls(true);
        loadDataWithBaseURL("http://bar/", "<script type='text/x-mathjax-config'>"
                + "MathJax.Hub.Config({ "
                + "showMathMenu: false, "
                + "jax: ['input/TeX','output/HTML-CSS'], " // output/SVG
                + "extensions: ['tex2jax.js','toMathML.js'], "
                + "TeX: { extensions: ['AMSmath.js','AMSsymbols.js',"
                + "'noErrors.js','noUndefined.js'] }, "
                //+"'SVG' : { blacker: 30, "
                // +"styles: { path: { 'shape-rendering': 'crispEdges' } } } "
                + "});</script>"
                + "<script type='text/javascript' "
                + "src='file:///android_asset/MathJax/MathJax.js'"
                + "></script>"
                + "<script type='text/javascript'>getLiteralMML = function() {"
                + "math=MathJax.Hub.getAllJax('math')[0];"
                // below, toMathML() rerurns literal MathML string
                + "mml=math.root.toMathML(''); return mml;"
                + "}; getEscapedMML = function() {"
                + "math=MathJax.Hub.getAllJax('math')[0];"
                // below, toMathMLquote() applies &-escaping to MathML string input
                + "mml=math.root.toMathMLquote(getLiteralMML()); return mml;}"
                + "</script>"
                + "<span id='math'></span><pre><span id='mmlout'></span></pre>", "text/html", "utf-8", "");
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializer();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializer();
    }

    public void setScript(String data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:document.getElementById('math').innerHTML='\\\\[" + doubleEscapeTeX(data) + "\\\\]';", null);
            evaluateJavascript("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);", null);
        } else {
            loadUrl("javascript:document.getElementById('math').innerHTML='\\\\[" + doubleEscapeTeX(data) + "\\\\]';");
            loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
        }
    }

    private String doubleEscapeTeX(String s) {
        String t = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\'') t += '\\';
            if (s.charAt(i) != '\n') t += s.charAt(i);
            if (s.charAt(i) == '\\') t += "\\";
        }
        return t;
    }
}
