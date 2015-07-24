package np.com.aawaz.csitentrance.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import np.com.aawaz.csitentrance.R;


public class PageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (getArguments().getInt("position")) {
            case 0:
                return inflater.inflate(R.layout.pager_item_one, container, false);
            case 1:
                return inflater.inflate(R.layout.pager_item_two, container, false);
            case 2:
                return inflater.inflate(R.layout.pager_item_three, container, false);
            case 3:
                return inflater.inflate(R.layout.pager_item_four, container, false);
            case 4:
                return inflater.inflate(R.layout.pager_item_five, container, false);
            case 5:
                return inflater.inflate(R.layout.pager_item_six, container, false);
            case 6:
                return inflater.inflate(R.layout.pager_item_seven, container, false);
            default:
                return null;
        }
    }
}
