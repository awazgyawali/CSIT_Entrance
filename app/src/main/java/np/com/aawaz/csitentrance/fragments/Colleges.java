package np.com.aawaz.csitentrance.fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.CollegesAdapter;


public class Colleges extends Fragment {
    private ArrayList<String> names = new ArrayList<>(),
            desc = new ArrayList<>(),
            website = new ArrayList<>(),
            address = new ArrayList<>(),
            phNo = new ArrayList<>();
    RecyclerView colzRecy;

    public static String AssetJSONFile(String filename, Context c) throws IOException {
        AssetManager manager = c.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();
        return new String(formArray);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        colzRecy = (RecyclerView) view.findViewById(R.id.colzRecy);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDataToArrayList();

        fillNormally();
    }


    private void fillNormally() {
        //Recycler view handler
        CollegesAdapter adapter = new CollegesAdapter(getContext(), names, address, desc, website, phNo);
        colzRecy.setAdapter(adapter);
        colzRecy.setLayoutManager(new StaggeredGridLayoutManager(isLargeScreen() ? 2 : 1, StaggeredGridLayoutManager.VERTICAL));
    }

    /*
        private void checkOccurrence() {
            namesNew.clear();
            descNew.clear();
            websiteNew.clear();
            addressNew.clear();
            phNoNew.clear();
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).toLowerCase().contains(search.getText().toString().toLowerCase()) ||
                        address.get(i).toLowerCase().contains(search.getText().toString().toLowerCase()) ||
                        desc.get(i).contains(search.getText().toString())) {
                    namesNew.add(names.get(i));
                    descNew.add(desc.get(i));
                    websiteNew.add(website.get(i));
                    addressNew.add(address.get(i));
                    phNoNew.add(phNo.get(i));
                }
            }
            fillNewRecy();
        }

    */

    public void setDataToArrayList() {
        //Reading from json file and insillizing inside arrayList
        try {
            JSONObject obj = new JSONObject(AssetJSONFile("college_feed.json", getContext()));
            JSONArray m_jArry = obj.getJSONArray("lists");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                names.add(jo_inside.getString("name"));
                desc.add(jo_inside.getString("desc"));
                website.add(jo_inside.getString("website"));
                address.add(jo_inside.getString("address"));
                phNo.add(jo_inside.getString("phone"));
            }

        } catch (Exception ignored) {
        }
    }

    public boolean isLargeScreen() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_colleges, container, false);
    }
}