package np.com.aawaz.csitentrance.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 1001;
    CardView facebook, google;
    GoogleApiClient client;
    private FirebaseAuth mAuth;

    private CallbackManager callBackManager;

    ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean processing = false;
    private TextInputEditText username, password;
    private CardView signIn;

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
                    if (user.getPhoneNumber() == null)
                        phoneIntent();
                    else if (user.getPhoneNumber().equals(""))
                        phoneIntent();

                    else {
                        SPHandler.getInstance().setPhoneNo(user.getPhoneNumber());
                        startActivity(new Intent(SignInActivity.this, MainActivity.class)
                                .putExtra("fragment", getIntent().getStringExtra("fragment")));
                        finish();
                    }
                }
            }
        };

        setupViews();

        handleFacebook();

        handleGoogle();

        handleSignInButton();

        handleSignUp();
    }

    private void phoneIntent() {

        startActivity(new Intent(SignInActivity.this, PhoneNoActivity.class)
                .putExtra("fragment", getIntent().getStringExtra("fragment")));
        finish();
    }

    private void handleSignUp() {
        TextView create = (TextView) findViewById(R.id.createAccount);
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
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().length() != 0 && password.getText().length() != 0) {
                    signIn.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    signIn.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    password.clearComposingText();
                                    Snackbar.make(findViewById(R.id.mainSignInView), "Invalid username password.", Snackbar.LENGTH_SHORT).setAction("Forgot password?", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            forgotPassword();
                                        }
                                    }).show();
                                }
                            });
                }
            }
        });
    }

    private void forgotPassword() {
        new MaterialDialog.Builder(this).title("Forgot password")
                .input("Email to sent reset link.", username.getText().toString(), false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull final MaterialDialog forgot_dialog, CharSequence input) {
                        mAuth.sendPasswordResetEmail(input.toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        new EventSender().logEvent("forget_password");
                                        Toast.makeText(SignInActivity.this, "Email has been sent!", Toast.LENGTH_SHORT).show();
                                        forgot_dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                });
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
                processing = false;
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
        username = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.processing);
        signIn = (CardView) findViewById(R.id.signIn);
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
                progressBar.setVisibility(View.INVISIBLE);
                processing = false;
                Toast.makeText(this, "Unable to connect. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void attachCredToFirebase(AuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        new EventSender().logEvent("user_signup");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        new EventSender().logEvent("attach_error");
                        progressBar.setVisibility(View.INVISIBLE);
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(SignInActivity.this, "Email address in use by another account. Try another option.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignInActivity.this, "Unable to login.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        processing = false;
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        processing = false;
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
