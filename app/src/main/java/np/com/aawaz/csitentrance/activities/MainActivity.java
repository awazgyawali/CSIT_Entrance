package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.appcompat.BuildConfig;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.MainRecyclerAdapter;
import np.com.aawaz.csitentrance.advance.BackgroundTaskHandler;
import np.com.aawaz.csitentrance.advance.Singleton;


public class MainActivity extends AppCompatActivity implements MainRecyclerAdapter.ClickListner {
    RecyclerView recycler;
    SharedPreferences pref;
    TextView points;
    MainRecyclerAdapter adapter;
    Tracker tracker;
    GoogleAnalytics analytics;
    int clickedItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker("UA-63920529-5");
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);


        loadAd();

        constructJob();

        checkWhatsNew();


        int avatar[] = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five,
                R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine, R.drawable.ten,
                R.drawable.eleven, R.drawable.twelve};

        pref = getSharedPreferences("info", MODE_PRIVATE);
        TextView name = (TextView) findViewById(R.id.nameView);
        points = (TextView) findViewById(R.id.pointView);
        name.setText(pref.getString("Name", "") + " " + pref.getString("Surname", ""));
        points.setText(getTotal() + " pts");
        ImageView img = (ImageView) findViewById(R.id.profPic);
        img.setImageDrawable(getResources().getDrawable(avatar[(pref.getInt("Avatar", 1)) - 1]));
        View shadow = findViewById(R.id.shadow);
        shadow.bringToFront();


        int primaryColors[] = {R.color.primary1, R.color.primary2, R.color.primary3, R.color.primary4, R.color.primary5,
                R.color.primary6, R.color.primary7, R.color.primary8, R.color.primary9, R.color.primary10,
                R.color.primary11};
        int darkColors[] = {R.color.dark1, R.color.dark2, R.color.dark3, R.color.dark4, R.color.dark5,
                R.color.dark6, R.color.dark7, R.color.dark8, R.color.dark9, R.color.dark10,
                R.color.dark11};
        int icon[] = {R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_play_arrow_white_24dp,
                R.drawable.ic_play_arrow_white_24dp, R.drawable.ic_play_arrow_white_24dp, R.drawable.ic_play_arrow_white_24dp,
                R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp};
        recycler = (RecyclerView) findViewById(R.id.gridView);
        adapter = new MainRecyclerAdapter(this, primaryColors, darkColors, icon);
        adapter.setClickListner(this);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
        points.setText(getTotal() + " pts");


    }

    private void checkWhatsNew() {
        int current=BuildConfig.VERSION_CODE;
        int stored=getSharedPreferences("version",MODE_PRIVATE).getInt("version",1);
        if(stored< current){
            new MaterialDialog.Builder(this)
                    .title("What's new")
                    .items(R.array.whatsnew)
                    .itemsCallbackSingleChoice(1,null)
                    .positiveText("Got It!")
                    .build()
                    .show();

            getSharedPreferences("version",MODE_PRIVATE).edit().putInt("version",current).apply();
        }
    }

    private void constructJob() {
        GcmNetworkManager mScheduler= Singleton.getInstance().getGcmScheduler();

        long periodSecs = 30L;

        int taskID=100;

        String tag = "periodic  | " + taskID + ": " + periodSecs + "s";

        PeriodicTask periodic = new PeriodicTask.Builder()
                .setService(BackgroundTaskHandler.class)
                .setPeriod(periodSecs)
                .setTag(tag)
                .setPersisted(true)
                .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED)
                .build();
        mScheduler.schedule(periodic);
    }

    private int getTotal() {
        SharedPreferences pref = getSharedPreferences("values", MODE_PRIVATE);
        int grand = 0;
        for (int i = 1; i < 8; i++)
            grand = grand + pref.getInt("score" + i, 0);
        return grand;
    }

    @Override
    protected void onResume() {
        super.onResume();
        points.setText(getTotal() + " pts");
        if(adapter!=null && clickedItem!=0)
            adapter.notifyItemChanged(clickedItem);
    }

    @Override
    public void itemClicked(View view, int position) {
        clickedItem=position;
        if (position == 0) {
            Intent intent = new Intent(this, ScoreBoard.class);
            startActivity(intent);
        } else if (position > 0 && position < 5) {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        } else if (position == 5) {
            new MaterialDialog.Builder(this)
                    .title("More...")
                    .content("We are working on adding more questions. Please have patience...")
                    .positiveText("Ok")
                    .build()
                    .show();
        } else if (position == 6) {
            Intent intent = new Intent(this, FullQuestion.class);
            startActivity(intent);
        } else if (position == 7) {
            Intent intent = new Intent(this, Colleges.class);
            startActivity(intent);
        } else if (position == 8) {
            Intent intent = new Intent(this, EntranceNews.class);
            startActivity(intent);
        } else if (position == 9) {
            Intent intent = new Intent(this, CSITQuery.class);
            startActivity(intent);
        } else if (position == 10) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }
    }

    public void loadAd() {
        final AdView mAdView = (AdView) findViewById(R.id.MainAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    public boolean isLargeScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            return true;
        }
        return false;
    }
}
