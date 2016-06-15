package np.com.aawaz.csitentrance.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.messaging.FirebaseMessaging;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class Settings extends AppCompatActivity {

    boolean newsSub,
            forumSub;
    SwitchCompat news, forum;
    TextView clearAll, connectFb, connectGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarSetting));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        news = (SwitchCompat) findViewById(R.id.notifNews);
        forum = (SwitchCompat) findViewById(R.id.notifForum);

        clearAll = (TextView) findViewById(R.id.clearAll);
        connectFb = (TextView) findViewById(R.id.connectFb);
        connectGoogle = (TextView) findViewById(R.id.connectGoogle);

        news.setChecked(SPHandler.getInstance().getNewsSubscribed());
        forum.setChecked(SPHandler.getInstance().getForumSubscribed());

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsSub)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
                else
                    FirebaseMessaging.getInstance().subscribeToTopic("news");
                newsSub = !newsSub;
                SPHandler.getInstance().setNewsSubscribed(newsSub);
            }
        });

        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forumSub)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("forum");
                else
                    FirebaseMessaging.getInstance().subscribeToTopic("forum");
                forumSub = !forumSub;
                SPHandler.getInstance().setNewsSubscribed(forumSub);
            }
        });
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(Settings.this)
                        .title("Confirmation")
                        .content("This will delete your progress for all years quiz and even from the leader board.")
                        .positiveText("Clear all")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                removeAllTheProgress();
                            }
                        })
                        .show();

            }
        });
    }

    private void removeAllTheProgress() {
        SPHandler handler = SPHandler.getInstance();

        handler.setScore(SPHandler.YEAR2069, 0);
        handler.setScore(SPHandler.YEAR2070, 0);
        handler.setScore(SPHandler.YEAR2071, 0);
        handler.setScore(SPHandler.YEAR2072, 0);
        handler.setScore(SPHandler.MODEL1, 0);
        handler.setScore(SPHandler.MODEL2, 0);
        handler.setScore(SPHandler.MODEL3, 0);
        handler.setScore(SPHandler.MODEL4, 0);

        handler.setPlayed(SPHandler.YEAR2069, 0);
        handler.setPlayed(SPHandler.YEAR2070, 0);
        handler.setPlayed(SPHandler.YEAR2071, 0);
        handler.setPlayed(SPHandler.YEAR2072, 0);
        handler.setPlayed(SPHandler.MODEL1, 0);
        handler.setPlayed(SPHandler.MODEL2, 0);
        handler.setPlayed(SPHandler.MODEL3, 0);
        handler.setPlayed(SPHandler.MODEL4, 0);

        Toast.makeText(this, "Cleared all progress!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
