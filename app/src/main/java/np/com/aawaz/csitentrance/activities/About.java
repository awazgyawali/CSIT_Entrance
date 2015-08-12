package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.AboutAdapter;


public class About extends AppCompatActivity {

    RecyclerView recyAbout;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarAbout));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyAbout = (RecyclerView) findViewById(R.id.aboutRecy);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbarAbout);
        collapsingToolbarLayout.setTitle("About Us");
        final AboutAdapter adapter = new AboutAdapter(this);
        GridLayoutManager manager = new GridLayoutManager(this, 6);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0)
                    return 6;
                else if (position == adapter.getItemCount() - 1)
                    return 6;
                else
                    return isLargeScreen() ? 2 : 3;
            }
        });
        recyAbout.setLayoutManager(manager);
        recyAbout.setAdapter(adapter);
    }

    public void feedBack(View view) {
        Intent sendMail = new Intent(Intent.ACTION_SEND);
        sendMail.setData(Uri.parse("mailto:"));
        String[] to = {"contact@aawaz.com.np", "dhakalramu2070@gmail.com"};
        sendMail.putExtra(Intent.EXTRA_EMAIL, to);
        sendMail.putExtra(Intent.EXTRA_SUBJECT, "Regarding CSIT Entrance Android Application.");
        sendMail.setType("message/rfc822");
        Intent chooser = Intent.createChooser(sendMail, "Send E-mail");
        startActivity(chooser);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isLargeScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return false;
        else
            return (getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_NORMAL;
    }
}
