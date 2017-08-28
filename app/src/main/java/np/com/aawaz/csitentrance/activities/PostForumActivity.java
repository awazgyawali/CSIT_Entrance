package np.com.aawaz.csitentrance.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Map;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.Post;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class PostForumActivity extends AppCompatActivity {

    ImageView buttonPost;
    AppCompatEditText questionEditText;
    private InputMethodManager imm;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_forum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }


        buttonPost = (ImageView) findViewById(R.id.postForum);
        questionEditText = (AppCompatEditText) findViewById(R.id.questionEditText);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("forum");

        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0)
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
                postToFirebase(questionEditText.getText().toString());
            }
        });
        buttonPost.setVisibility(View.GONE);

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
            finish();
            SPHandler.getInstance().setLastPostedTime(System.currentTimeMillis());
            questionEditText.setText("");
        } else {
            new MaterialDialog.Builder(this)
                    .title("Opps...")
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
        long preTime = SPHandler.getInstance().getLastPostedTime();
        long postTime = System.currentTimeMillis();

        long difference = (postTime - preTime) / (60 * 60 * 1000);

        return difference >= 1;
    }

    public String getRemainingTime() {
        long timeInMilis = (SPHandler.getInstance().getLastPostedTime() + 3600000) - System.currentTimeMillis();
        return timeInMilis / (1000 * 60) + " minutes";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


}
