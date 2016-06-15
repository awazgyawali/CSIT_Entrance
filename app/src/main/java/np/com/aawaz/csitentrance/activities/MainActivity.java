package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.robototextview.widget.RobotoTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.CSITColleges;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceFAQs;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceForum;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceNews;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceResult;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.Home;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.LeaderBoard;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.More;
import np.com.aawaz.csitentrance.objects.Feedback;

public class MainActivity extends AppCompatActivity {

    public static TabLayout tabLayout;
    FragmentManager manager;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    public static CoordinatorLayout mainLayout;
    Toolbar toolbar;
    static RobotoTextView titleMain;
    AppBarLayout appBarLayout;
    RobotoTextView name;
    CircleImageView imageView;

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

        name = (RobotoTextView) mNavigationView.getHeaderView(0).findViewById(R.id.userName);
        RobotoTextView email = (RobotoTextView) mNavigationView.getHeaderView(0).findViewById(R.id.userEmail);
        imageView = (CircleImageView) mNavigationView.getHeaderView(0).findViewById(R.id.user_profile);
        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Picasso.with(this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .error(ContextCompat.getDrawable(this, R.drawable.account_holder))
                .into(imageView);
    }

    private void navigate(MenuItem item) {
        int id = item.getItemId();
        mDrawerLayout.closeDrawer(mNavigationView);
        invalidateOptionsMenu();
        if (id == R.id.settings) {
            startActivity(new Intent(MainActivity.this, Settings.class));
            return;
        } else if (id == R.id.rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=np.com.aawaz.csitentrance")));
            return;
        } else if (id == R.id.like) {
            startActivity(new Intent(Intent.ACTION_VIEW, newFacebookIntent()));
            return;
        } else if (id == R.id.feedback) {
            new MaterialDialog.Builder(this)
                    .title("Send Feedback")
                    .input("Feedback text...", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            Feedback feedback = new Feedback(input.toString());
                            FirebaseDatabase.getInstance().getReference().child("feedback").push().setValue(feedback);
                            Toast.makeText(MainActivity.this, "Thanks for your feedback.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .positiveText("Send")
                    .show();
            return;
        }
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

            case R.id.entranceFAQ:
                manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceFAQs()).commit();
                setTitle("Entrance FAQs");
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void setAppBarElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setElevation(elevation);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 202) {
            name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            Picasso.with(this)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .into(imageView);
        }
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

    public Uri newFacebookIntent() {
        String url = "https://www.facebook.com/CSITentrance/";
        Uri uri = null;
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            uri = Uri.parse(url);

        }
        return uri;
    }
}