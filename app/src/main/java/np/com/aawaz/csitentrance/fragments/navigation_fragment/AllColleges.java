package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.SearchActivity;
import np.com.aawaz.csitentrance.adapters.CollegesAdapter;
import np.com.aawaz.csitentrance.interfaces.CollegeMenuClicks;


public class AllColleges extends Fragment {
    RecyclerView colzRecy;
    private ArrayList<String> names = new ArrayList<>(),
            website = new ArrayList<>(),
            address = new ArrayList<>(),
            phNo = new ArrayList<>();
    private SliderLayout featured_slider;
    private FloatingActionButton fab;

    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();
        return new String(formArray);
    }

    public static AllColleges newInstance() {
        return new AllColleges();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        colzRecy = view.findViewById(R.id.colzRecy);
        featured_slider = view.findViewById(R.id.featured_ad_slider);
        colzRecy.setNestedScrollingEnabled(false);
        fab = view.findViewById(R.id.searchCollege);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDataToArrayList();
        setupFeaturedCollege();
    }

    private void setupFeaturedCollege() {


        featured_slider.addSlider(getSliderViewWithImage(R.drawable.samriddhi_full));
        featured_slider.addSlider(getSliderViewWithImage(R.drawable.sagarmatha));
        featured_slider.addSlider(getSliderViewWithImage(R.drawable.achs_full_image));
    }

    private BaseSliderView getSliderViewWithImage(final int image_src) {
        return new BaseSliderView(getContext()) {
            @Override
            public View getView() {
                ImageView v = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.featured_slider, null);
                v.setImageResource(image_src);
                return v;
            }
        };
    }


    private void fillNormally() {
        //Recycler view handler
        CollegesAdapter adapter = new CollegesAdapter(getContext(), names, address);
        adapter.setMenuClickListener(new CollegeMenuClicks() {
            @Override
            public void onCallClicked(final int position) {
                if (phNo.get(position).equals("null"))
                    Toast.makeText(getContext(), "No phone number found.", Toast.LENGTH_SHORT).show();
                else
                    new MaterialDialog.Builder(getContext())
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
                    Toast.makeText(getContext(), "No web address found.", Toast.LENGTH_SHORT).show();
                else
                    new MaterialDialog.Builder(getContext())
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
                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(getContext(), "Google maps doesn't exist..", Toast.LENGTH_SHORT).show();
                }
            }
        });
        colzRecy.setAdapter(adapter);
        colzRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
    }

    public void setDataToArrayList() {
        //Reading from json file and insillizing inside arrayList
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONObject obj = new JSONObject(AssetJSONFile("college_feed.json", getContext()));
                    JSONArray m_jArry = obj.getJSONArray("lists");
                    for (int i = 0; i < m_jArry.length(); i++) {
                        JSONObject jo_inside = m_jArry.getJSONObject(i);
                        names.add(jo_inside.getString("name"));
                        website.add(jo_inside.getString("website"));
                        address.add(jo_inside.getString("address"));
                        phNo.add(jo_inside.getString("phone"));
                    }
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                fillNormally();
            }
        }.execute();

    }

    public boolean isLargeScreen() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_colleges, container, false);
    }
}