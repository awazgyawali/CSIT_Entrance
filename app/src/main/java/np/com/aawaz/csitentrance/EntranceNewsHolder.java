package np.com.aawaz.csitentrance;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class EntranceNewsHolder extends AppCompatActivity {
    ImageView newsImage;
    TextView title;
    TextView content;
    TextView subTopic;
    TextView newsAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance_news_holder);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarEachNews));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newsImage = (ImageView) findViewById(R.id.newsImage);
        title = (TextView) findViewById(R.id.eachNewsTitle);
        content = (TextView) findViewById(R.id.newsContent);
        subTopic = (TextView) findViewById(R.id.newsSubTitle);
        newsAuthor = (TextView) findViewById(R.id.newsAuthor);

        if (!getIntent().getExtras().getString("imageURL").equals("null"))
            newsImage.setImageURI(Uri.parse(getIntent().getExtras().getString("imageURL")));
        title.setText(getIntent().getExtras().getString("title"));
        content.setText(getIntent().getExtras().getString("content"));
        subTopic.setText(getIntent().getExtras().getString("subTopic"));
        newsAuthor.setText(getIntent().getExtras().getString("author"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
}
