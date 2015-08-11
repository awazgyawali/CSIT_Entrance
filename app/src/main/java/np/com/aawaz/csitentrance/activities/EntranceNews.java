package np.com.aawaz.csitentrance.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.NewsAdapter;
import np.com.aawaz.csitentrance.advance.Singleton;


public class EntranceNews extends AppCompatActivity {

    ArrayList<String> topic = new ArrayList<>(),
            subTopic = new ArrayList<>(),
            imageURL = new ArrayList<>(),
            content = new ArrayList<>(),
            author = new ArrayList<>(),
            link = new ArrayList<>(),
            linkTitle = new ArrayList<>();
    RecyclerView recy;
    SwipeRefreshLayout refreshLayout;
    Context context;
    RequestQueue requestQueue;
    NewsAdapter newsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance_news);

        loadAd();

        context = this;

        //Toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.newsToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshNews);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setDataToArrayList(false);
            }
        });

        //Setting the data
        setDataToArrayListFromDb();
    }

    public void fillData() {
        recy = (RecyclerView) findViewById(R.id.newsFeedRecy);
        newsAdapter = new NewsAdapter(this, topic, subTopic, imageURL, content, author, link, linkTitle, recy);
        recy.setAdapter(newsAdapter);
        recy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
        refreshLayout.setRefreshing(false);
    }

    public void setDataToArrayListFromDb() {
        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        Cursor cursor = database.query("news", new String[]{"title","subTopic","content","imageURL","author","link","linkTitle"}, null, null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
            topic.add(cursor.getString(cursor.getColumnIndex("title")));
            subTopic.add(cursor.getString(cursor.getColumnIndex("subTopic")));
            content.add(cursor.getString(cursor.getColumnIndex("content")));
            imageURL.add(cursor.getString(cursor.getColumnIndex("imageURL")));
            author.add(cursor.getString(cursor.getColumnIndex("author")));
            link.add(cursor.getString(cursor.getColumnIndex("link")));
            linkTitle.add(cursor.getString(cursor.getColumnIndex("linkTitle")));
        }
        if (i == 0) {
            setDataToArrayList(true);
        } else {
            fillData();
        }
        cursor.close();
        database.close();

    }

    public void setDataToArrayList(final boolean finish) {

        //Reading from json file and insillizing inside arrayList
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Please wait")
                .progress(true, 0)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        requestQueue.cancelAll("news");
                        finish();
                    }
                })
                .show();
        if (!finish) {
            dialog.dismiss();
        }
        requestQueue = Singleton.getInstance().getRequestQueue();
        String url = getString(R.string.url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                topic.clear();
                subTopic.clear();
                imageURL.clear();
                content.clear();
                author.clear();
                linkTitle.clear();
                link.clear();
                try {
                    JSONArray jsonArray = response.getJSONArray("news");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);
                        topic.add(jo_inside.getString("topic"));
                        subTopic.add(jo_inside.getString("subTopic"));
                        imageURL.add(jo_inside.getString("imageURL"));
                        content.add(jo_inside.getString("content"));
                        author.add(jo_inside.getString("author"));
                        link.add(jo_inside.getString("link"));
                        linkTitle.add(jo_inside.getString("linkTitle"));
                    }
                    fillData();
                    storeToDb();
                    context.getSharedPreferences("values",Context.MODE_PRIVATE).edit().putInt("score8",topic.size()).apply();
                    dialog.dismiss();
                    refreshLayout.setRefreshing(false);
                    Snackbar.make(findViewById(R.id.parentNews), "News updated.", Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    refreshLayout.setRefreshing(false);
                    if (finish) {
                        finish();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(R.id.parentNews), "Something went wrong. Report us.", Snackbar.LENGTH_LONG).show();

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshLayout.setRefreshing(false);
                if (finish) {
                    finish();
                    Toast.makeText(getApplicationContext(), "Unable to fetch news. Please check your internet connection.", Toast.LENGTH_LONG).show();
                } else {
                    Snackbar.make(findViewById(R.id.parentNews), "Unable to fetch news. Swipe down to retry.", Snackbar.LENGTH_LONG).show();

                }
            }
        });
        requestQueue.add(jsonObjectRequest.setTag("news"));
    }

    private void storeToDb() {
        SQLiteDatabase database = Singleton.getInstance().getDatabase();
        database.delete("news", null, null);
        ContentValues values = new ContentValues();
        for (int i = 0; i < topic.size(); i++) {
            values.clear();
            values.put("title", topic.get(i));
            values.put("subTopic", subTopic.get(i));
            values.put("content", content.get(i));
            values.put("imageURL", imageURL.get(i));
            values.put("author", author.get(i));
            values.put("link", link.get(i));
            values.put("linkTitle", linkTitle.get(i));
            database.insert("news", null, values);
        }
        database.close();
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

    public void loadAd() {
        final AdView mAdView = (AdView) findViewById(R.id.eachNewsAd);
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

    @Override
    public void onBackPressed() {
        if (newsAdapter != null && newsAdapter.expandedPosition != -1) {
            int prevPosi = newsAdapter.expandedPosition;
            newsAdapter.expandedPosition = -1;
            newsAdapter.notifyItemChanged(prevPosi);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    public boolean isLargeScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return false;
        else
            return (getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_NORMAL;
    }
}
