package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.MainRecyclerAdapter;


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
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker("UA-63920529-5");
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);

        loadAd();

        int avatar[] = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five,
                R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine, R.drawable.ten,
                R.drawable.eleven, R.drawable.twelve};

        pref = getSharedPreferences("info", MODE_PRIVATE);
        TextView name = (TextView) findViewById(R.id.nameView);
        points = (TextView) findViewById(R.id.pointView);
        name.setText(pref.getString("Name", "") + " " + pref.getString("Surname", ""));
        points.setText(getTotalScore() + " pts");
        ImageView img = (ImageView) findViewById(R.id.profPic);
        img.setImageDrawable(getResources().getDrawable(avatar[(pref.getInt("Avatar", 1)) - 1]));
        points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getTotalPlayed() != 0)
                    new MaterialDialog.Builder(MainActivity.this)
                            .title("Reset progress")
                            .content("Are you sure you want to erase the game progress?")
                            .positiveText("Yes")
                            .negativeText("No")
                            .positiveColor(getResources().getColor(R.color.green))
                            .negativeColor(getResources().getColor(R.color.red))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    removeAllTheProgress();
                                    points.setText(getTotalScore() + " pts");
                                    Snackbar.make(findViewById(R.id.mainParent), "Progress reset successful", Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .build()
                            .show();
            }
        });


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
        points.setText(getTotalScore() + " pts");
    }

    private void removeAllTheProgress() {
        SharedPreferences.Editor pref = getSharedPreferences("values", MODE_PRIVATE).edit();
        for (int i = 1; i < 5; i++) {
            pref.putInt("score" + i, 0);
            pref.putInt("played" + i, 0);
        }
        pref.apply();
        adapter.notifyDataSetChanged();
    }

    private int getTotalScore() {

        SharedPreferences pref = getSharedPreferences("values", MODE_PRIVATE);
        int grand = 0;
        for (int i = 1; i < 8; i++)
            grand = grand + pref.getInt("score" + i, 0);
        return grand;

    }

    private int getTotalPlayed() {
        SharedPreferences pref = getSharedPreferences("values", MODE_PRIVATE);
        int grand = 0;
        for (int i = 1; i < 8; i++)
            grand = grand + pref.getInt("played" + i, 0);
        return grand;
    }

    @Override
    protected void onResume() {
        super.onResume();
        points.setText(getTotalScore() + " pts");
        if(adapter!=null && clickedItem!=0)
            adapter.notifyItemChanged(clickedItem);
    }

    @Override
    public void itemClicked(View view, int position) {
        clickedItem=position;
        YoYo.with(Techniques.Flash)
                .duration(500)
                .playOn(view);
        if (position == 0) {
            if (!isConnected()) {
                Snackbar.make(findViewById(R.id.mainParent), "No internet connection", Snackbar.LENGTH_SHORT).show();
                return;
            }
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
            if (getSharedPreferences("values", Context.MODE_PRIVATE).getInt("score" + position, 0) == 0 && !isConnected()) {
                Snackbar.make(findViewById(R.id.mainParent), "No internet connection", Snackbar.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, EntranceNews.class);
                startActivity(intent);
            }
        } else if (position == 9) {
            if (!isConnected()) {
                Snackbar.make(findViewById(R.id.mainParent), "No internet connection", Snackbar.LENGTH_SHORT).show();
                return;
            }
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
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
