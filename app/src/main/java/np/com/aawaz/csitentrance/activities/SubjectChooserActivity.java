package np.com.aawaz.csitentrance.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.SubjectListAdapter;
import np.com.aawaz.csitentrance.objects.SPHandler;
import np.com.aawaz.csitentrance.objects.YearItem;

public class SubjectChooserActivity extends AppCompatActivity {
    int position;
    String[] subjectCodes;
    ArrayList<YearItem> items = new ArrayList<>();
    RecyclerView recyclerView;


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

        recyclerView = findViewById(R.id.subjectListRecyclerView);

        prepareRecyclerView();

    }

    private void prepareRecyclerView() {
        addItem("College Model Questions", YearItem.Companion.getSECTION_TITLE(), -1);
        addItem("Samriddhi Model Question", YearItem.Companion.getYEAR_SET(), 12);
        addItem("Sagarmatha Model Question", YearItem.Companion.getYEAR_SET(), 16);
        addItem("CAB Model Question", YearItem.Companion.getYEAR_SET(), 20);

        addItem("Old Questions", YearItem.Companion.getSECTION_TITLE(), -1);
        addItem("2069 TU Examination", YearItem.Companion.getYEAR_SET(), 0);
        addItem("2070 TU Examination", YearItem.Companion.getYEAR_SET(), 1);
        addItem("2071 TU Examination", YearItem.Companion.getYEAR_SET(), 2);
        addItem("2072 TU Examination", YearItem.Companion.getYEAR_SET(), 3);
        addItem("2073 TU Examination", YearItem.Companion.getYEAR_SET(), 4);
        addItem("2074 TU Examination", YearItem.Companion.getYEAR_SET(), 5);
        addItem("2075 TU Examination", YearItem.Companion.getYEAR_SET(), 24);


        addItem("Model Questions", YearItem.Companion.getSECTION_TITLE(), -1);
        addItem("Model Paper 1", YearItem.Companion.getYEAR_SET(), 6);
        addItem("Model Paper 2", YearItem.Companion.getYEAR_SET(), 7);
        addItem("Model Paper 3", YearItem.Companion.getYEAR_SET(), 8);
        addItem("Model Paper 4", YearItem.Companion.getYEAR_SET(), 9);
        addItem("Model Paper 5", YearItem.Companion.getYEAR_SET(), 10);
        addItem("Model Paper 6", YearItem.Companion.getYEAR_SET(), 11);
        addItem("Model Paper 7", YearItem.Companion.getYEAR_SET(), 13);
        addItem("Model Paper 8", YearItem.Companion.getYEAR_SET(), 14);
        addItem("Model Paper 9", YearItem.Companion.getYEAR_SET(), 15);
        addItem("Model Paper 10", YearItem.Companion.getYEAR_SET(), 17);
        addItem("Model Paper 11", YearItem.Companion.getYEAR_SET(), 18);
        addItem("Model Paper 12", YearItem.Companion.getYEAR_SET(), 19);
        addItem("Model Paper 13", YearItem.Companion.getYEAR_SET(), 21);
        addItem("Model Paper 14", YearItem.Companion.getYEAR_SET(), 23);

        SubjectListAdapter adapter = new SubjectListAdapter(this, subjectCodes[position], items);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addItem(String title, int type, int paperCode) {
        YearItem item = new YearItem();
        item.setType(type);
        item.setTitle(title);
        item.setPaperCode(paperCode);

        items.add(item);
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
