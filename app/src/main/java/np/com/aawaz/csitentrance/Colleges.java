package np.com.aawaz.csitentrance;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class Colleges extends AppCompatActivity {
    ArrayList<String> names = new ArrayList<>(),
            desc = new ArrayList<>(),
            website = new ArrayList<>(),
            address = new ArrayList<>();
    ArrayList<String> phNo = new ArrayList<>();

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

        //Fetch from JSONFILE
        setDataToArrayList();

        //Recycler view handler
        RecyclerView colzRecy = (RecyclerView) findViewById(R.id.colzRecy);
        colzAdapter adapter = new colzAdapter(this, names, address, desc, website, phNo);
        colzRecy.setAdapter(adapter);
        colzRecy.setLayoutManager(new GridLayoutManager(this, 1));
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
            e.printStackTrace();
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
        AdView mAdView = (AdView) findViewById(R.id.colzAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
}






