package np.com.aawaz.csitentrance.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.advance.MyApplication;
import np.com.aawaz.csitentrance.advance.Singleton;

public class Result extends AppCompatActivity {
    TextView unPublished;
    RequestQueue requestQueue;
    MaterialDialog dialog;
    SharedPreferences result;
    FancyButton resultButton;
    LinearLayout viewResult;
    EditText rollNo;
    ImageView adView;



    private void workForViewingResult() {
        resultButton= (FancyButton) findViewById(R.id.resultReqButton);
        rollNo= (EditText) findViewById(R.id.resultRollNo);
        resultButton.setEnabled(false);
        rollNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()!=0)
                    resultButton.setEnabled(true);
                else
                    resultButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                StringRequest request=new StringRequest(Request.Method.GET, "http://avaaj.com.np/jsonFeed/result.php?rollNo="+rollNo.getText(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("null")){
                            new MaterialDialog.Builder(Result.this)
                                    .content("Oops!! This roll number is not in the list.")
                                    .build()
                                    .show();
                        } else{
                            startActivity(new Intent(Result.this,ViewResult.class).putExtra("result",response));
                        }
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Snackbar.make(findViewById(R.id.coreResult), "Unable to connect to the server. Please try again.", Snackbar.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                requestQueue.add(request);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarResult));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MyApplication.changeStatusBarColor(R.color.status_bar_college, this);   //To change code and ad view milaune

        requestQueue= Singleton.getInstance().getRequestQueue();
        unPublished= (TextView) findViewById(R.id.unPublished);
        viewResult= (LinearLayout) findViewById(R.id.viewResult);
        adView= (ImageView) findViewById(R.id.adImage);
        Picasso.with(this)
                .load("www.avaaj.com.np/ads/featured.jpg")
                .into(adView);
        readyDialog();

        result=getSharedPreferences("resultInfo",MODE_PRIVATE);

        if(result.getBoolean("published",false)){
            viewResult.setVisibility(View.VISIBLE);
            workForViewingResult();
        } else {
            dialog.show();
            isPublishedCheck();
        }
    }

    private void isPublishedCheck(){
        StringRequest request = new StringRequest(Request.Method.GET, "http://avaaj.com.np/jsonFeed/isPublished.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                if (response.contains("published")) {
                    result.edit().putBoolean("published",true).apply();
                    workForViewingResult();
                    viewResult.setVisibility(View.VISIBLE);
                } else
                    unPublished.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(Result.this, "Unable to connect to the server. Please try again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        requestQueue.add(request);
    }

    private void readyDialog(){
        dialog = new MaterialDialog.Builder(this)
            .progress(true, 0)
            .content("Please wait...")
            .cancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    finish();
                }
            })
            .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
