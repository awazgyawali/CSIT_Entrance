package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.AboutAdapter;


public class About extends AppCompatActivity {

    RecyclerView recyAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarAbout));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyAbout = (RecyclerView) findViewById(R.id.aboutRecy);
        AboutAdapter adapter = new AboutAdapter(this);
        recyAbout.setAdapter(adapter);
        recyAbout.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 3 : 2, StaggeredGridLayoutManager.VERTICAL));
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
