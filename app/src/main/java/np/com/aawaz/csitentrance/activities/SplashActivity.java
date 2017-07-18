package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class SplashActivity extends AppCompatActivity {

    Context context;
    Intent main_activity_intent, sign_in_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;
        new EventSender().logEvent("app_opened");
        sign_in_intent = new Intent(this, SignInActivity.class);
        main_activity_intent = new Intent(context, MainActivity.class)
                .replaceExtras(getIntent().getExtras());

        if (getIntent().getStringExtra("result_published") != null)
            SPHandler.getInstance().setResultPublished();

        onNewIntent(getIntent());

    }

    @Override
    protected void onStart() {
        super.onStart();
        new EventSender()
                .logEvent("samriddhi_splash");
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (SPHandler.getInstance().getPhoneNo().equals(""))
                        startActivity(new Intent(SplashActivity.this, PhoneNoActivity.class));
                    else
                        startActivity(main_activity_intent);
                    finish();
                }
            }, 5000);
        } else {
            startActivity(sign_in_intent);
            finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
