package np.com.aawaz.csitentrance.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.GraphFeedAdapter;


public class EntranceForum extends Fragment {

    CallbackManager callbackManager;
    LoginButton button;
    String id;
    RecyclerView recyclerView;
    RelativeLayout intro;
    ProgressBar progressBar;
    LinearLayout errorPart;
    View header;
    MaterialDialog dialog;
    AppCompatEditText postText;
    ArrayList<String> poster = new ArrayList<>(),
            messages = new ArrayList<>(),
            likes = new ArrayList<>(),
            postId = new ArrayList<>(),
            comments = new ArrayList<>();
    private RelativeLayout mainLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainLayout = (RelativeLayout) view.findViewById(R.id.forum_main);
        errorPart = (LinearLayout) view.findViewById(R.id.errorPart);
        button = (LoginButton) view.findViewById(R.id.fbLoginButton);
        intro = (RelativeLayout) view.findViewById(R.id.introForum);
        progressBar = (ProgressBar) view.findViewById(R.id.progressCircleFullFeed);
        recyclerView = (RecyclerView) view.findViewById(R.id.fullFeedRecycler);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ReadyDialog();
        errorPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debugDataAdder();
                errorPart.setVisibility(View.GONE);
            }
        });
        button.setPublishPermissions("publish_actions");

        firstLoginPage();
        if (first()) {
            LoginManager.getInstance().logOut();
            getContext().getSharedPreferences("values", Context.MODE_PRIVATE).edit().putBoolean("postPermission", false).apply();
        }


        try {
            if (!AccessToken.getCurrentAccessToken().isExpired()) {
                button.setVisibility(View.GONE);
                intro.setVisibility(View.GONE);
                debugDataAdder();
            }
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean first() {
        return getContext().getSharedPreferences("values", Context.MODE_PRIVATE).getBoolean("postPermission", true);
    }


    private void fillRecy() throws Exception {
        progressBar.setVisibility(View.GONE);
        GraphFeedAdapter adapter = new GraphFeedAdapter(getContext(), poster, messages, likes, comments, postId, header);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.queryFragHoler, frag, "fullfeed");
                transaction.commit();
            }
        });
    }

    private void debugDataAdder() {
        progressBar.setVisibility(View.VISIBLE);
        header = View.inflate(getContext(), R.layout.post_feed_header, null);
        handelHeader();
        final String requestId = "CSITentrance/feed";
        Bundle params = new Bundle();
        params.putString("fields", "message,story,likes.limit(0).summary(true),comments.limit(0).summary(true),from,created_time");
        final GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), requestId, params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                try {
                    FacebookRequestError error = graphResponse.getError();
                    dialog.dismiss();
                    if (error != null) {
                        errorPart.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        messages.clear();
                        poster.clear();
                        postId.clear();
                        likes.clear();
                        comments.clear();

                        JSONArray array = graphResponse.getJSONObject().getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject arrayItem = array.getJSONObject(i);
                            if (!arrayItem.getJSONObject("from").getString("name").equals("CSIT Entrance"))
                                try {
                                    messages.add(arrayItem.getString("message"));
                                    postId.add(arrayItem.getString("id"));
                                    likes.add(arrayItem.getJSONObject("likes").getJSONObject("summary").getInt("total_count") + "");
                                    comments.add(arrayItem.getJSONObject("comments").getJSONObject("summary").getInt("total_count") + "");
                                    poster.add(arrayItem.getJSONObject("from").getString("name"));
                                } catch (Exception ignored) {
                                }
                        }
                        fillRecy();

                    }
                } catch (Exception ignored) {
                }
            }
        });
        graphRequest.executeAsync();
    }

    private void handelHeader() {
        ProfilePictureView profPic = (ProfilePictureView) header.findViewById(R.id.user_profpic);
        postText = (AppCompatEditText) header.findViewById(R.id.userPostText);
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

    public void ReadyDialog() {
        dialog = new MaterialDialog.Builder(getContext())
                .progress(true, 0)
                .content("Posting....")
                .cancelable(false)
                .build();
    }

    private void sendPostRequestThroughGraph(String message) {
        dialog.show();
        Bundle params = new Bundle();
        params.putString("message", message);
        final String requestId = "CSITentrance/feed";
        new GraphRequest(AccessToken.getCurrentAccessToken(), requestId, params, HttpMethod.POST, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                if (graphResponse.getError() != null) {
                    dialog.dismiss();
                    Snackbar.make(mainLayout, "Unable to post.", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mainLayout, "Post successful.", Snackbar.LENGTH_SHORT).show();
                    debugDataAdder();
                    postText.setText("");
                }
            }
        }).executeAsync();
    }

    private void firstLoginPage() {

        callbackManager = CallbackManager.Factory.create();

        button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                debugDataAdder();
                button.setVisibility(View.GONE);
                intro.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}