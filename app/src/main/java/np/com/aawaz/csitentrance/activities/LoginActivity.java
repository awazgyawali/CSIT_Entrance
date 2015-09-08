package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import np.com.aawaz.csitentrance.R;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    EditText name;
    EditText sur;
    EditText email;
    EditText phone;
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

    LinearLayout splash;
    RelativeLayout first;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        first = (RelativeLayout) findViewById(R.id.firstLayout);
        splash = (LinearLayout) findViewById(R.id.reqularLayout);
        first.setVisibility(View.GONE);
        splash.setVisibility(View.GONE);
        fab = (FloatingActionButton) findViewById(R.id.fabBtn);
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
                        finish();
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
        phone = (EditText) findViewById(R.id.phoneNo);
        email = (EditText) findViewById(R.id.email);
        name.addTextChangedListener(this);
        sur.addTextChangedListener(this);
        phone.addTextChangedListener(this);
        email.addTextChangedListener(this);
        fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done_black_18dp));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.getText().toString().length() != 10 || !email.getText().toString().contains(".com") || !email.getText().toString().contains("@")) {
                    if (phone.getText().toString().length() != 10) {
                        YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(phone);
                        Toast.makeText(LoginActivity.this, "Invalid mobile number.", Toast.LENGTH_SHORT).show();
                        phone.setText("");
                    } else {
                        YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(email);
                        Toast.makeText(LoginActivity.this, "Invalid email.", Toast.LENGTH_SHORT).show();
                        email.setText("");
                    }
                } else {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Name", name.getText().toString());
                    editor.putString("Surname", sur.getText().toString());
                    editor.putString("E-mail", email.getText().toString());
                    editor.putString("PhoneNo", phone.getText().toString() + "");
                    editor.putString("uniqueID", email.getText().toString());
                    editor.putInt("Avatar", avatar);
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(), Introduction.class));
                    finish();
                }
            }
        });
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
            a1.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a2) {
            avatar = 2;
            a2.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a3) {
            avatar = 3;
            a3.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a4) {
            avatar = 4;
            a4.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a5) {
            avatar = 5;
            a5.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a6) {
            avatar = 6;
            a6.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a7) {
            avatar = 7;
            a7.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a8) {
            avatar = 8;
            a8.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a9) {
            avatar = 9;
            a9.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a10) {
            avatar = 10;
            a10.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a11) {
            avatar = 11;
            a11.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        } else if (view == a12) {
            avatar = 12;
            a12.setBackgroundColor(ContextCompat.getColor(this, R.color.primary1));

        }

        name.setText(name.getText().toString());

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!name.getText().toString().equals("") && !phone.getText().toString().equals("") && !sur.getText().toString().equals("") && !email.getText().toString().equals("") && avatar != 0) {
            fab.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Landing)
                    .duration(700)
                    .playOn(fab);
        } else {
            fab.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void resetAllBack() {
        a1.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a3.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a4.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a5.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a6.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a7.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a8.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a9.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a10.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a11.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        a12.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }

}
