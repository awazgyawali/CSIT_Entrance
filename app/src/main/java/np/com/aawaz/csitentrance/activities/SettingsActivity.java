package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    boolean newsSub,
            forumSub;
    SwitchCompat news, forum, showAnswer;
    TextView clearAll, connectFb, connectGoogle;
    CallbackManager callBackManager = CallbackManager.Factory.create();
    private int RC_SIGN_IN = 100;
    private GoogleApiClient client;
    private TextView addPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarSetting));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        news = (SwitchCompat) findViewById(R.id.notifNews);
        forum = (SwitchCompat) findViewById(R.id.notifForum);
        showAnswer = (SwitchCompat) findViewById(R.id.answer_button);

        clearAll = (TextView) findViewById(R.id.clearAll);
        addPhoneNo = (TextView) findViewById(R.id.add_phone_number);
        connectFb = (TextView) findViewById(R.id.connectFb);
        connectGoogle = (TextView) findViewById(R.id.connectGoogle);

        news.setChecked(SPHandler.getInstance().getNewsSubscribed());
        forum.setChecked(SPHandler.getInstance().getForumSubscribed());
        showAnswer.setChecked(SPHandler.getInstance().shouldShowAnswers());

        news.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (newsSub)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("new");
                else
                    FirebaseMessaging.getInstance().subscribeToTopic("new");
                newsSub = !newsSub;
                SPHandler.getInstance().setNewsSubscribed(newsSub);
            }
        });

        forum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (forumSub)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("forums");
                else
                    FirebaseMessaging.getInstance().subscribeToTopic("forums");
                forumSub = !forumSub;
                SPHandler.getInstance().setForumSubscribed(forumSub);
            }
        });
        showAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHandler.getInstance().showAnswer(isChecked);
            }
        });
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(SettingsActivity.this)
                        .title("Confirmation")
                        .content("This will delete your progress for all years quiz and even from the leader board.")
                        .positiveText("Clear all")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                removeAllTheProgress();
                            }
                        })
                        .show();

            }
        });
        handleConnections();
    }

    private void handleConnections() {
        addPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, PhoneNoActivity.class));
            }
        });
        handleFacebook();
        handleGoogle();
        checkAttachment();
    }

    private void checkAttachment() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        for (String provider : user.getProviders()) {
            if (provider.equals("facebook.com")) {
                connectFb.setText("Connected with Facebook");
                connectFb.setOnClickListener(null);
            } else if (provider.equals("google.com")) {
                connectGoogle.setText("Connected with Google");
                connectGoogle.setOnClickListener(null);
            } else if (!SPHandler.getInstance().getPhoneNo().equals("")) {
                addPhoneNo.setText("Connected with phone number");
                addPhoneNo.setOnClickListener(null);
            }
        }
    }

    private void handleGoogle() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        try {
            client = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        connectGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(client);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void handleFacebook() {
        final LoginButton facebookLoginButton = new LoginButton(this);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                new EventSender().logEvent("fb_connected");
                addAccount(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()));
            }

            @Override
            public void onCancel() {
                Toast.makeText(SettingsActivity.this, "User aborted connection.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SettingsActivity.this, "Unable to connect connection.", Toast.LENGTH_SHORT).show();
            }
        });

        connectFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLoginButton.callOnClick();
            }
        });
    }

    private void addAccount(AuthCredential credential) {
        FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                checkAttachment();
            }
        });

    }

    private void removeAllTheProgress() {
        SPHandler handler = SPHandler.getInstance();
        handler.clearAll();
        Toast.makeText(this, "Cleared all progress!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //For fb login
        callBackManager.onActivityResult(requestCode, resultCode, data);

        //For Google Login
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                new EventSender().logEvent("google_signedup");
                addAccount(GoogleAuthProvider.getCredential(account.getIdToken(), null));
            } else {
                Toast.makeText(this, "Unable to connect. CHeck your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
