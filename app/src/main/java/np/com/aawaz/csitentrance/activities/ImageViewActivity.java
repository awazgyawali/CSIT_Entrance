package np.com.aawaz.csitentrance.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.custom_views.TouchImageView;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        TouchImageView imageView = (TouchImageView) findViewById(R.id.imageViewerImage);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarImageView));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Entrance News");

        Picasso.with(this).load(getIntent().getStringExtra("image_link")).into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}
