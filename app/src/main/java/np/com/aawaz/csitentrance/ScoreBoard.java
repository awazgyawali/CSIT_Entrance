package np.com.aawaz.csitentrance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ScoreBoard extends AppCompatActivity {

    ArrayList<String> names = new ArrayList<>();
    MaterialDialog dialogInitial;
    ArrayList<Integer> scores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        loadAd();
        Toolbar toolbar = (Toolbar) findViewById(R.id.ScoreToolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary1));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        YoYo.with(Techniques.SlideInDown)
                .duration(1500)
                .playOn(findViewById(R.id.coreLayoutScore));
        dialogInitial = new MaterialDialog.Builder(this)
                .content("Please wait...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        dialogInitial.show();
        fetchFromInternet();

    }

    private void callFillRecyclerView() {
        Log.d("debug", names + " and " + scores);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.scoreBoardRecyclerView);
        mRecyclerView.setAdapter(new ScoreBoardAdapter(this, names, scores));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
                    sortScoreAndName();
                } catch (JSONException e) {
                    dialogInitial.dismiss();
                    Toast.makeText(getApplicationContext(), "Internal error. Please report us.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogInitial.dismiss();
                Toast.makeText(getApplicationContext(), "Unable to get the scores. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void sortScoreAndName() {
        for (int i = 0; i < scores.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (scores.get(i) > scores.get(j))
                    swap(i, j);
            }
        }
        callFillRecyclerView();
    }

    private void swap(int i, int j) {
        int tempSco = scores.get(i);
        String tempName = names.get(i);
        scores.remove(i);
        names.remove(i);
        scores.add(i, scores.get(j));
        names.add(i, names.get(j));
        scores.remove(j);
        names.remove(j);
        scores.add(j, tempSco);
        names.add(j, tempName);

    }

    public void loadAd() {
        AdView mAdView = (AdView) findViewById(R.id.scoreBoardAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

}
