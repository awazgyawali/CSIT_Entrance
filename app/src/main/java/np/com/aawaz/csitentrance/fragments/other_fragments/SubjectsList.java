package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import np.com.aawaz.csitentrance.R;


public class SubjectsList extends Fragment {
    CardView chem, phy, math, eng;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chem = (CardView) view.findViewById(R.id.chems);
        phy = (CardView) view.findViewById(R.id.physc);
        math = (CardView) view.findViewById(R.id.maths);
        eng = (CardView) view.findViewById(R.id.english);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("subject", "Chemistry");
                openBottom(bundle);

            }
        });
        phy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Bundle bundle = new Bundle();
                bundle.putString("subject", "Physics");
                openBottom(bundle);
            }
        });
        math.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Bundle bundle = new Bundle();
                bundle.putString("subject", "Mathematics");
                openBottom(bundle);
            }
        });
        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Bundle bundle = new Bundle();
                bundle.putString("subject", "English");
                openBottom(bundle);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subjects_list, container, false);
    }

    private void openBottom(Bundle bundle) {
        BottomSheetDialogFragment bottomSheetDialogFragment = new SubjectYearChooser();
        bottomSheetDialogFragment.setArguments(bundle);
        bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());

    }

}
