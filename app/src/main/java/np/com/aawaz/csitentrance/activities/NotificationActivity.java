package np.com.aawaz.csitentrance.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.NotificationAdapter;
import np.com.aawaz.csitentrance.objects.Notification;
import np.com.aawaz.csitentrance.services.NotificationDatabase;

public class NotificationActivity extends AppCompatActivity {

    private ArrayList<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notificationRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NotificationAdapter(this, getNotifications()));
    }

    public ArrayList<Notification> getNotifications() {
        notifications = new ArrayList<>();

        SQLiteDatabase database = new NotificationDatabase(this).getReadableDatabase();

        Cursor cursor = database.query("notification", new String[]{"title", "text", "time", "post_id", "tag"}, null, null, null, null, "time DESC");

        while (cursor.moveToNext()) {
            Notification notification = new Notification();
            notification.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            notification.setText(cursor.getString(cursor.getColumnIndex("text")));
            notification.setPost_id(cursor.getString(cursor.getColumnIndex("post_id")));
            notification.setTag(cursor.getString(cursor.getColumnIndex("tag")));
            notification.setTime(cursor.getLong(cursor.getColumnIndex("time")));

            notifications.add(notification);
        }
        cursor.close();
        return notifications;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
