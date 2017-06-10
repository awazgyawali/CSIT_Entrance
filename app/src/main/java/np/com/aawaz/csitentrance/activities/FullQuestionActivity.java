package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_question_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.toolabrFullQuestion));
        loading = (ProgressBar) findViewById(R.id.loading_full);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        code = getIntent().getIntExtra("position", 0);

        webView = (WebView) findViewById(R.id.fullQuestionWebView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loading.setVisibility(View.GONE);
            }
        });
        setDataToArrayList();
        TextView title = (TextView) findViewById(R.id.titleFullQuestion);
        title.setText(getResources().getStringArray(R.array.years)[code - 1]);
    }


    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();

        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    public void setDataToArrayList() {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONObject obj = new JSONObject(AssetJSONFile("question" + code + ".json", FullQuestionActivity.this));
                    JSONArray m_jArry = obj.getJSONArray("questions");
                    for (int i = 0; i < m_jArry.length(); i++) {
                        JSONObject jo_inside = m_jArry.getJSONObject(i);
                        htmlData = htmlData + jo_inside.getString("question") + "<br>";
                        htmlData = htmlData + "a) " + jo_inside.getString("a") + "<br>";
                        htmlData = htmlData + "b) " + jo_inside.getString("b") + "<br>";
                        htmlData = htmlData + "c) " + jo_inside.getString("c") + "<br>";
                        htmlData = htmlData + "d) " + jo_inside.getString("d") + "<br>";
                        htmlData = htmlData + "<b>Answer: " + jo_inside.getString("ans") + "</b><br>";
                        htmlData = htmlData + "<hr>";
                    }
                } catch (Exception ignored) {
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                webView.loadDataWithBaseURL("", htmlData, "text/html", "utf-8", null);
            }
        });
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
