package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.AllColleges;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceFAQs;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceForum;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceNews;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.EntranceResult;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.Home;
import np.com.aawaz.csitentrance.fragments.navigation_fragment.LeaderBoard;
import np.com.aawaz.csitentrance.misc.MyApplication;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.Feedback;
import np.com.aawaz.csitentrance.objects.SPHandler;
import np.com.aawaz.csitentrance.services.ScoreUploader;

public class MainActivity extends AppCompatActivity {

    public static boolean openedIntent = false;
    Intent intent;

    public TabLayout tabLayout;
    private static TextView titleMain;
    FragmentManager manager;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    TextView name;
    CircleImageView imageView;
    String[] navigationText = new String[]{"leaderboard", "colleges",
            "faqs", "news", "forum", "result",
            "setting", "feedback", "share", "like", "rate"};
    int[] navigationId = new int[]{R.id.leaderBoard, R.id.csitColleges, R.id.entranceFAQ, R.id.entranceNews, R.id.entranceForum, R.id.entranceResult, R.id.settings, R.id.feedback, R.id.share, R.id.like, R.id.rate};

    public static void setTitle(String name) {
        titleMain.setText(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        uploadInstanceId();

        intent = getIntent();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerMain);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        manager = getSupportFragmentManager();
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutMain);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarMain);
        titleMain = (TextView) findViewById(R.id.titleMain);


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

        handlingIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.intent = intent;
        handlingIntent(intent);
    }

    private void handlingIntent(Intent intent) {
        if (intent.getStringExtra("fragment") != null) {
            String string = intent.getStringExtra("fragment");
            for (int i = 0; i < navigationText.length; i++)
                if (navigationText[i].equals(string)) {
                    openedIntent = false;
                    navigate(mNavigationView.getMenu().findItem(navigationId[i]));
                }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadScore();
        FirebaseDatabase.getInstance().getReference().child("ads").keepSynced(true);
    }

    private void uploadScore() {
        if (SPHandler.getInstance().isScoreChanged())
            startService(new Intent(MyApplication.getAppContext(), ScoreUploader.class));
    }

    private void uploadInstanceId() {
        String token = FirebaseInstanceId.getInstance().getToken();
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !SPHandler.getInstance().isInstanceIdAdded()) {
            FirebaseDatabase.getInstance().getReference()
                    .child("instance_ids")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);
            SPHandler.getInstance().instanceIdAdded();
            FirebaseMessaging.getInstance().subscribeToTopic("allDevices");
            FirebaseMessaging.getInstance().subscribeToTopic("new");
            FirebaseMessaging.getInstance().subscribeToTopic("forums");
        }
    }

    private void manageHeader() {

        name = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.userName);
        TextView email = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.userEmail);
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
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            new EventSender().logEvent("settings");
            return;
        } else if (id == R.id.share) {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");

            share.putExtra(Intent.EXTRA_SUBJECT, "CSIT Entrance");
            share.putExtra(Intent.EXTRA_TEXT, "Single app for all BSc CSIT Entrance preparing students.\nhttps://b5b88.app.goo.gl/jdF1");

            startActivity(Intent.createChooser(share, "Share CSIT Entrance"));
            new EventSender().logEvent("shared_app");
            return;
        } else if (id == R.id.rate) {
            new EventSender().logEvent("rated_app");
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=np.com.aawaz.csitentrance")));
            } catch (Exception e) {
                Toast.makeText(this, "No play store app found.", Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (id == R.id.like) {
            startActivity(newFacebookIntent());
            new EventSender().logEvent("liked_page");
            return;
        } else if (id == R.id.feedback) {
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("Send Feedback")
                    .input("Feedback text...", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            Feedback feedback = new Feedback(input.toString());
                            FirebaseDatabase.getInstance().getReference().child("feedback").push().setValue(feedback);
                            new EventSender().logEvent("sent_feedback");
                            Toast.makeText(MainActivity.this, "Thanks for your feedback.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .positiveText("Send")
                    .build();
            dialog.getInputEditText().setLines(5);
            dialog.getInputEditText().setSingleLine(false);
            dialog.getInputEditText().setMaxLines(7);
            dialog.show();
            return;
        } else if (id == R.id.logout) {
            SPHandler.getInstance().clearAll();
            new MaterialDialog.Builder(this)
                    .title("Log out")
                    .content("Are you sure you want to log out?")
                    .positiveText("Log Out")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            FirebaseAuth.getInstance().signOut();
                            if (AccessToken.getCurrentAccessToken() != null)
                                LoginManager.getInstance().logOut();
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("allDevices");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("new");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("forums");
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                            finish();
                        }
                    })
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
                new EventSender().logEvent("leaderboard");
                break;

            case R.id.entranceFAQ:
                manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceFAQs()).commit();
                setTitle("Entrance FAQs");
                new EventSender().logEvent("faqs");
                item.setChecked(true);
                break;

            case R.id.entranceNews:
                manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceNews()).commit();
                setTitle("Entrance News");
                new EventSender().logEvent("news");
                item.setChecked(true);
                break;

            case R.id.entranceForum:
                manager.beginTransaction().replace(R.id.fragmentHolder, EntranceForum.newInstance(intent.getStringExtra("post_id"))).commit();
                setTitle("Entrance Forum");
                new EventSender().logEvent("forum");
                item.setChecked(true);
                break;

            case R.id.csitColleges:
                manager.beginTransaction().replace(R.id.fragmentHolder, new AllColleges()).commit();
                setTitle("CSIT Colleges");
                new EventSender().logEvent("colleges");
                item.setChecked(true);
                break;

            case R.id.entranceResult:
                manager.beginTransaction().replace(R.id.fragmentHolder, new EntranceResult()).commit();
                setTitle("Entrance Result");
                setAppBarElevation(0);
                new EventSender().logEvent("result");
                item.setChecked(true);
                break;
        }
    }

    private Intent newFacebookIntent() {
        Uri uri = Uri.parse("https://m.facebook.com/CSITEntrance");
        return new Intent(Intent.ACTION_VIEW, uri);
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
        } else if (item.getItemId() == R.id.notifications) {
            startActivity(new Intent(this, NotificationActivity.class));
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
        else if (getToolbarTitle().equals("Full Question") || getToolbarTitle().equals("Scoreboard"))
            Home.viewPager.setCurrentItem(0, true);
        else if (!getToolbarTitle().equals("Play Quiz"))
            navigate(mNavigationView.getMenu().findItem(R.id.main_home));
        else
            super.onBackPressed();
    }

    public String getToolbarTitle() {
        return titleMain.getText().toString();
    }
}