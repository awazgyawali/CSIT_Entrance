package np.com.aawaz.csitentrance.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.CommentAdapter;

public class SinglePostViewer extends Fragment {

    RecyclerView CommentsRecyclerView;

    View view;
    ProgressBar progressBar;
    ArrayList<String> comments = new ArrayList<>(),
            commenter = new ArrayList<>(),
            commenterID = new ArrayList<>();
    String post, poster, posterID;
    boolean canLike, canComment, hasLiked;
    int likes, comment;

    TextView postView, PosterView, likeView, shareView;
    ProfilePictureView posterImage;
    EditText commentView;
    Button commentButton;
    View header, footer;
    LayoutInflater inflater;


    public SinglePostViewer() {
        // Required empty public constructor
    }

    private void inflateHeaderAndFooter() {
        inflater = LayoutInflater.from(getActivity());
        header = inflater.inflate(R.layout.single_feed_header, null, false);
        footer = inflater.inflate(R.layout.single_feed_footer, null, false);
        postView = (TextView) header.findViewById(R.id.post);
        PosterView = (TextView) header.findViewById(R.id.poster);
        likeView = (TextView) header.findViewById(R.id.likeText);
        shareView = (TextView) header.findViewById(R.id.shareText);
        posterImage = (ProfilePictureView) header.findViewById(R.id.posterPic);
        commentView = (EditText) footer.findViewById(R.id.commentText);
        commentButton = (Button) footer.findViewById(R.id.commentButton);
        readyCommentButton();
    }

    private void readyCommentButton() {
        commentView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0)
                    commentButton.setEnabled(false);
                else
                    commentButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                params.putString("message", commentView.getText().toString());
                new GraphRequest(AccessToken.getCurrentAccessToken(), getArguments().getString("postID"), params, HttpMethod.POST, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                    }
                }).executeAsync();
            }
        });
    }

    private void initializeView() {
        progressBar = (ProgressBar) view.findViewById(R.id.progressbarSingleFeed);
        CommentsRecyclerView = (RecyclerView) view.findViewById(R.id.commentsRecyOfFullPost);
        inflateHeaderAndFooter();
    }

    private void fillViews() {
        postView.setText(post);
        posterImage.setProfileId(posterID);
        PosterView.setText(poster);
        CommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CommentAdapter adapter = new CommentAdapter(getActivity(), commenter, comments, commenterID, header, footer);
        CommentsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeView();
        String requestId = getArguments().getString("postID");
        Bundle params = new Bundle();
        params.putString("fields", "comments.summary(true){message,from},message,likes.limit(0).summary(true),from");
        final GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), requestId, params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                FacebookRequestError error = graphResponse.getError();
                if (error != null) {
                } else {
                    try {
                        JSONObject object = graphResponse.getJSONObject().getJSONObject("comments");
                        JSONArray commentArray = object.getJSONArray("data");
                        for (int i = 0; i < commentArray.length(); i++) {
                            JSONObject commentObject = commentArray.getJSONObject(i);
                            comments.add(commentObject.getString("message"));
                            commenter.add(commentObject.getJSONObject("from").getString("name"));
                            commenterID.add(commentObject.getJSONObject("from").getString("id"));
                        }
                        comment = object.getJSONObject("summary").getInt("total_count");
                        canComment = object.getJSONObject("summary").getBoolean("can_comment");
                        post = graphResponse.getJSONObject().getString("message");
                        poster = graphResponse.getJSONObject().getJSONObject("from").getString("name");
                        posterID = graphResponse.getJSONObject().getJSONObject("from").getString("id");
                        likes = graphResponse.getJSONObject().getJSONObject("likes").getJSONObject("summary").getInt("total_count");
                        canLike = graphResponse.getJSONObject().getJSONObject("likes").getJSONObject("summary").getBoolean("can_like");
                        hasLiked = graphResponse.getJSONObject().getJSONObject("likes").getJSONObject("summary").getBoolean("has_liked");
                        progressBar.setVisibility(View.GONE);
                        fillViews();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        graphRequest.executeAsync();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_single_post_viewer, container, false);
        return view;
    }


}
