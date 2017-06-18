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
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;
import np.com.aawaz.csitentrance.adapters.CollegesAdapter;
import np.com.aawaz.csitentrance.custom_views.FeaturedSlideView;
import np.com.aawaz.csitentrance.interfaces.CollegeMenuClicks;
import np.com.aawaz.csitentrance.objects.FeaturedCollege;


public class AllColleges extends Fragment implements ValueEventListener {
    RecyclerView colzRecy;
    SliderLayout adSlider;
    private ArrayList<String> names = new ArrayList<>(),
            website = new ArrayList<>(),
            address = new ArrayList<>(),
            phNo = new ArrayList<>();

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
        colzRecy = (RecyclerView) view.findViewById(R.id.colzRecy);
        adSlider = (SliderLayout) view.findViewById(R.id.featured_ad_slider);
        colzRecy.setNestedScrollingEnabled(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDataToArrayList();
    }

    @Override
    public void onStart() {
        super.onStart();
        // addFeaturedListener(); todo in next update
    }

    private void addFeaturedListener() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ads").child("featured_colleges");
        reference.addListenerForSingleValueEvent(this);
    }


    private void fillNormally() {
        //Recycler view handler
        CollegesAdapter adapter = new CollegesAdapter(getContext(), names, address);
        adapter.setMenuClickListener(new CollegeMenuClicks() {
            @Override
            public void onCallClicked(final int position) {
                if (phNo.get(position).equals("null"))
                    Snackbar.make(MainActivity.mainLayout, "No phone number found.", Snackbar.LENGTH_SHORT).show();
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
                    Snackbar.make(MainActivity.mainLayout, "No web address found.", Snackbar.LENGTH_SHORT).show();
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
                    Snackbar.make(MainActivity.mainLayout, "Google maps doesn't exist..", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        colzRecy.setAdapter(adapter);
        colzRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
    }

    public void setDataToArrayList() {
        //Reading from json file and insillizing inside arrayList
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
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
        });

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

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot child : dataSnapshot.getChildren())
            fillFeaturedColleges(child.getValue(FeaturedCollege.class));
    }

    private void fillFeaturedColleges(FeaturedCollege college) {
        Bundle bundle = new Bundle();
        bundle.putString("address", college.address);
        bundle.putLong("phone_no", college.phone);
        bundle.putString("destination_url", college.website);
        bundle.putString("description", college.detail);

        FeaturedSlideView textSliderView = new FeaturedSlideView(getContext());
        // initialize a SliderLayout
        textSliderView
                .description(college.name)
                .image(college.banner_image)
                .bundle(bundle)
                .setScaleType(BaseSliderView.ScaleType.CenterCrop);

        adSlider.addSlider(textSliderView);
        adSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}