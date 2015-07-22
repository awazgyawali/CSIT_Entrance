package np.com.aawaz.csitentrance.Activities;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.melnykov.fab.FloatingActionButton;

import java.util.UUID;

import np.com.aawaz.csitentrance.AdvanceClasses.CirclePageIndicator;
import np.com.aawaz.csitentrance.Fragments.PageFragment;
import np.com.aawaz.csitentrance.R;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    EditText name;
    EditText sur;
    EditText email;
    FloatingActionButton fab;
    SharedPreferences pref;

    int avatar = 0;
    Context context;

    ImageView a1;
    ImageView a2;
    ImageView a3;
    ImageView a4;
    ImageView a5;
    ImageView a6;
    ImageView a7;
    ImageView a8;
    ImageView a9;
    ImageView a10;
    ImageView a11;
    ImageView a12;

    RelativeLayout splash;
    RelativeLayout first;
    RelativeLayout viewPagerLayout;

    ViewPager introViewPager;
    CirclePageIndicator indicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        splash = (RelativeLayout) findViewById(R.id.reqularLayout);
        first = (RelativeLayout) findViewById(R.id.firstLayout);
        viewPagerLayout = (RelativeLayout) findViewById(R.id.viewPagerLayout);

        introViewPager = (ViewPager) findViewById(R.id.introViewPager);

        context = this;

        pref = getSharedPreferences("info", MODE_PRIVATE);
        if (!pref.getString("Name", "").equals("")) {
            splash.setVisibility(View.VISIBLE);
            Thread background = new Thread() {
                public void run() {
                    try {
                        sleep(1500);
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {

                    }
                }
            };
            background.start();
        } else {
            first.setVisibility(View.VISIBLE);
            firstTime();
        }

    }

    private void firstTime() {
        callAllAvatarImage();
        name = (EditText) findViewById(R.id.NameText);
        sur = (EditText) findViewById(R.id.LastText);
        email = (EditText) findViewById(R.id.email);
        fab = (FloatingActionButton) findViewById(R.id.fabBtn);
        name.addTextChangedListener(this);
        sur.addTextChangedListener(this);
        email.addTextChangedListener(this);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_18dp));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Name", name.getText().toString());
                editor.putString("Surname", sur.getText().toString());
                editor.putString("E-mail", email.getText().toString());
                editor.putString("uniqueID", UUID.randomUUID().toString());
                editor.putInt("Avatar", avatar);
                editor.apply();
                showViewPager();
            }
        });
    }

    private void showViewPager() {
        first.setVisibility(View.GONE);
        viewPagerLayout.setVisibility(View.VISIBLE);
        final FragmentPagerAdapter viewPager = new CustomPagerAdapter(getSupportFragmentManager());
        introViewPager.setAdapter(viewPager);

        final int colors[] = {Color.parseColor("#ff8a80"), Color.parseColor("#83ffff"), Color.parseColor("#81c784"), Color.parseColor("#ffff8d"),
                Color.parseColor("#b388fe"), Color.parseColor("#81c784"), Color.parseColor("#ffff8d"), 0};
        indicator = (CirclePageIndicator) findViewById(R.id.pagerIndicator);
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

    public void callAllAvatarImage() {
        a1 = (ImageView) findViewById(R.id.a1);
        a2 = (ImageView) findViewById(R.id.a2);
        a3 = (ImageView) findViewById(R.id.a3);
        a4 = (ImageView) findViewById(R.id.a4);
        a5 = (ImageView) findViewById(R.id.a5);
        a6 = (ImageView) findViewById(R.id.a6);
        a7 = (ImageView) findViewById(R.id.a7);
        a8 = (ImageView) findViewById(R.id.a8);
        a9 = (ImageView) findViewById(R.id.a9);
        a10 = (ImageView) findViewById(R.id.a10);
        a11 = (ImageView) findViewById(R.id.a11);
        a12 = (ImageView) findViewById(R.id.a12);
        a1.setOnClickListener(this);
        a2.setOnClickListener(this);
        a3.setOnClickListener(this);
        a4.setOnClickListener(this);
        a5.setOnClickListener(this);
        a6.setOnClickListener(this);
        a7.setOnClickListener(this);
        a8.setOnClickListener(this);
        a9.setOnClickListener(this);
        a10.setOnClickListener(this);
        a11.setOnClickListener(this);
        a12.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        resetAllBack();
        if (view == a1) {
            avatar = 1;
            a1.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if (view == a2) {
            avatar = 2;
            a2.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if (view == a3) {
            avatar = 3;
            a3.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if (view == a4) {
            avatar = 4;
            a4.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if (view == a5) {
            avatar = 5;
            a5.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));


        } else if (view == a6) {
            avatar = 6;
            a6.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if (view == a7) {
            avatar = 7;
            a7.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if (view == a8) {
            avatar = 8;
            a8.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if (view == a9) {
            avatar = 9;
            a9.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if (view == a10) {
            avatar = 10;
            a10.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if (view == a11) {
            avatar = 11;
            a11.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if (view == a12) {
            avatar = 12;
            a12.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        }

        name.setText(name.getText().toString());

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!name.getText().toString().equals("") && !sur.getText().toString().equals("") && !email.getText().toString().equals("") && avatar != 0) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void resetAllBack() {
        a1.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a2.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a3.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a4.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a5.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a6.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a7.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a8.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a9.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a10.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a11.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a12.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
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
