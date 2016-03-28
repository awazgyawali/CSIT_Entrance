package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import np.com.aawaz.csitentrance.R;

public class FeaturedColleges extends Fragment {


    public FeaturedColleges() {
        // Required empty public constructor
    }

    public static FeaturedColleges newInstance() {
        return new FeaturedColleges();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

}
