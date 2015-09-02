package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.advance.MyApplication;

public class ReadNews extends AppCompatActivity {

    TextView titleEach, contentEach, authorEach, subTopicEach;
    Button linkButton;
    ImageView newsImage;
    CardView core;

    String topic, content, author, subTopic, link, linkTitle, imageURL;

    private void initilizeViews() {
        titleEach = (TextView) findViewById(R.id.eachNewsTitle);
        contentEach = (TextView) findViewById(R.id.newsContent);
        authorEach = (TextView) findViewById(R.id.newsAuthor);
        subTopicEach = (TextView) findViewById(R.id.newsSubTitle);
        linkButton = (Button) findViewById(R.id.linkButton);
        newsImage = (ImageView) findViewById(R.id.newsImage);
        core = (CardView) findViewById(R.id.fullNewsCore);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news);
        setSupportActionBar((Toolbar) findViewById(R.id.fullNewsToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        MyApplication.changeStatusBarColor(R.color.status_bar_news, this);

        initilizeViews();

        getFromIntent();

        YoYo.with(Techniques.FadeInRight)
                .duration(700)
                .playOn(core);
        getSupportActionBar().setTitle(topic);
        titleEach.setText(topic);
        contentEach.setText(content);
        authorEach.setText(author);
        subTopicEach.setText(subTopic);
        if (!link.equals("null")) {
            linkButton.setText(linkTitle);
            linkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                }
            });
            linkButton.setVisibility(View.VISIBLE);
        } else {
            linkButton.setVisibility(View.GONE);
        }
        if (imageURL.equals("null")) {
            newsImage.setVisibility(View.GONE);
        } else {
            newsImage.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(imageURL)
                    .into(newsImage);
            newsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageURL)));
                }
            });
        }

    }

    private void getFromIntent() {
        Bundle bundle = getIntent().getExtras().getBundle("bundle");

        topic = bundle.getString("title");
        subTopic = bundle.getString("subTopic");
        imageURL = bundle.getString("imageURL");
        content = bundle.getString("content");
        linkTitle = bundle.getString("linkTitle");
        link = bundle.getString("link");
        author = bundle.getString("author");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        YoYo.with(Techniques.FadeOutRight)
                .duration(700)
                .playOn(core);
        super.onBackPressed();
    }
}
