package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.QueAdater;


public class FullQuestion extends AppCompatActivity implements QueAdater.ClickListner {

    RecyclerView listRecy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_question);
        setSupportActionBar((Toolbar) findViewById(R.id.fullQueToolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadAd();

        int primaryColors[] = {R.color.primary2, R.color.primary4, R.color.primary5,
                R.color.primary6, R.color.primary7, R.color.primary8, R.color.primary9, R.color.primary10};
        int darkColors[] = {R.color.dark2, R.color.dark4, R.color.dark5,
                R.color.dark6, R.color.dark7, R.color.dark8, R.color.dark9, R.color.dark10};
        int icon[] = {R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp,
                R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp, R.drawable.ic_arrow_forward_white_24dp};

        //Recyc
        QueAdater adater = new QueAdater(this, primaryColors, darkColors, icon);
        listRecy = (RecyclerView) findViewById(R.id.onlineRecyc);
        listRecy.setAdapter(adater);
        listRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
        adater.setClickListner(this);
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

    @Override
    public void itemClicked(View view, int position) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("code", position + 1);
        startActivity(intent);
    }

    public void loadAd() {
        final AdView mAdView = (AdView) findViewById(R.id.fullQueAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    public boolean isLargeScreen() {
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return true;
        }
        return false;
    }
}
