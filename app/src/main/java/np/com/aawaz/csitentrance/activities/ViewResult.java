package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import np.com.aawaz.csitentrance.R;

public class ViewResult extends AppCompatActivity {

    TextView resultHolder;
    ImageView adview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarResulted));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        resultHolder= (TextView) findViewById(R.id.resultHolder);
        adview= (ImageView) findViewById(R.id.adViewResult);
        Picasso.with(this)
                .load("www.avaaj.com.np/ads/featured.jpg")
                .into(adview);
        resultHolder.setText(Html.fromHtml(getIntent().getExtras().getString("result")));
    }

    public void openQuery(View view){
        finish();
        startActivity(new Intent(this,EntranceForum.class));
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
