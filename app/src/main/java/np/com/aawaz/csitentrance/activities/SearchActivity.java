package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.CollegesAdapter;
import np.com.aawaz.csitentrance.interfaces.CollegeMenuClicks;
import np.com.aawaz.csitentrance.objects.EventSender;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<String> names = new ArrayList<>(),
            website = new ArrayList<>(),
            address = new ArrayList<>(),
            phNo = new ArrayList<>();
    RecyclerView colzRecy;
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
        website.clear();
        address.clear();
        phNo.clear();
        try {
            JSONArray m_jArry = new JSONObject(AssetJSONFile("college_feed.json", this)).getJSONArray("lists");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                if (jo_inside.getString("name").toLowerCase().contains(search.getText().toString().toLowerCase()) ||
                        jo_inside.getString("address").toLowerCase().contains(search.getText().toString().toLowerCase())) {
                    names.add(jo_inside.getString("name"));
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
        new EventSender().logEvent("searched_college");

        setTitle("");
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarSearch));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        emptyLayout = (LinearLayout) findViewById(R.id.emptyLayout);
        search = (AppCompatEditText) findViewById(R.id.searchEditText);
        colzRecy = (RecyclerView) findViewById(R.id.searchRecycler);

        imm.showSoftInput(search,InputMethodManager.HIDE_IMPLICIT_ONLY);

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
        CollegesAdapter adapter = new CollegesAdapter(this, names, address);
        colzRecy.setAdapter(adapter);
        adapter.setMenuClickListener(new CollegeMenuClicks() {
            @Override
            public void onCallClicked(final int position) {
                if (phNo.get(position).equals("null"))
                    Snackbar.make(MainActivity.mainLayout, "No phone number found.", Snackbar.LENGTH_SHORT).show();
                else
                    new MaterialDialog.Builder(SearchActivity.this)
                            .title("Call Confirmation")
                            .content("Call " + phNo.get(position) + "?")
                            .positiveText("Call")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phNo.get(position), null)));
                                }
                            })
                            .show();
            }

            @Override
            public void onWebsiteClicked(final int position) {
                if (website.get(position).equals("null"))
                    Snackbar.make(MainActivity.mainLayout, "No web address found.", Snackbar.LENGTH_SHORT).show();
                else
                    new MaterialDialog.Builder(SearchActivity.this)
                            .title("Confirmation.")
                            .content("Open " + website.get(position) + "?")
                            .positiveText("Open")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website.get(position))));
                                }
                            })
                            .show();
            }

            @Override
            public void onMapClicked(int position) {

                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + names.get(position));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Snackbar.make(MainActivity.mainLayout, "Google maps doesn't exist..", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        colzRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
