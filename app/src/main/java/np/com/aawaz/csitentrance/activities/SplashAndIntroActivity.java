package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.google.firebase.auth.FirebaseAuth;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.fragments.other_fragments.SplashFragment;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class SplashAndIntroActivity extends AppIntro {

    Context context;
    Intent main_activity_intent, sign_in_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        new EventSender().logEvent("app_opened");
        sign_in_intent = new Intent(this, SignInActivity.class);
        main_activity_intent = new Intent(context, MainActivity.class)
                .replaceExtras(getIntent().getExtras());

        if (getIntent().getStringExtra("result_published") != null)
            SPHandler.getInstance().setResultPublished();

        onNewIntent(getIntent());

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            showSkipButton(false);

            setProgressButtonEnabled(false);
            setBarColor(Color.TRANSPARENT);
            setSeparatorColor(Color.TRANSPARENT);

            addSlide(new SplashFragment());

            selectedIndicatorColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(main_activity_intent);
                    finish();
                }
            }, 1500);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        Uri data = intent.getData();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            main_activity_intent.putExtra("fragment", data.getLastPathSegment());
            sign_in_intent.putExtra("fragment", data.getLastPathSegment());
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(sign_in_intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(sign_in_intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
