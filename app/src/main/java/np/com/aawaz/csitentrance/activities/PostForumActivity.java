package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.Post;
import np.com.aawaz.csitentrance.objects.SPHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostForumActivity extends AppCompatActivity {

    FancyButton buttonPost;
    AppCompatEditText questionEditText;
    private InputMethodManager imm;
    private DatabaseReference reference;
    OkHttpClient client;
    private CardView answerCard;
    private TextView yes, no, botAnswerHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new OkHttpClient();
        setContentView(R.layout.activity_post_forum);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }


        buttonPost = findViewById(R.id.postForum);
        questionEditText = findViewById(R.id.questionEditText);
        answerCard = findViewById(R.id.answerCard);
        botAnswerHolder = findViewById(R.id.botAnswerHolder);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("forum_data/posts");


        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                answerCard.setVisibility(View.GONE);
                if (charSequence.toString().trim().length() == 0)
                    buttonPost.setVisibility(View.GONE);
                else
                    buttonPost.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(questionEditText.getWindowToken(), 0);
                sendToAPI(questionEditText.getText().toString());
            }
        });
        buttonPost.setVisibility(View.GONE);

    }

    private void sendToAPI(final String message) {
        String url = "https://api.dialogflow.com/v1/query?v=20150910&lang=en&query=" + message + "&sessionId=12345&timezone=America/New_York";
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer 27067ba8b5a64ac3ab6ed72f1aabd3d3")
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PostForumActivity.this, "Seems like there is no internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                if (response.isSuccessful()) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject object = new JSONObject(response.body().string());
                                String intentName = object.getJSONObject("result").getJSONObject("metadata").getString("intentName");
                                if (intentName.equals("Default Fallback Intent")) {
                                    postToFirebase(message);
                                } else {
                                    botAnswerHolder.setText(object.getJSONObject("result").getJSONObject("fulfillment").getString("speech"));
                                    answerCard.setVisibility(View.VISIBLE);
                                    yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    });
                                    no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            postToFirebase(message);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                postToFirebase(message);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        SPHandler.getInstance().setForumText(questionEditText.getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        questionEditText.setText(SPHandler.getInstance().getForumText());
    }

    private void postToFirebase(final String message) {
        if (canPost()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Post post = new Post(currentUser.getUid(), currentUser.getDisplayName(), new Date().getTime(), message, FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            Map<String, Object> postValues = post.toMap();
            reference.push().setValue(postValues);
            SPHandler.getInstance().setLastPostedTime(System.currentTimeMillis());
            questionEditText.setText("");
            setResult(200, null);
            finish();
        } else {
            new MaterialDialog.Builder(this)
                    .title("Ops...")
                    .content("You can only ask one question per hour, please try after " + getRemainingTime() + ".")
                    .show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private boolean canPost() {

        //todo change in the release
        long preTime = SPHandler.getInstance().getLastPostedTime();
        long postTime = System.currentTimeMillis();

        long difference = (postTime - preTime) / (60 * 60 * 1000);

        return difference >= 1;
    }

    public String getRemainingTime() {
        long timeInMilis = (SPHandler.getInstance().getLastPostedTime() + 1800000) - System.currentTimeMillis();
        return timeInMilis / (1000 * 60) + " minutes";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


}
