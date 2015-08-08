package np.com.aawaz.csitentrance.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.advance.Singleton;


public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    RequestQueue requestQueue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        loadAd();

        webView = (WebView) findViewById(R.id.webView);

        requestQueue = Singleton.getInstance().getRequestQueue();
        String url = getResources().getString(R.string.url);
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Please wait...")
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        requestQueue.cancelAll("fullQue");
                        finish();
                    }
                })
                .progress(true, 0)
                .show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                webView.loadUrl("file:///android_asset/question" + getIntent().getExtras().getInt("code") + ".html");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                finish();
                Toast.makeText(getApplicationContext(), "Unable to connect. Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest.setTag("fullQue"));
    }

    public void loadAd() {
        AdView mAdView = (AdView) findViewById(R.id.webViewAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
