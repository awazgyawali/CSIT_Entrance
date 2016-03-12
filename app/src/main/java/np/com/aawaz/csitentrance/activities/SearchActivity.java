package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.CollegesAdapter;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<String> names = new ArrayList<>(),
            desc = new ArrayList<>(),
            website = new ArrayList<>(),
            address = new ArrayList<>(),
            phNo = new ArrayList<>();
    RecyclerView colzRecy;
    JSONObject coreObject;
    LinearLayout emptyLayout;
    AppCompatEditText search;


    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();
        return new String(formArray);
    }

    public void setDataToArrayList() {
        //Reading from json file and insillizing inside arrayList
        names.clear();
        desc.clear();
        website.clear();
        address.clear();
        phNo.clear();
        try {
            JSONArray m_jArry = coreObject.getJSONArray("lists");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                if (jo_inside.getString("name").toLowerCase().contains(search.getText().toString().toLowerCase()) ||
                        jo_inside.getString("address").toLowerCase().contains(search.getText().toString().toLowerCase()) ||
                        jo_inside.getString("desc").contains(search.getText().toString())) {
                    names.add(jo_inside.getString("name"));
                    desc.add(jo_inside.getString("desc"));
                    website.add(jo_inside.getString("website"));
                    address.add(jo_inside.getString("address"));
                    phNo.add(jo_inside.getString("phone"));
                }
            }
        } catch (Exception ignored) {
        }
    }

    public boolean isLargeScreen() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("");
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarSearch));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            coreObject = new JSONObject(AssetJSONFile("college_feed.json", this));
        } catch (Exception ignored) {
        }

        emptyLayout = (LinearLayout) findViewById(R.id.emptyLayout);
        search = (AppCompatEditText) findViewById(R.id.searchEditText);
        colzRecy = (RecyclerView) findViewById(R.id.searchRecycler);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    colzRecy.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                } else {
                    colzRecy.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                    setDataToArrayList();
                    fillNormally();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void fillNormally() {
        CollegesAdapter adapter = new CollegesAdapter(this, names, address, desc, website, phNo);
        colzRecy.setAdapter(adapter);
        colzRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
