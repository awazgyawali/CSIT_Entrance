package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import np.com.aawaz.csitentrance.R;


public class LoginActivity extends AppCompatActivity implements TextWatcher {
    EditText name;
    EditText sur;
    EditText email;
    EditText phone;
    FloatingActionButton fab;
    SharedPreferences pref;

    Context context;

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
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!name.getText().toString().equals("") && !phone.getText().toString().equals("") && !sur.getText().toString().equals("") && !email.getText().toString().equals("")) {
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
}
