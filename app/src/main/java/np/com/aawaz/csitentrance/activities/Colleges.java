package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.colzAdapter;


public class Colleges extends AppCompatActivity {
    ArrayList<String> names = new ArrayList<>(),
            desc = new ArrayList<>(),
            website = new ArrayList<>(),
            address = new ArrayList<>(),
            phNo = new ArrayList<>();
    ArrayList<String> namesNew = new ArrayList<>(),
            descNew = new ArrayList<>(),
            websiteNew = new ArrayList<>(),
            addressNew = new ArrayList<>(),
            phNoNew = new ArrayList<>();
    EditText search;

    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();
        return new String(formArray);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colleges);

        loadAd();

        //Toolbar Stuff's
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarColz);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        search= (EditText) findViewById(R.id.search);
        //Fetch from JSONFILE
        setDataToArrayList();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0)
                    fillNormally();
                else
                    checkOccurrence();
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        fillNormally();
    }

    private void fillNormally(){
        //Recycler view handler
        RecyclerView colzRecy = (RecyclerView) findViewById(R.id.colzRecy);
        colzAdapter adapter = new colzAdapter(this, names, address, desc, website, phNo);
        colzRecy.setAdapter(adapter);
        colzRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
    }

    private void checkOccurrence() {
        namesNew.clear();
        descNew.clear();
        websiteNew.clear();
        addressNew.clear();
        phNoNew.clear();
        for(int i=0;i<names.size();i++){
            if(names.get(i).toLowerCase().contains(search.getText().toString().toLowerCase()) ||
                    address.get(i).toLowerCase().contains(search.getText().toString().toLowerCase()) ||
                    desc.get(i).contains(search.getText().toString())){
                namesNew.add(names.get(i));
                descNew.add(desc.get(i));
                websiteNew.add(website.get(i));
                addressNew.add(address.get(i));
                phNoNew.add(phNo.get(i));
            }
        }
        fillNewRecy();
    }

    private void fillNewRecy() {
        //Recycler view handler
        RecyclerView colzRecy = (RecyclerView) findViewById(R.id.colzRecy);
        colzAdapter adapter = new colzAdapter(this, namesNew, addressNew, descNew, websiteNew, phNoNew);
        colzRecy.setAdapter(adapter);
        ;
        colzRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
    }

    public void setDataToArrayList() {
        //Reading from json file and insillizing inside arrayList
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("college_feed.json", this));
            JSONArray m_jArry = obj.getJSONArray("lists");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                names.add(jo_inside.getString("name"));
                desc.add(jo_inside.getString("desc"));
                website.add(jo_inside.getString("website"));
                address.add(jo_inside.getString("address"));
                phNo.add(jo_inside.getString("phone"));
            }

        } catch (Exception e) {
        }
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

    public void loadAd() {
        final AdView mAdView = (AdView) findViewById(R.id.colzAd);
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
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return false;
        else
            return (getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_NORMAL;
    }
}






