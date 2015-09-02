package np.com.aawaz.csitentrance.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
    boolean hasLiked;
    int likes, comment;
    TextView postView, PosterView, likeView, commentCountView, liked;
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
        liked = (TextView) header.findViewById(R.id.likeText);
        PosterView = (TextView) header.findViewById(R.id.poster);
        likeView = (TextView) header.findViewById(R.id.likeCount);
        commentCountView = (TextView) header.findViewById(R.id.commentCount);
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
            public void onClick(final View views) {
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .progress(true, 0)
                        .content("Posting....")
                        .build();
                dialog.show();
                Bundle params = new Bundle();
                params.putString("message", commentView.getText().toString());
                new GraphRequest(AccessToken.getCurrentAccessToken(), getArguments().getString("postID") + "/comments", params, HttpMethod.POST, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        dialog.dismiss();
                        if (graphResponse.getError() != null)
                            Snackbar.make(view.findViewById(R.id.singlePostCore), "Unable to comment.", Snackbar.LENGTH_SHORT).show();
                        else {
                            commentView.setText("");
                            commentCountView.setText((comment + 1) + " comments");
                            Snackbar.make(view.findViewById(R.id.singlePostCore), "Commented successfully.", Snackbar.LENGTH_SHORT).show();
                            onResume();
                        }
                    }
                }).executeAsync();
            }
        });
    }

    private void initializeView() {
        progressBar = (ProgressBar) view.findViewById(R.id.progressbarSingleFeed);
        progressBar.bringToFront();
        CommentsRecyclerView = (RecyclerView) view.findViewById(R.id.commentsRecyOfFullPost);
        inflateHeaderAndFooter();
    }

    private void fillViews() {
        postView.setText(post);
        posterImage.setProfileId(posterID);
        PosterView.setText(poster);
        if (hasLiked)
            liked.setText("Liked");
        likeView.setText(likes + " likes");
        commentCountView.setText(comment + " comments");
        CommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CommentAdapter adapter = new CommentAdapter(getActivity(), commenter, comments, commenterID, header, footer);
        CommentsRecyclerView.setAdapter(adapter);
        liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasLiked) {
                    liked.setText("Like");
                    likeView.setText((likes - 1) + " likes");
                } else {
                    liked.setText("Liked");
                    likeView.setText((likes + 1) + " likes");
                }
                hasLiked = !hasLiked;
                new GraphRequest(AccessToken.getCurrentAccessToken(), getArguments().getString("postID") + "/likes", new Bundle(),
                        hasLiked ? HttpMethod.DELETE : HttpMethod.POST, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                    }
                }).executeAsync();
            }
        });
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
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
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
                        post = graphResponse.getJSONObject().getString("message");
                        poster = graphResponse.getJSONObject().getJSONObject("from").getString("name");
                        posterID = graphResponse.getJSONObject().getJSONObject("from").getString("id");
                        likes = graphResponse.getJSONObject().getJSONObject("likes").getJSONObject("summary").getInt("total_count");
                        hasLiked = graphResponse.getJSONObject().getJSONObject("likes").getJSONObject("summary").getBoolean("has_liked");
                        progressBar.setVisibility(View.GONE);
                        fillViews();
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
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
