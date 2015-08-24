package np.com.aawaz.csitentrance.activities;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.ScoreBoardAdapter;
import np.com.aawaz.csitentrance.advance.MyApplication;
import np.com.aawaz.csitentrance.advance.Singleton;


public class ScoreBoard extends AppCompatActivity {

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();
    MaterialDialog dialogInitial;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        loadAd();
        setSupportActionBar((Toolbar) findViewById(R.id.ScoreToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        MyApplication.changeStatusBarColor(R.color.primary1, this);
        YoYo.with(Techniques.SlideInDown)
                .duration(1500)
                .playOn(findViewById(R.id.coreLayoutScore));
        dialogInitial = new MaterialDialog.Builder(this)
                .content("Please wait...")
                .progress(true, 0)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        requestQueue.cancelAll("score");
                        finish();
                    }
                })
                .build();
        dialogInitial.show();
        fetchFromInternet();

    }

    private void callFillRecyclerView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.scoreBoardRecyclerView);
        mRecyclerView.setAdapter(new ScoreBoardAdapter(this, names, scores));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 3 : 2, StaggeredGridLayoutManager.VERTICAL));
        dialogInitial.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchFromInternet() {
        requestQueue = Singleton.getInstance().getRequestQueue();
        String url = getString(R.string.fetchScoreurl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("scores");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);
                        names.add(jo_inside.getString("Name"));
                        scores.add(jo_inside.getInt("Score"));
                    }
                    callFillRecyclerView();
                } catch (JSONException e) {
                    dialogInitial.dismiss();
                    finish();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogInitial.dismiss();
                Toast.makeText(getApplicationContext(), "Unable to connect. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        requestQueue.add(jsonObjectRequest.setTag("score"));
    }

    public void loadAd() {
        final AdView mAdView = (AdView) findViewById(R.id.scoreBoardAd);
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

    public boolean isLargeScreen() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
