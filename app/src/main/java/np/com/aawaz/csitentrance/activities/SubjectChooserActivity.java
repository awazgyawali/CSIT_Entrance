package np.com.aawaz.csitentrance.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import np.com.aawaz.csitentrance.R;

public class SubjectChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_chooser);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarSubject));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int position = getIntent().getIntExtra("index", -1);

        String[] subjects = {"Physics", "Chemistry", "Math", "English"};

        getSupportActionBar().setTitle(subjects[position]);

        int[] subColor = {R.color.physics, R.color.chemistry, R.color.math, R.color.english};
        int[] drawables = {R.drawable.physics, R.drawable.chemistry, R.drawable.math, R.drawable.english};

        ImageView image = (ImageView) findViewById(R.id.bannerImage);
        TextView title = (TextView) findViewById(R.id.bannerTitle);

        title.setText(subjects[position]);
        image.setImageDrawable(ContextCompat.getDrawable(this, drawables[position]));
        title.setTextColor(ContextCompat.getColor(this, subColor[position]));
        slideAnimator();
    }

    private void slideAnimator() {
        if (Build.VERSION.SDK_INT >= 21) {
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in));
            getWindow().setEnterTransition(slide);
            getWindow().setSharedElementExitTransition(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
