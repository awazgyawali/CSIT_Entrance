package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.interfaces.ResponseListener;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.ModelExam;
import np.com.aawaz.csitentrance.objects.SPHandler;
import np.com.aawaz.csitentrance.services.NetworkRequester;

public class ModelEntranceActivity extends AppCompatActivity {
    TextView venue, address, time, date, fee, detail, registerNow, registeredName, registeredRoll;
    TextInputEditText modelExamResultInput;
    Button modelExamResultButton;
    SliderLayout mockEntranceSlider;
    String regName;

    ModelExam exam;
    private LinearLayout regDetail, resultDetail;
    private TextView resultText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_entrance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        bindViews();
        regName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mockEntranceSlider.addSlider(getSlide("https://scontent.fktm7-1.fna.fbcdn.net/v/t31.0-8/21316208_1510633865667887_1719989955509467391_o.jpg?_nc_cat=0&oh=6f837a9728b03583efc52339f7d72349&oe=5C01612F"));
        mockEntranceSlider.addSlider(getSlide("https://scontent.fktm7-1.fna.fbcdn.net/v/t31.0-8/21200683_1509849439079663_54078487823343609_o.jpg?_nc_cat=0&oh=c9fb52dec08777e4dd0e4046159bcd17&oe=5BFC38D1"));
        mockEntranceSlider.addSlider(getSlide("https://scontent.fktm7-1.fna.fbcdn.net/v/t31.0-8/21272906_1509849189079688_4628729443948047894_o.jpg?_nc_cat=0&oh=1594e220d941d6a8619edd4a3475befb&oe=5C0A1912"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        FirebaseDatabase.getInstance().getReference().child("mock_test").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exam = dataSnapshot.getValue(ModelExam.class);
                venue.setText(exam.getVenue());
                address.setText(exam.getAddress());
                date.setText(exam.getDate());
                fee.setText(exam.getCost());
                time.setText(exam.getTime());
                detail.setText(exam.getDetail());

                if (exam.getCan_reg()) {
                    registerNow.setText("Register Now");
                    registerNow.setEnabled(true);
                    registerNow.setBackgroundColor(ContextCompat.getColor(ModelEntranceActivity.this, R.color.colorAccent));
                } else {
                    registerNow.setText("Registration closed");
                    registerNow.setEnabled(false);
                    registerNow.setBackgroundColor(ContextCompat.getColor(ModelEntranceActivity.this, R.color.grey));
                }

                if (exam.getResult_published())
                    resultDetail.setVisibility(View.VISIBLE);
                else
                    resultDetail.setVerticalGravity(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ModelEntranceActivity.this, "Unable to connect, try again later.", Toast.LENGTH_SHORT).show();
            }
        });

        if (SPHandler.getInstance().getRegistrationDetail() == null) {
            regDetail.setVisibility(View.GONE);

            registerNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MaterialDialog dialog = new MaterialDialog.Builder(ModelEntranceActivity.this)
                            .content("Please wait....")
                            .progress(true, 100)
                            .cancelable(false)
                            .show();

                    register(dialog);
                }
            });
        } else {
            registerNow.setVisibility(View.GONE);
            parseRegData();
        }

        manageResultViewing();
    }

    private void register(final MaterialDialog dialog) {
        HashMap<String, String> map = new HashMap<>();
        map.put("uuid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("name", regName);
        map.put("phone_number", SPHandler.getInstance().getPhoneNo());


        new NetworkRequester(exam.getRegister_link(), map, new ResponseListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("success")) {
                        new EventSender().logEvent("model_registered");
                        SPHandler.getInstance().setRegistrationDetail(object.getJSONObject("data").toString());
                        Toast.makeText(ModelEntranceActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                        parseRegData();
                    } else
                        Toast.makeText(ModelEntranceActivity.this, "Sorry, the registration has been closed.", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(ModelEntranceActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(ModelEntranceActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void manageResultViewing() {
        modelExamResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modelExamResultInput.getText().toString().length() < 1)
                    return;
                HashMap<String, String> map = new HashMap<>();
                map.put("roll", modelExamResultInput.getText().toString());

                final MaterialDialog dialog = new MaterialDialog.Builder(ModelEntranceActivity.this)
                        .content("Please wait....")
                        .progress(true, 100)
                        .cancelable(false)
                        .show();
                new NetworkRequester(exam.getResult_link(), map, new ResponseListener() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject object = new JSONObject(response);

                            if (object.getBoolean("success")) {
                                resultText.setVisibility(View.VISIBLE);
                                String string = "Congratulations <b>" + object.getJSONObject("data").getString("name")
                                        + "</b>. You have scored <b>" + object.getJSONObject("data").getInt("score")
                                        + "</b> and your rank is <b>" + object.getJSONObject("data").getInt("rank")
                                        + ".</b>";
                                resultText.setText(Html.fromHtml(string));
                            } else
                                Toast.makeText(ModelEntranceActivity.this, "Sorry, the result is not available for that roll number.", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ModelEntranceActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(ModelEntranceActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void parseRegData() {
        try {
            JSONObject object = SPHandler.getInstance().getRegistrationDetail();
            regDetail.setVisibility(View.VISIBLE);
            registerNow.setVisibility(View.GONE);
            final String name = object.getString("name");
            final String id = object.getString("id");
            registeredName.setText(name);
            registeredRoll.setText("Roll No: " + object.getString("id"));
            registeredName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(ModelEntranceActivity.this).title("Change Name").input("New Name", name, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            regName = input.toString();
                            updateName(id);
                        }
                    }).show();
                }
            });
        } catch (JSONException e) {
            Toast.makeText(ModelEntranceActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateName(String id) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this).content("Loading.....")
                .progress(true, 100)
                .build();
        dialog.show();
        OkHttpClient client = new OkHttpClient();
        new EventSender().logEvent("name_changed");

        RequestBody body = new FormEncodingBuilder()
                .add("name", regName)
                .build();

        Request request = new Request.Builder()
                .url("https://csit-entrance-7d58.appspot.com/update/" + id)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .put(body)  // Use PUT on this line.
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                e.printStackTrace();
                dialog.dismiss();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                register(dialog);
            }
        });
    }

    private void bindViews() {
        venue = (TextView) findViewById(R.id.modelExamVenue);
        address = (TextView) findViewById(R.id.modelExamAddress);
        time = (TextView) findViewById(R.id.modelExamTime);
        date = (TextView) findViewById(R.id.modelExamDate);
        fee = (TextView) findViewById(R.id.modelExamFee);
        detail = (TextView) findViewById(R.id.modelExamDetail);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + venue.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(ModelEntranceActivity.this, "Google maps doesn't exist..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerNow = (TextView) findViewById(R.id.modelExamRegister);

        registeredName = (TextView) findViewById(R.id.registeredName);

        registeredRoll = (TextView) findViewById(R.id.registeredRoll);

        modelExamResultInput = (TextInputEditText) findViewById(R.id.modelExamResultInput);
        modelExamResultButton = (Button) findViewById(R.id.modelExamResultButton);
        resultText = (TextView) findViewById(R.id.resultText);
        regDetail = (LinearLayout) findViewById(R.id.reg_view);
        mockEntranceSlider = findViewById(R.id.mockEntranceSlider);
        resultDetail = (LinearLayout) findViewById(R.id.result_view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public BaseSliderView getSlide(String image_url) {
        DefaultSliderView slide = new DefaultSliderView(this);
        slide.image(image_url);
        return slide;
    }
}
