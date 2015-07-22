package np.com.aawaz.csitentrance.Activities;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import np.com.aawaz.csitentrance.AdvanceClasses.CirclePageIndicator;
import np.com.aawaz.csitentrance.Fragments.PageFragment;
import np.com.aawaz.csitentrance.R;


public class Introduction extends AppCompatActivity {

    ViewPager introViewPager;
    CirclePageIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        indicator = (CirclePageIndicator) findViewById(R.id.pagerIndicator);
        introViewPager = (ViewPager) findViewById(R.id.introViewPager);

        final FragmentPagerAdapter viewPager = new CustomPagerAdapter(getSupportFragmentManager());
        introViewPager.setAdapter(viewPager);
        final int colors[] = {Color.parseColor("#ff8a80"), Color.parseColor("#83ffff"), Color.parseColor("#81c784"), Color.parseColor("#ffff8d"),
                Color.parseColor("#b388fe"), Color.parseColor("#81c784"), Color.parseColor("#ffff8d"), 0};
        indicator.setViewPager(introViewPager);
        introViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (android.os.Build.VERSION.SDK_INT >= 11) {
                    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
                    introViewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
                } else {
                    introViewPager.setBackgroundColor(colors[position]);
                }

            }


            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void startMainActivity(View v) {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class CustomPagerAdapter extends FragmentPagerAdapter {


        public CustomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            PageFragment frag = new PageFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            frag.setArguments(bundle);
            return frag;
        }

        @Override
        public int getCount() {
            return 7;
        }
    }
}
