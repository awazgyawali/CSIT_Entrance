package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.io.IOException;
import java.io.InputStream;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.misc.QuizTextView;
import np.com.aawaz.csitentrance.misc.Singleton;


public class WebViewActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    QuizTextView webView;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (QuizTextView) findViewById(R.id.pdfview);

        requestQueue = Singleton.getInstance().getRequestQueue();
        progressBar = (ProgressBar) findViewById(R.id.progressWebView);


        webView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        try {
            webView.setScript(AssetJSONFile("question" + getIntent().getExtras().getInt("code") + ".htm", this));
        } catch (IOException e) {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            finish();
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();

        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }
}
