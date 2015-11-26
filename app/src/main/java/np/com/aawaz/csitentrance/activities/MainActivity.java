package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.advance.BackgroundTaskHandler;
import np.com.aawaz.csitentrance.advance.Singleton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        Tracker tracker = analytics.newTracker("UA-63920529-5");
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);

        CardView layout = (CardView) findViewById(R.id.welcomeView);

        constructJob();

        YoYo.with(Techniques.SlideInUp)
                .duration(300)
                .playOn(layout);

        YoYo.with(Techniques.SlideOutDown)
                .duration(300)
                .delay(4300)
                .playOn(layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillSubtitles();
    }

    private void fillSubtitles() {
        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);

        TextView name = (TextView) findViewById(R.id.nameHolder);
        TextView points = (TextView) findViewById(R.id.scoreHolder);
        TextView lastPlay = (TextView) findViewById(R.id.lastPlayed);
        TextView lastView = (TextView) findViewById(R.id.lastViewed);
        TextView unreadNews = (TextView) findViewById(R.id.unreadNews);
        TextView resultPublished = (TextView) findViewById(R.id.resultStatus);

        name.setText("Welcome "+pref.getString("Name", ""));
        points.setText("Your score: "+getTotalScore());
        lastPlay.setText(pref.getString("LastPlayed",""));
        lastView.setText(pref.getString("LastViewed",""));
        unreadNews.setText(getUnreadNews());
        resultPublished.setText(pref.getBoolean("published",false)?"Published":"Not published");
    }

    private String getUnreadNews() {
        int unread = getSharedPreferences("values", Context.MODE_PRIVATE).getInt("newsNo", 0) - getSharedPreferences("values", Context.MODE_PRIVATE).getInt("readNewsNo", 0);
        if (unread==0)
            return "";
        else
            return unread+" unread news";
    }

    private int getTotalScore() {
        SharedPreferences pref = getSharedPreferences("values", MODE_PRIVATE);
        int grand = 0;
        for (int i = 1; i <= 8; i++)
            grand = grand + pref.getInt("score" + i, 0);
        return grand;
    }

    public void menuClickHandler(View view) {
        int id=view.getId();
        switch (id){
            case R.id.scoreBoard:
                if (!isConnected())
                    Snackbar.make(findViewById(R.id.mainParent), "No internet connection", Snackbar.LENGTH_SHORT).show();
                else
                    startActivity( new Intent(this, ScoreBoard.class));
                break;

            case  R.id.playQuiz:
                startActivity( new Intent(this, PlayQuiz.class));
                break;

            case  R.id.fullQuestion:
                startActivity( new Intent(this, FullQuestion.class));
                break;

            case  R.id.more:
                startActivity( new Intent(this, More.class));
                break;

            case  R.id.entranceNews:
                if (getSharedPreferences("values", Context.MODE_PRIVATE).getInt("newsNo", 0) == 0 && !isConnected())
                    Snackbar.make(findViewById(R.id.mainParent), "No internet connection", Snackbar.LENGTH_SHORT).show();
                else
                    startActivity( new Intent(this, EntranceNews.class));
                break;

            case  R.id.entranceForum:
                if (!isConnected())
                    Snackbar.make(findViewById(R.id.mainParent), "No internet connection", Snackbar.LENGTH_SHORT).show();
                else
                    startActivity( new Intent(this, EntranceForum.class));
                break;

            case  R.id.csitColleges:
                startActivity(new Intent(this, Colleges.class));
                break;

            case  R.id.entranceResult:
                if (!isConnected())
                    Snackbar.make(findViewById(R.id.mainParent), "No internet connection", Snackbar.LENGTH_SHORT).show();
                else
                    startActivity( new Intent(this, Result.class));
                break;

            case  R.id.settings:
                startActivity( new Intent(this, Settings.class));
                break;

            case  R.id.aboutUs:
                startActivity( new Intent(this, About.class));
                break;
        }

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void constructJob() {

        String tag = "periodic";

        GcmNetworkManager mScheduler = Singleton.getInstance().getGcmScheduler();

        long periodSecs = 600L;

        PeriodicTask periodic = new PeriodicTask.Builder()
                .setService(BackgroundTaskHandler.class)
                .setPeriod(periodSecs)
                .setTag(tag)
                .setFlex(periodSecs)
                .setPersisted(true)
                .setUpdateCurrent(true)
                .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_CONNECTED)
                .build();
        mScheduler.schedule(periodic);
    }
}
