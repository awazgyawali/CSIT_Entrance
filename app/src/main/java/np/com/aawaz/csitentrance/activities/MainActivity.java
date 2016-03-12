package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.misc.BackgroundTaskHandler;
import np.com.aawaz.csitentrance.misc.Singleton;
import np.com.aawaz.csitentrance.fragments.Colleges;
import np.com.aawaz.csitentrance.fragments.EntranceForum;
import np.com.aawaz.csitentrance.fragments.EntranceNews;
import np.com.aawaz.csitentrance.fragments.Home;
import np.com.aawaz.csitentrance.fragments.Result;
import np.com.aawaz.csitentrance.fragments.ScoreBoard;

public class MainActivity extends AppCompatActivity {

    FragmentManager manager;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        setTitle("Home");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerMain);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        manager = getSupportFragmentManager();

        manager.beginTransaction().replace(R.id.fragmentHolder, new Home()).commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                invalidateOptionsMenu();
                int id = item.getItemId();
                switch (id) {
                    case R.id.main_home:
                        manager.beginTransaction().replace(R.id.fragmentHolder, new Home()).commit();
                        setTitle("Home");
                        item.setEnabled(true);
                        break;

                    case R.id.scoreBoard:
                        manager.beginTransaction().replace(R.id.fragmentHolder, new ScoreBoard()).commit();
                        setTitle("Scoreboard");
                        item.setEnabled(true);
                        break;

                    case R.id.more:
                        manager.beginTransaction().replace(R.id.fragmentHolder, new More()).commit();
                        setTitle("More");
                        item.setEnabled(true);
                        break;

                    case R.id.entranceNews:
                        manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceNews()).commit();
                        setTitle("Entrance News");
                        item.setEnabled(true);
                        break;

                    case R.id.entranceForum:
                        manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceForum()).commit();
                        setTitle("Entrance Forum");
                        item.setEnabled(true);
                        break;

                    case R.id.csitColleges:
                        manager.beginTransaction().replace(R.id.fragmentHolder, new Colleges()).commit();
                        setTitle("CSIT Colleges");
                        item.setEnabled(true);
                        break;

                    case R.id.entranceResult:
                        manager.beginTransaction().replace(R.id.fragmentHolder, new Result()).commit();
                        setTitle("Entrance Result");
                        item.setEnabled(true);
                        break;

                    case R.id.settings:
                        startActivity(new Intent(MainActivity.this, Settings.class));
                        break;

                    case R.id.aboutUs:
                        startActivity(new Intent(MainActivity.this, About.class));
                        break;
                }
                mDrawerLayout.closeDrawer(mNavigationView);
                return true;
            }
        });

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.addDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();


        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        Tracker tracker = analytics.newTracker("UA-63920529-5");
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);

        constructJob();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getTitle().equals("CSIT Colleges"))
            menu.findItem(R.id.search).setVisible(true);
        else
            menu.findItem(R.id.search).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView))
            mDrawerLayout.closeDrawer(mNavigationView);
        else
            super.onBackPressed();
    }
}