package np.com.aawaz.csitentrance.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.custom_views.TouchImageView;
import np.com.aawaz.csitentrance.objects.EventSender;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_activity);
        new EventSender().logEvent("full_image");

        TouchImageView imageView = (TouchImageView) findViewById(R.id.image_full_size);


        Picasso.with(this)
                .load(getIntent().getStringExtra("image_link"))
                .into(imageView);
    }
}
