package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class SubjectChooserActivity extends AppCompatActivity {
    int position;
    String[] subjectCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_chooser);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarSubject));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        position = getIntent().getIntExtra("index", -1);

        String[] subjects = {"Physics", "Chemistry", "Math", "English"};
        subjectCodes = new String[]{SPHandler.PHYSICS, SPHandler.CHEMISTRY, SPHandler.MATH, SPHandler.ENGLISH};

        getSupportActionBar().setTitle(subjects[position]);

        int[] subColor = {R.color.physics, R.color.chemistry, R.color.math, R.color.english};
        int[] drawables = {R.drawable.physics, R.drawable.chemistry, R.drawable.math, R.drawable.english};

        ImageView image = (ImageView) findViewById(R.id.bannerImage);
        TextView title = (TextView) findViewById(R.id.bannerTitle);

        title.setText(subjects[position]);
        image.setImageDrawable(ContextCompat.getDrawable(this, drawables[position]));
        title.setTextColor(ContextCompat.getColor(this, subColor[position]));
        slideAnimator();

        handleViews();
    }

    private void handleViews() {
        LinearLayout que69, que70, que71, que72, que73, que74, que1, que2, que3, que4, que5, que6;

        que1 = (LinearLayout) findViewById(R.id.question1);
        que2 = (LinearLayout) findViewById(R.id.question2);
        que3 = (LinearLayout) findViewById(R.id.question3);
        que4 = (LinearLayout) findViewById(R.id.question4);
        que5 = (LinearLayout) findViewById(R.id.question5);
        que6 = (LinearLayout) findViewById(R.id.question6);
        que69 = (LinearLayout) findViewById(R.id.question2069);
        que70 = (LinearLayout) findViewById(R.id.question2070);
        que71 = (LinearLayout) findViewById(R.id.question2071);
        que72 = (LinearLayout) findViewById(R.id.question2072);
        que73 = (LinearLayout) findViewById(R.id.question2073);
        que74 = (LinearLayout) findViewById(R.id.question2074);

        que69.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(0);
            }
        });
        que70.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(1);
            }
        });
        que71.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(2);
            }
        });
        que72.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(3);
            }
        });
        que73.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(4);
            }
        });
        que74.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(5);
            }
        });
        que1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(6);
            }
        });
        que2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(7);
            }
        });
        que3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(8);
            }
        });
        que4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(9);
            }
        });
        que5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(10);
            }
        });
        que6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked(11);
            }
        });
    }

    private void clicked(int pos) {
        String[] codes = {SPHandler.YEAR2069, SPHandler.YEAR2070, SPHandler.YEAR2071, SPHandler.YEAR2072, SPHandler.YEAR2073, SPHandler.YEAR2074,
                SPHandler.MODEL1, SPHandler.MODEL2, SPHandler.MODEL3, SPHandler.MODEL4, SPHandler.MODEL5, SPHandler.MODEL6};
        Intent intent = new Intent(this, SubjectQuizActivity.class);
        intent.putExtra("code", codes[pos]);
        intent.putExtra("position", pos);
        intent.putExtra("subject", subjectCodes[position]);
        startActivity(intent);
        new EventSender().logEvent("played_subject");
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
