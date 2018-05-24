package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import np.com.aawaz.csitentrance.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        setSupportActionBar((Toolbar) findViewById(R.id.aboutUsToolbar));
        setTitle("");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        findViewById(R.id.about_fb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntentToAddress("https://www.facebook.com/CSITentrance/");
            }
        });

        findViewById(R.id.about_twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntentToAddress("https://twitter.com/CsitEntrance");

            }
        });

        findViewById(R.id.about_github).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntentToAddress("https://github.com/awazgyawali/CSIT_Entrance");
            }
        });
    }

    private void sendIntentToAddress(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
