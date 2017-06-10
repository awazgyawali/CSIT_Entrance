package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.squareup.picasso.Picasso;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;

public class EachNews extends AppCompatActivity {

    private String mUrl;
    private String mTitle;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_news);
        new EventSender().logEvent("each_news");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_each_news);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) toolbar.getParent();
        collapsingToolbarLayout.setTitle("Entrance News");


        TextView
                newsDetail = (TextView) findViewById(R.id.each_news_detail);
        TextView time = (TextView) findViewById(R.id.each_news_time),
                title = (TextView) findViewById(R.id.each_news_title);
        ImageView imageView = (ImageView) findViewById(R.id.each_news_image);

        bundle = getIntent().getBundleExtra("data");
        title.setText(bundle.getString("title"));
        newsDetail.setText(Html.fromHtml(bundle.getString("detail")));
        time.setText(bundle.getString("time"));
        if (bundle.getString("image_link").equals("null")) {
            imageView.setVisibility(View.GONE);
            collapsingToolbarLayout.setTitle("Entrance News");
        } else {
            Picasso.with(this)
                    .load(bundle.getString("image_link"))
                    .into(imageView);
            imageView.setVisibility(View.VISIBLE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EachNews.this, ImageViewActivity.class)
                        .putExtra("image_link", bundle.getString("image_link")));
            }
        });
        appIndexing();
    }


    private void appIndexing() {
        mUrl = "http://csitentrance.brainants.com/news";
        mTitle = bundle.getString("title");
    }


    public com.google.firebase.appindexing.Action getAction() {
        return Actions.newView(mTitle, mUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUserActions.getInstance().start(getAction());
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
