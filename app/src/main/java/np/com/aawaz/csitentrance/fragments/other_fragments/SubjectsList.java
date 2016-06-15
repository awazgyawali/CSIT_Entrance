package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.SubjectChooserActivity;


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


        phy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(getContext(), SubjectChooserActivity.class).putExtra("index", 0),
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), getView().findViewById(R.id.phyImage), "destinationSubject").toBundle());

                    getActivity().getWindow().setSharedElementExitTransition(null);
                } else {
                    startActivity(new Intent(getContext(), SubjectChooserActivity.class).putExtra("index", 0));
                }
            }
        });


        chem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(getContext(), SubjectChooserActivity.class).putExtra("index", 1),
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), getView().findViewById(R.id.chemImage), "destinationSubject").toBundle());
                    getActivity().getWindow().setSharedElementExitTransition(null);

                } else {
                    startActivity(new Intent(getContext(), SubjectChooserActivity.class).putExtra("index", 1));
                }
            }
        });

        math.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(getContext(), SubjectChooserActivity.class).putExtra("index", 2),
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), getView().findViewById(R.id.mthImage), "destinationSubject").toBundle());
                    getActivity().getWindow().setSharedElementExitTransition(null);
                } else {
                    startActivity(new Intent(getContext(), SubjectChooserActivity.class).putExtra("index", 2));
                }

            }
        });
        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(getContext(), SubjectChooserActivity.class).putExtra("index", 3),
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), getView().findViewById(R.id.engImage), "destinationSubject").toBundle());
                    getActivity().getWindow().setSharedElementExitTransition(null);
                } else {
                    startActivity(new Intent(getContext(), SubjectChooserActivity.class).putExtra("index", 3));
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subjects_list, container, false);
    }
}
