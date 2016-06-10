package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.google.firebase.auth.FirebaseAuth;

import np.com.aawaz.csitentrance.R;


public class SplashAndIntroActivity extends AppIntro {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = this;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            showSkipButton(false);
            setProgressButtonEnabled(false);
            setBarColor(Color.TRANSPARENT);
            setSeparatorColor(Color.TRANSPARENT);

            addSlide(AppIntroFragment.newInstance(getString(R.string.app_name),
                    getString(R.string.tag_link),
                    R.drawable.splash_icon,
                    ContextCompat.getColor(this, R.color.colorPrimary)));

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

            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_one),
                    getString(R.string.description_1),
                    R.drawable.play,
                    ContextCompat.getColor(this, R.color.background_1)));

            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_two),
                    getString(R.string.description_2),
                    R.drawable.scoreboard_big,
                    ContextCompat.getColor(this, R.color.background_2)));

            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_three),
                    getString(R.string.description_3),
                    R.drawable.school_big,
                    ContextCompat.getColor(this, R.color.background_3)));

            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_four),
                    getString(R.string.description_4),
                    R.drawable.news_big,
                    ContextCompat.getColor(this, R.color.background_4)));

            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_five),
                    getString(R.string.description_5),
                    R.drawable.forum_big,
                    ContextCompat.getColor(this, R.color.background_5)));

            addSlide(AppIntroFragment.newInstance(getString(R.string.intro_six),
                    getString(R.string.description_6),
                    R.drawable.result_big,
                    ContextCompat.getColor(this, R.color.background_6)));
            setDoneText("Get Started");
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
