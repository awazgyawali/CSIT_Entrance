package np.com.aawaz.csitentrance.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.joanzapata.pdfview.PDFView;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.advance.Singleton;


public class WebViewActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    PDFView pdfview;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        loadAd();

        pdfview = (PDFView) findViewById(R.id.pdfview);

        requestQueue = Singleton.getInstance().getRequestQueue();
        String url = "http://www.google.com";
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
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdfview.fromAsset("question" + getIntent().getExtras().getInt("code") + ".pdf")
                        .defaultPage(1)
                        .showMinimap(false)
                        .enableSwipe(true)
                        .load();
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                finish();
                Toast.makeText(getApplicationContext(), "Unable to connect. Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(request.setTag("fullQue"));
    }

    public void loadAd() {
        final AdView mAdView = (AdView) findViewById(R.id.webViewAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });

    }
}
