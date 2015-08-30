package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.GraphFeedAdapter;
import np.com.aawaz.csitentrance.advance.MyApplication;
import np.com.aawaz.csitentrance.fragments.SinglePostViewer;


public class CSITQuery extends AppCompatActivity {

    CallbackManager callbackManager;
    LoginButton button;
    String id;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    View header;
    ArrayList<String> poster = new ArrayList<>(),
            messages = new ArrayList<>(),
            likes = new ArrayList<>(),
            postId = new ArrayList<>(),
            comments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csitquery);
        setSupportActionBar((Toolbar) findViewById(R.id.queryToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        MyApplication.changeStatusBarColor(R.color.status_bar_query, this);
        button = (LoginButton) findViewById(R.id.fbLoginButton);
        progressBar = (ProgressBar) findViewById(R.id.progressCircleFullFeed);
        button.setReadPermissions(Arrays.asList("user_status", "publish_actions"));

        loadAd();

        firstLoginPage();

        try {
            if (!AccessToken.getCurrentAccessToken().isExpired()) {
                RelativeLayout loginLayout;
                loginLayout = (RelativeLayout) findViewById(R.id.firstLogin);
                loginLayout.setVisibility(View.GONE);
                debugDataAdder();
            } else {
                LinearLayout loginLayout = (LinearLayout) findViewById(R.id.reqularFeed);
                loginLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void fillRecy() {
        progressBar.setVisibility(View.GONE);
        GraphFeedAdapter adapter = new GraphFeedAdapter(this, poster, messages, likes, comments, postId, header);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setClickListener(new GraphFeedAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                if (position == 0)
                    return;
                position--;
                SinglePostViewer frag = new SinglePostViewer();
                Bundle bundle = new Bundle();
                bundle.putString("postID", postId.get(position));
                frag.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.queryFragHoler, frag, "fullfeed");
                transaction.commit();
            }
        });
    }

    private void debugDataAdder() {
        recyclerView = (RecyclerView) findViewById(R.id.fullFeedRecycler);
        header = View.inflate(this, R.layout.post_feed_header, null);
        handelHeader();
        final String requestId = "CSITentrance/feed";
        Bundle params = new Bundle();
        params.putString("fields", "message,likes.limit(0).summary(true),comments.limit(0).summary(true),from");
        final GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), requestId, params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                FacebookRequestError error = graphResponse.getError();
                if (error != null) {
                } else {
                    try {
                        JSONArray array = graphResponse.getJSONObject().getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject arrayItem = array.getJSONObject(i);
                            messages.add(arrayItem.getString("message"));
                            postId.add(arrayItem.getString("id"));
                            likes.add(arrayItem.getJSONObject("likes").getJSONObject("summary").getInt("total_count") + "");
                            comments.add(arrayItem.getJSONObject("comments").getJSONObject("summary").getInt("total_count") + "");
                            poster.add(arrayItem.getJSONObject("from").getString("name"));
                            fillRecy();
                        }
                    } catch (JSONException ignored) {
                    }
                }
            }
        });
        graphRequest.executeAsync();

    }

    private void handelHeader() {
        ProfilePictureView profPic = (ProfilePictureView) header.findViewById(R.id.user_profpic);
        final AppCompatEditText postText = (AppCompatEditText) header.findViewById(R.id.userPostText);
        final FancyButton postButton = (FancyButton) header.findViewById(R.id.user_post_button);
        try {
            profPic.setProfileId(Profile.getCurrentProfile().getId());
        } catch (Exception ignored) {
        }
        postButton.setEnabled(false);
        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    postButton.setEnabled(true);
                else postButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPostRequestThroughGraph(postText.getText().toString());
            }
        });
    }

    private void sendPostRequestThroughGraph(String message) {
        Bundle params = new Bundle();
        params.putString("message", message);
        final String requestId = "CSITentrance/feed";
        new GraphRequest(AccessToken.getCurrentAccessToken(), requestId, params, HttpMethod.POST, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
            }
        }).executeAsync();
    }

    private void firstLoginPage() {

        callbackManager = CallbackManager.Factory.create();

        button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                debugDataAdder();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void loadAd() {
        final AdView mAdView = (AdView) findViewById(R.id.QueryAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("fullfeed")).commit();
        } catch (Exception e) {
            super.onBackPressed();
            finish();
        }
    }
}