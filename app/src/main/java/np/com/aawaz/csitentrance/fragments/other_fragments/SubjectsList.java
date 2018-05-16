package np.com.aawaz.csitentrance.fragments.other_fragments;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.SubjectChooserActivity;
import np.com.aawaz.csitentrance.custom_views.SubjectCard;


public class SubjectsList extends Fragment {
    SubjectCard chem, phy, math, eng;

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

        chem = view.findViewById(R.id.chems);
        chem.setText("Chemistry")
                .setImage(R.drawable.chemistry);

        phy = view.findViewById(R.id.physc);
        phy.setText("Physics")
                .setImage(R.drawable.physics);

        math = view.findViewById(R.id.maths);
        math.setText("Mathematics")
                .setImage(R.drawable.math);

        eng = view.findViewById(R.id.english);
        eng.setText("English")
                .setImage(R.drawable.english);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        phy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(getContext(), SubjectChooserActivity.class).putExtra("index", 0),
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), phy.findViewById(R.id.subject_image), "destinationSubject").toBundle());

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
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), chem.findViewById(R.id.subject_image), "destinationSubject").toBundle());
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
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), math.findViewById(R.id.subject_image), "destinationSubject").toBundle());
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
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), eng.findViewById(R.id.subject_image), "destinationSubject").toBundle());
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
