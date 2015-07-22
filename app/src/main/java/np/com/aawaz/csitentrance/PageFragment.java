package np.com.aawaz.csitentrance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by aawaz on 7/22/15.
 */
public class PageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (getArguments().getInt("position")) {
            case 0:
                return inflater.inflate(R.layout.pager_item_one, container, false);
            case 1:
                return inflater.inflate(R.layout.pager_item_one, container, false);
            case 2:
                return inflater.inflate(R.layout.pager_item_one, container, false);
            case 3:
                return inflater.inflate(R.layout.pager_item_one, container, false);
            default:
                return inflater.inflate(R.layout.pager_item_one, container, false);
        }
    }
}
