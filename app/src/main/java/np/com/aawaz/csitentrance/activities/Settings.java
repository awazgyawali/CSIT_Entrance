package np.com.aawaz.csitentrance.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import np.com.aawaz.csitentrance.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarSetting));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");


    }
    private void removeAllTheProgress() {
        SharedPreferences.Editor pref = getSharedPreferences("values", MODE_PRIVATE).edit();
        for (int i = 1; i <= 8; i++) {
            pref.putInt("score" + i, 0);
            pref.putInt("played" + i, 0);
        }
        pref.apply();
    }
    private int getTotalPlayed() {
        SharedPreferences pref = getSharedPreferences("values", MODE_PRIVATE);
        int grand = 0;
        for (int i = 1; i <= 8; i++)
            grand = grand + pref.getInt("played" + i, 0);
        return grand;
    }

}
