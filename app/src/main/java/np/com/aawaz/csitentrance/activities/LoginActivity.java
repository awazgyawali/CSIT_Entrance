package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.fragments.SignUp;


public class LoginActivity extends IntroActivity {

    SharedPreferences pref;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);

        super.onCreate(savedInstanceState);
        setFinishEnabled(false);
        context = this;
        pref = getSharedPreferences("info", MODE_PRIVATE);
        if (!pref.getString("Name", "").equals("")) {

            setFinishEnabled(false);
            setSkipEnabled(false);
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.app_name)
                    .description(R.string.tag_link)
                    .image(R.drawable.splash_icon)
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .build());

            Thread background = new Thread() {
                public void run() {
                    try {
                        sleep(1500);
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        finish();
                    }
                }
            };
            background.start();
        } else {
            //todo Show INTRO
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro_one)
                    .description(R.string.description_1)
                    .image(R.drawable.play)
                    .background(R.color.background_1)
                    .backgroundDark(R.color.background_dark_1)
                    .build());

            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro_two)
                    .description(R.string.description_2)
                    .image(R.drawable.scoreboard_big)
                    .background(R.color.background_2)
                    .backgroundDark(R.color.background_dark_2)
                    .build());

            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro_three)
                    .description(R.string.description_3)
                    .image(R.drawable.school_big)
                    .background(R.color.background_3)
                    .backgroundDark(R.color.background_dark_3)
                    .build());


            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro_four)
                    .description(R.string.description_4)
                    .image(R.drawable.news_big)
                    .background(R.color.background_4)
                    .backgroundDark(R.color.background_dark_4)
                    .build());

            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro_five)
                    .description(R.string.description_5)
                    .image(R.drawable.forum_big)
                    .background(R.color.background_5)
                    .backgroundDark(R.color.background_dark_5)
                    .build());

            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro_six)
                    .description(R.string.description_6)
                    .image(R.drawable.result_big)
                    .background(R.color.background_6)
                    .backgroundDark(R.color.background_dark_6)
                    .build());

            addSlide(new FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(new SignUp())
                    .build());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
