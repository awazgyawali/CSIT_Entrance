package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import np.com.aawaz.csitentrance.R;


public class FullQuestionActivity extends AppCompatActivity {
    WebView webView;
    int code;
    String htmlData = "";
    ProgressBar loading;

    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();

        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_question_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.toolabrFullQuestion));
        loading =  findViewById(R.id.loading_full);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        code = getIntent().getIntExtra("position", 0);

        webView = findViewById(R.id.fullQuestionWebView);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new Object() {

            @JavascriptInterface
            public void performClick(int pos) {
                startActivity(new Intent(FullQuestionActivity.this, DiscussionActivity.class)
                .putExtra("code",code-1)
                .putExtra("position", pos));
            }
        }, "ok");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loading.setVisibility(View.GONE);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        htmlData = "<html lang=\"en\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
                "  <link href=\"https://fonts.googleapis.com/css?family=Work+Sans:500,700\" rel=\"stylesheet\">" +
                "  <title>Document</title>" +
                "  <style>" +
                "    body{" +
                "      margin: 0;" +
                "    }" +
                "    div {" +
                "      font-size: 18px;" +
                "      line-height: 1.5em;" +
                "      text-align: justify;" +
                "      font-family: 'Work Sans', sans-serif" +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "<div>";

        setDataToArrayList();
        TextView title = (TextView) findViewById(R.id.titleFullQuestion);
        title.setText(getResources().getStringArray(R.array.years)[code - 1]);
    }

    public void setDataToArrayList() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONObject obj = new JSONObject(AssetJSONFile("question" + code + ".json", FullQuestionActivity.this));
                    JSONArray m_jArry = obj.getJSONArray("questions");
                    for (int i = 0; i < m_jArry.length(); i++) {
                        JSONObject jo_inside = m_jArry.getJSONObject(i);
                        htmlData = htmlData + "<p onclick=\"ok.performClick("+ i+");\">";
                        htmlData = htmlData + jo_inside.getString("question") + "<br>";
                        htmlData = htmlData + "a) " + jo_inside.getString("a") + "<br>";
                        htmlData = htmlData + "b) " + jo_inside.getString("b") + "<br>";
                        htmlData = htmlData + "c) " + jo_inside.getString("c") + "<br>";
                        htmlData = htmlData + "d) " + jo_inside.getString("d") + "<br>";
                        htmlData = htmlData + "<b>Answer: " + jo_inside.getString("ans") + "</b><br>";
                        htmlData = htmlData + "</p>";
                        htmlData = htmlData + "<hr>";
                    }

                    htmlData = htmlData + "</div>" +
                            "</body>" +
                            "</html>";
                } catch (Exception ignored) {
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                webView.loadDataWithBaseURL("", htmlData, "text/html", "utf-8", null);
            }
        }.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
