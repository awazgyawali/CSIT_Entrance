package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class PhoneNoActivity extends AppCompatActivity {


    TextInputEditText phone, c_code;
    TextView autoVerify, counter;
    MaterialDialog verifying, dialog;
    CountDownTimer downTimer;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private FirebaseAuth mAuth;
    private TextView skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no);
        TextView detail = findViewById(R.id.phone_string_text);
        phone = findViewById(R.id.phone_no);
        c_code = findViewById(R.id.c_code);
        autoVerify = findViewById(R.id.auto_verify_in);
        counter = findViewById(R.id.timer);
        skip = findViewById(R.id.skip);

        autoVerify.setVisibility(View.GONE);
        counter.setVisibility(View.GONE);

        final CardView submit = findViewById(R.id.phone_continue_button);

        dialog = new MaterialDialog.Builder(PhoneNoActivity.this)
                .content("Sending verification code")
                .progress(true, 100)
                .cancelable(false)
                .build();

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPHandler.getInstance().setPhoneNo("SKIPPED");
                startActivity(new Intent(PhoneNoActivity.this, MainActivity.class)
                        .putExtra("fragment", getIntent().getStringExtra("fragment")));
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_link))));
            }
        });

        submit.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 10)
                    submit.setVisibility(View.VISIBLE);
                else
                    submit.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        phone.setText(SPHandler.getInstance().getPhoneNo().replace("+977", ""));

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                    if (user.getPhoneNumber() != null && !user.getPhoneNumber().equals("")) {
                        new EventSender().logEvent("phone_verified");
                        onVerified();
                    }
            }
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                dialog.dismiss();
                e.printStackTrace();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(PhoneNoActivity.this, "Invalid mobile number. Please try again with a valid one.", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(PhoneNoActivity.this, "Too many verification request sent. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                submit.setVisibility(View.VISIBLE);
                if (verifying != null)
                    ((TextView) submit.getChildAt(0)).setText("Resend Code");
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                dialog.dismiss();
                submit.setVisibility(View.INVISIBLE);
                phone.setEnabled(false);
                counter.setVisibility(View.VISIBLE);
                autoVerify.setVisibility(View.VISIBLE);
                downTimer = new CountDownTimer(25000, 1000) {

                    @Override
                    public void onTick(long l) {
                        counter.setText(l / 1000 + " seconds");
                    }

                    @Override
                    public void onFinish() {
                        onVerified();
                    }
                }.start();
            }

        };

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (c_code.getText().toString().length() > 1) {
                    new MaterialDialog.Builder(PhoneNoActivity.this)
                            .title("Confirm your number.")
                            .content(Html.fromHtml("Is <b>" + c_code.getText().toString() + phone.getText().toString() + "</b> your phone no?<br><br>This will send a verification code to your phone."))
                            .positiveText("Continue")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    requestCode();
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(PhoneNoActivity.this, "Invalid country code.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestCode() {
        SPHandler.getInstance().setPhoneNo(phone.getText().toString());
        PhoneAuthProvider.getInstance().verifyPhoneNumber(c_code.getText().toString() + phone.getText().toString(),
                60,
                TimeUnit.SECONDS,
                PhoneNoActivity.this,
                mCallback);
        dialog.show();
    }

    private void onVerified() {
        if (verifying != null)
            try {
                verifying.dismiss();
            } catch (Exception ignored) {
            }
        if (dialog != null)
            dialog.dismiss();
        SPHandler.getInstance().setPhoneNo(phone.getText().toString());


        startActivity(new Intent(PhoneNoActivity.this, MainActivity.class)
                .putExtra("fragment", getIntent().getStringExtra("fragment")));
        finish();
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        verifying = new MaterialDialog.Builder(this)
                .content("Verifying the code...")
                .progress(true, 100)
                .build();
        verifying.show();
        downTimer.cancel();
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        new EventSender().logEvent("phone_verified");
                        onVerified();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        verifying.dismiss();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            new MaterialDialog.Builder(PhoneNoActivity.this)
                                    .title("Phone number associated with another account.")
                                    .content("Would you like to login to that account instead?")
                                    .positiveText("Yes")
                                    .negativeText("No")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            FirebaseAuth.getInstance().signOut();
                                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                        @Override
                                                        public void onSuccess(AuthResult authResult) {
                                                            new EventSender().logEvent("phone_verified");
                                                            onVerified();
                                                        }
                                                    });
                                        }
                                    })
                                    .show();
                        } else
                            Toast.makeText(PhoneNoActivity.this, "Invalid verification code.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
