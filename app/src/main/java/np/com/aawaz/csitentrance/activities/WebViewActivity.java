package np.com.aawaz.csitentrance.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
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
        pdfview.fromAsset("question" + getIntent().getExtras().getInt("code") + ".pdf")
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .load();
        /*
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Connecting. Please wait...")
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
        */
    }

    public void reportFull(View v) {
        final MaterialDialog dialogMis = new MaterialDialog.Builder(this)
                .title("Found mistake on")
                .items(R.array.list)
                .positiveText("Cancel")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        ContentValues values = new ContentValues();
                        SQLiteDatabase database = Singleton.getInstance().getDatabase();
                        values.put("text", "PDF Year: " + (getIntent().getExtras().getInt("code") + 2068) + charSequence);
                        database.insert("report", null, values);
                        database.close();
                    }
                })
                .build();
        dialogMis.show();
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
