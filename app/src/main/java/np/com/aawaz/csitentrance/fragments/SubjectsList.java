package np.com.aawaz.csitentrance.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import np.com.aawaz.csitentrance.R;


public class SubjectsList extends Fragment {

    public SubjectsList() {
        // Required empty public constructor
    }

    public static SubjectsList newInstance() {
        return new SubjectsList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subjects_list, container, false);
    }

}
