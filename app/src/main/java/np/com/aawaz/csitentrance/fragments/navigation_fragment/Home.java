package np.com.aawaz.csitentrance.fragments.navigation_fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;
import np.com.aawaz.csitentrance.activities.ModelEntranceActivity;
import np.com.aawaz.csitentrance.fragments.other_fragments.YearsList;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class Home extends Fragment {


    public static ViewPager viewPager;
    TabLayout tabLayout;
    TextView modelEntranceExam;
    String[] titles = {"Home", "CSIT Colleges"};

    public Home() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tabLayout.removeAllTabs();
        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return YearsList.newInstance();
                    case 1:
                        return AllColleges.newInstance();
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MainActivity.setTitle(titles[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0)
                .setIcon(ContextCompat.getDrawable(getContext(), R.drawable.selector_calender));
        tabLayout.getTabAt(1)
                .setIcon(ContextCompat.getDrawable(getContext(), R.drawable.selector_colleges));
    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseDatabase.getInstance().getReference().child("demo_entrance/live").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (Boolean.valueOf(dataSnapshot.getValue().toString()))
                    modelEntranceExam.setVisibility(View.VISIBLE);
                else
                    modelEntranceExam.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("demo_entrance/result_published").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (Boolean.valueOf(dataSnapshot.getValue().toString()))
                    modelEntranceExam.setText("Model Entrance Exam - View Result");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (SPHandler.getInstance().getRegistrationDetail() != null) {
            modelEntranceExam.setText("Model Entrance Exam");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewPagerHome);
        tabLayout = ((MainActivity) getActivity()).tabLayout;
        modelEntranceExam = (TextView) view.findViewById(R.id.modelEntranceExam);
        modelEntranceExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ModelEntranceActivity.class));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager, container, false);
    }

}
