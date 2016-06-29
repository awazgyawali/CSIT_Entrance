package np.com.aawaz.csitentrance.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.robototextview.widget.RobotoTextView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 1001;
    CardView facebook, google, email;
    GoogleApiClient client;
    private FirebaseAuth mAuth;

    private CallbackManager callBackManager;
    private TextInputEditText username = null,
            password = null;


    ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean processing = false;

    public SignInActivity() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    FirebaseMessaging.getInstance().subscribeToTopic("news");
                    FirebaseMessaging.getInstance().subscribeToTopic("forum");
                    startActivity(new Intent(SignInActivity.this, MainActivity.class)
                            .putExtra("fragment", getIntent().getStringExtra("fragment")));
                    finish();
                }
            }
        };

        setupViews();

        handleFacebook();

        handleGoogle();

        handleSignInButton();

        handleSignUp();
    }

    private void handleSignUp() {
        RobotoTextView create = (RobotoTextView) findViewById(R.id.createAccount);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermissionAndContinue(new Intent(SignInActivity.this, SignUpActivity.class), 100);
            }
        });
    }

    private void askPermissionAndContinue(final Intent intent, final int req) {
        new TedPermission(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        startActivityForResult(intent, req);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> arrayList) {

                    }
                })
                .setDeniedMessage("It seems that you rejected the permission request.\n\nPlease turn on Storage permissions from settings to proceed.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void handleSignInButton() {
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = setupEmailSignInDialog();

                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                dialog.show();
            }
        });

    }

    public MaterialDialog setupEmailSignInDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Sign in with e-mail")
                .icon(ContextCompat.getDrawable(this, R.drawable.email))
                .positiveText("Sign In")
                .neutralText("Forgot Password")
                .autoDismiss(false)
                .customView(R.layout.email_login, false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        if (username.getText().length() != 0 && password.getText().length() != 0) {
                            dialog.setCancelable(false);
                            dialog.getActionButton(DialogAction.POSITIVE).setText("Signing in...");
                            mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.setCancelable(true);
                                            dialog.getActionButton(DialogAction.POSITIVE).setText("Sign In");
                                            ((TextInputLayout) username.getParent()).setError("Email or password didn't match.");
                                        }
                                    });
                        }
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new MaterialDialog.Builder(SignInActivity.this)
                                .title("Forgot Password")
                                .input("Email", "", false, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        mAuth.sendPasswordResetEmail(input.toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(SignInActivity.this, "Email has been sent!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .show();
                    }
                })
                .build();

        username = (TextInputEditText) dialog.getView().findViewById(R.id.username);
        password = (TextInputEditText) dialog.getView().findViewById(R.id.password);

        return dialog;
    }

    private void handleGoogle() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();

        client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)

                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!processing) {
                    processing = true;
                    progressBar.setVisibility(View.VISIBLE);
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(client);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });
    }

    private void handleFacebook() {
        callBackManager = CallbackManager.Factory.create();

        final LoginButton facebookLoginButton = new LoginButton(this);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                new EventSender().logEvent("fb_signedup");
                attachCredToFirebase(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()));
            }

            @Override
            public void onCancel() {
                processing = false;
            }

            @Override
            public void onError(FacebookException error) {
                processing = true;
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!processing) {
                    processing = true;
                    facebookLoginButton.callOnClick();
                }
            }
        });
    }

    public void setupViews() {
        google = (CardView) findViewById(R.id.loginGoogle);
        facebook = (CardView) findViewById(R.id.loginFacebook);
        email = (CardView) findViewById(R.id.loginEmail);
        progressBar = (ProgressBar) findViewById(R.id.processing);
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
                attachCredToFirebase(GoogleAuthProvider.getCredential(account.getIdToken(), null));
            } else {
                progressBar.setVisibility(View.GONE);
                processing = false;
                Toast.makeText(this, "Unable to connect. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }

        if (mAuth.getCurrentUser() != null) {
            FirebaseMessaging.getInstance().subscribeToTopic("news");
            FirebaseMessaging.getInstance().subscribeToTopic("forum");
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra("fragment", getIntent().getStringExtra("fragment")));
            finish();
        }
    }

    public void attachCredToFirebase(AuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new EventSender().logEvent("attach_error");
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        processing = false;
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
