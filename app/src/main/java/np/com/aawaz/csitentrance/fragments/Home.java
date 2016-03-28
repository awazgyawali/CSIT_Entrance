package np.com.aawaz.csitentrance.fragments;


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

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.MainActivity;

public class Home extends Fragment {


    ViewPager viewPager;
    TabLayout tabLayout;

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
                        return YearsList.newInstance("quiz");
                    case 1:
                        return SubjectsList.newInstance();
                    case 2:
                        return YearsList.newInstance("full");
                    case 3:
                        return ScoreBoard.newInstance();
                }
                return null;
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.calender_selector));
        tabLayout.getTabAt(1).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.book_selector));
        tabLayout.getTabAt(2).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.file_selector));
        tabLayout.getTabAt(3).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.scoreboard_selector));
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewPagerHome);
        tabLayout = MainActivity.tabLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager, container, false);
    }

}
