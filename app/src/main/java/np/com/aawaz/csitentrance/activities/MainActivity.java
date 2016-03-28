package np.com.aawaz.csitentrance.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.devspark.robototextview.widget.RobotoTextView;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.CSITColleges;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceForum;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceNews;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceResult;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.Home;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.LeaderBoard;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.More;

public class MainActivity extends AppCompatActivity {

    public static TabLayout tabLayout;
    FragmentManager manager;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    public static CoordinatorLayout mainLayout;
    Toolbar toolbar;
    static RobotoTextView titleMain;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerMain);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        manager = getSupportFragmentManager();
        mainLayout = (CoordinatorLayout) findViewById(R.id.mainParent);
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutMain);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarMain);
        titleMain = (RobotoTextView) findViewById(R.id.titleMain);

        setTitle("Play Quiz");
        manager.beginTransaction().replace(R.id.fragmentHolder, new Home()).commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                navigate(item);
                return true;
            }
        });

        manageHeader();

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
    }

    private void manageHeader() {
        RobotoTextView name = (RobotoTextView) mNavigationView.getHeaderView(0).findViewById(R.id.userName);
        RobotoTextView email = (RobotoTextView) mNavigationView.getHeaderView(0).findViewById(R.id.userEmail);
        CircleImageView imageView = (CircleImageView) mNavigationView.getHeaderView(0).findViewById(R.id.user_profile);
        SharedPreferences pref = getSharedPreferences("info", Context.MODE_PRIVATE);
        name.setText(pref.getString("Name", "") + " " + pref.getString("Surname", ""));
        email.setText(pref.getString("E-mail", ""));
        imageView.setImageURI(Uri.parse(pref.getString("ImageLink", "")));
    }

    private void navigate(MenuItem item) {
        invalidateOptionsMenu();
        int id = item.getItemId();
        tabLayout.setVisibility(View.GONE);
        setAppBarElevation(getResources().getDimension(R.dimen.app_bar_elevation));
        switch (id) {
            case R.id.main_home:
                manager.beginTransaction().replace(R.id.fragmentHolder, new Home()).commit();
                setTitle("Play Quiz");
                tabLayout.setVisibility(View.VISIBLE);
                item.setChecked(true);
                break;

            case R.id.leaderBoard:
                manager.beginTransaction().replace(R.id.fragmentHolder, new LeaderBoard()).commit();
                setTitle("Leaderboard");
                item.setChecked(true);
                break;

            case R.id.more:
                manager.beginTransaction().replace(R.id.fragmentHolder, new More()).commit();
                setTitle("More");
                item.setChecked(true);
                break;

            case R.id.entranceNews:
                manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceNews()).commit();
                setTitle("Entrance News");
                item.setChecked(true);
                break;

            case R.id.entranceForum:
                manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceForum()).commit();
                setTitle("Entrance Forum");
                setAppBarElevation(0);
                item.setChecked(true);
                break;

            case R.id.csitColleges:
                manager.beginTransaction().replace(R.id.fragmentHolder, new CSITColleges()).commit();
                setTitle("CSIT Colleges");
                tabLayout.setVisibility(View.VISIBLE);
                item.setChecked(true);
                break;

            case R.id.entranceResult:
                manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceResult()).commit();
                setTitle("Entrance Result");
                setAppBarElevation(0);
                item.setChecked(true);
                break;

            case R.id.settings:
                startActivity(new Intent(MainActivity.this, Settings.class));
                break;

            case R.id.aboutUs:
                startActivity(new Intent(MainActivity.this, About.class));
                break;
        }
        mDrawerLayout.closeDrawer(mNavigationView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAppBarElevation(float elevation) {
        appBarLayout.setElevation(elevation);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getToolbarTitle().equals("CSIT Colleges"))
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

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView))
            mDrawerLayout.closeDrawer(mNavigationView);
        else if (!getToolbarTitle().equals("Play Quiz"))
            navigate(mNavigationView.getMenu().findItem(R.id.main_home));
        else
            super.onBackPressed();
    }

    public static void setTitle(String name) {
        titleMain.setText(name);
    }

    public String getToolbarTitle() {
        return titleMain.getText().toString();
    }
}