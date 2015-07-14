package np.com.aawaz.csitentrance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.melnykov.fab.FloatingActionButton;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    EditText name;
    EditText sur;
    EditText email;
    FloatingActionButton fab;
    SharedPreferences pref;

    int avatar=0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        callAllAvatarImage();
        pref=getSharedPreferences("info",MODE_PRIVATE);
        if(!pref.getString("Name","").equals("")){
            finish();
            Intent main=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(main);
        }
        name= (EditText) findViewById(R.id.NameText);
        sur= (EditText) findViewById(R.id.LastText);
        email= (EditText) findViewById(R.id.email);
        fab= (FloatingActionButton) findViewById(R.id.fabBtn);
        name.addTextChangedListener(this);
        sur.addTextChangedListener(this);
        email.addTextChangedListener(this);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_18dp));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor=pref.edit();
                editor.putString("Name",name.getText().toString());
                editor.putString("Surname", sur.getText().toString());
                editor.putString("E-mail", email.getText().toString());
                editor.putInt("Avatar", avatar);
                editor.commit();
                finish();
                Intent main=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);
            }
        });
    }


    public void callAllAvatarImage(){
        a1= (ImageView) findViewById(R.id.a1);
        a2= (ImageView) findViewById(R.id.a2);
        a3= (ImageView) findViewById(R.id.a3);
        a4= (ImageView) findViewById(R.id.a4);
        a5= (ImageView) findViewById(R.id.a5);
        a6= (ImageView) findViewById(R.id.a6);
        a7= (ImageView) findViewById(R.id.a7);
        a8= (ImageView) findViewById(R.id.a8);
        a9= (ImageView) findViewById(R.id.a9);
        a10= (ImageView) findViewById(R.id.a10);
        a11= (ImageView) findViewById(R.id.a11);
        a12= (ImageView) findViewById(R.id.a12);
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


        if(view==a1){
            avatar=1;
            a1.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if(view==a2){
            avatar=2;
            a2.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if(view==a3){
            avatar=3;
            a3.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if(view==a4){
            avatar=4;
            a4.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if(view==a5){
            avatar=5;
            a5.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));


        } else if(view==a6){
            avatar=6;
            a6.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if(view==a7){
            avatar=7;
            a7.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if(view==a8){
            avatar=8;
            a8.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if(view==a9){
            avatar=9;
            a9.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if(view==a10){
            avatar=10;
            a10.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if(view==a11){
            avatar=11;
            a11.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        } else if(view==a12){
            avatar=12;
            a12.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_light));

        }

        name.setText(name.getText().toString());

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!name.getText().toString().equals("") && !sur.getText().toString().equals("") && !email.getText().toString().equals("") && avatar!=0){
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void resetAllBack(){
        a1.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a2.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a3.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a4.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a5.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a6.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a7.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a8.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a9.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a10.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a11.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        a12.setBackgroundColor(getResources().getColor(R.color.abc_primary_text_material_dark));
    }
}
