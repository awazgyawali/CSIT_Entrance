package np.com.aawaz.csitentrance.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.News;

public class NewsDetailActivity extends AppCompatActivity {

    Bundle bundle;
    private String mUrl;
    private String mTitle;
    TextView time, title,author;
    WebView newsDetail;
    ViewSwitcher viewSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_news);
        new EventSender().logEvent("each_news");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_each_news);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Entrance News");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcherNews);
        newsDetail = (WebView) findViewById(R.id.each_news_detail);
        time = (TextView) findViewById(R.id.each_news_time);
        title = (TextView) findViewById(R.id.each_news_title);
        author = (TextView) findViewById(R.id.each_news_author);
        if (getIntent().getStringExtra("news_id") != null) {
            fetchFromInternet(getIntent().getStringExtra("news_id"));
        } else {
            bundle = getIntent().getBundleExtra("data");
            title.setText(bundle.getString("title"));
            author.setText(bundle.getString("author"));
            newsDetail.loadDataWithBaseURL("", readyWithCSS(bundle.getString("detail")), "text/html", "UTF-8", "");
            time.setText(convertToSimpleDate(bundle.getLong("time")));
            viewSwitcher.showNext();
            appIndexing(bundle.getString("title"));
        }
    }

    private void fetchFromInternet(String post_id) {
        FirebaseDatabase.getInstance().getReference().child("news").child(post_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                News news = dataSnapshot.getValue(News.class);
                title.setText(news.title);
                author.setText(news.author);
                newsDetail.loadData(readyWithCSS(news.message), "text/html", "UTF-8");
                time.setText(convertToSimpleDate(news.time_stamp));
                viewSwitcher.showNext();
                appIndexing(news.title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NewsDetailActivity.this, "Unable to fetch news. Check your internet connection.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private CharSequence convertToSimpleDate(long created_time) {
        return DateUtils.getRelativeTimeSpanString(created_time, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
    }

    private String readyWithCSS(String string) {
        String initial = "<html lang=\"en\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">" +
                "  <title>Document</title>" +
                "  <style>" +
                "    body{" +
                "      margin: 0;" +
                "    }" +
                "    img {" +
                "      width: 100%;" +
                "    }" +
                "    p {" +
                "      font-size: 18px;" +
                "      line-height: 1.5em;" +
                "      text-align: justify;" +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>";
        String footer = "</body>" +
                "</html>";
        return initial + string + footer;
    }


    private void appIndexing(String mTitle) {
        mUrl = "http://csitentrance.brainants.com/news";
        this.mTitle = mTitle;
        FirebaseUserActions.getInstance().start(getAction());
    }


    public com.google.firebase.appindexing.Action getAction() {
        return Actions.newView(mTitle, mUrl);
    }

    @Override
    public void onStop() {
        FirebaseUserActions.getInstance().end(getAction());
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
