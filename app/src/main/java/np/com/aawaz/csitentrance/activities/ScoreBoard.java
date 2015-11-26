package np.com.aawaz.csitentrance.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.ScoreBoardAdapter;
import np.com.aawaz.csitentrance.advance.BackgroundTaskHandler;
import np.com.aawaz.csitentrance.advance.MyApplication;
import np.com.aawaz.csitentrance.advance.Singleton;


public class ScoreBoard extends AppCompatActivity {

    ArrayList<String> names = new ArrayList<>();
    ArrayList<Integer> scores = new ArrayList<>();
    MaterialDialog dialogInitial;
    RequestQueue requestQueue;

    CallbackManager callbackManager;
    ShareDialog shareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
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
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    public boolean isLargeScreen() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void share(View view) {
        Toast.makeText(this, "Generating sharing messages. Please wait...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(MyApplication.writeTextOnDrawable(ScoreBoard.this, ScoreBoard.this.getSharedPreferences("info", MODE_PRIVATE).getString("Name", "") + " has scored " + BackgroundTaskHandler.getTotal()
                                + " out of 800.").getBitmap())
                        .build();

                final SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //uddate UI
                        shareDialog.show(content);
                    }
                });

            }
        }).start();
    }
}

