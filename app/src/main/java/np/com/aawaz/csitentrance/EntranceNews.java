package np.com.aawaz.csitentrance;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class EntranceNews extends AppCompatActivity {

    ArrayList<String> topic=new ArrayList<>(),
            subTopic=new ArrayList<>(),
            imageURL=new ArrayList<>(),
            content=new ArrayList<>(),
            author=new ArrayList<>();
    RecyclerView recy;
    SwipeRefreshLayout refreshLayout;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance_news);

        loadAd();

        context=this;

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

    public void fillData(){
        recy= (RecyclerView) findViewById(R.id.newsFeedRecy);
        NewsAdapter newsAdapter=new NewsAdapter(this,topic,subTopic,imageURL,content,author);
        recy.setAdapter(newsAdapter);
        recy.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout.setRefreshing(false);
    }

    public void setDataToArrayListFromDb(){
        NewsDataBase dataBase=new NewsDataBase(this);
        SQLiteDatabase database= dataBase.getWritableDatabase();
        Cursor cursor=database.query("news", new String[]{"title,subTopic,content,imageURL,author"}, null, null, null, null, null);
        int i=0;
        while(cursor.moveToNext()){
            i++;
            topic.add(cursor.getString(cursor.getColumnIndex("title")));
            subTopic.add(cursor.getString(cursor.getColumnIndex("subTopic")));
            content.add(cursor.getString(cursor.getColumnIndex("content")));
            imageURL.add(cursor.getString(cursor.getColumnIndex("imageURL")));
            author.add(cursor.getString(cursor.getColumnIndex("author")));
        }
        if(i==0){
            setDataToArrayList(true);
        }else {
            fillData();
        }
        cursor.close();
        database.close();

    }

    public void setDataToArrayList(final boolean finish) {

        //Reading from json file and insillizing inside arrayList
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .content("Please wait")
                    .progress(true,0)
                    .show();
        if(!finish){
            dialog.dismiss();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url=getString(R.string.url);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                topic.clear();
                subTopic.clear();
                imageURL.clear();
                content.clear();
                author.clear();
                try {
                    JSONArray jsonArray = response.getJSONArray("news");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);
                        topic.add(jo_inside.getString("topic"));
                        subTopic.add(jo_inside.getString("subTopic"));
                        imageURL.add(jo_inside.getString("imageURL"));
                        content.add(jo_inside.getString("content"));
                        author.add(jo_inside.getString("author"));
                    }
                    fillData();
                    storeToDb();
                    dialog.dismiss();
                    refreshLayout.setRefreshing(false);
                    new SnackBar.Builder((Activity) context)
                            .withMessage("News updated.")
                            .show();
                } catch (Exception e) {
                    refreshLayout.setRefreshing(false);

                    if(finish){
                        finish();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Something went wrong. Please try again later.",Toast.LENGTH_LONG).show();
                    } else {
                        new SnackBar.Builder((Activity) context)
                                .withMessage("Something went wrong. Report us.")
                                .show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshLayout.setRefreshing(false);
                if(finish){
                    finish();
                    Toast.makeText(getApplicationContext(),"Unable to fetch news. Please check your internet connection.",Toast.LENGTH_LONG).show();
                } else{
                    new SnackBar.Builder((Activity) context)
                            .withMessage("Unable to fetch news. Swipe down to retry.")
                            .show();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void storeToDb() {
        NewsDataBase dataBase=new NewsDataBase(this);
        SQLiteDatabase database= dataBase.getWritableDatabase();
        database.delete("news",null,null);
        ContentValues values=new ContentValues();
        for(int i=0;i<topic.size();i++) {
            values.clear();
            values.put("title", topic.get(i));
            values.put("subTopic", subTopic.get(i));
            values.put("content", content.get(i));
            values.put("imageURL", imageURL.get(i));
            values.put("author", author.get(i));
            database.insert("news",null,values);
        }
        database.close();
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

    public void loadAd(){
        AdView mAdView = (AdView) findViewById(R.id.eachNewsAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

}
