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
import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.CommentAdapter;

public class SinglePostViewer extends Fragment {

    RecyclerView CommentsRecyclerView;
    View view;
    MaterialDialog dialog;
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

    public void ReadyDialog() {
        dialog = new MaterialDialog.Builder(getActivity())
                .progress(true, 0)
                .content("Posting....")
                .cancelable(false)
                .build();
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
                dialog.show();
                Bundle params = new Bundle();
                params.putString("message", commentView.getText().toString());
                new GraphRequest(AccessToken.getCurrentAccessToken(), getArguments().getString("postID") + "/comments", params, HttpMethod.POST, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        if (graphResponse.getError() != null) {
                            Snackbar.make(view.findViewById(R.id.singlePostCore), "Unable to comment.", Snackbar.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            commentView.setText("");
                            commentCountView.setText((comment + 1) + " comments");
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

    private void fillViews() throws Exception {
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
                    likes--;
                    likeView.setText(likes + " likes");
                } else {
                    likes++;
                    liked.setText("Liked");
                    likeView.setText(likes + " likes");
                }
                new GraphRequest(AccessToken.getCurrentAccessToken(), getArguments().getString("postID") + "/likes", new Bundle(),
                        hasLiked ? HttpMethod.DELETE : HttpMethod.POST, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {

                    }
                }).executeAsync();
                hasLiked = !hasLiked;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeView();
        ReadyDialog();
        String requestId = getArguments().getString("postID");
        Bundle params = new Bundle();
        commenter.clear();
        commenterID.clear();
        comments.clear();
        params.putString("fields", "comments.summary(true){message,from},message,story,likes.limit(0).summary(true),from");
        final GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), requestId, params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                try {
                    FacebookRequestError error = graphResponse.getError();
                    dialog.dismiss();
                if (error != null) {
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                } else {
                        JSONObject object = graphResponse.getJSONObject().getJSONObject("comments");
                        JSONArray commentArray = object.getJSONArray("data");
                        for (int i = 0; i < commentArray.length(); i++) {
                            JSONObject commentObject = commentArray.getJSONObject(i);
                            comments.add(commentObject.getString("message"));
                            commenter.add(commentObject.getJSONObject("from").getString("name"));
                            commenterID.add(commentObject.getJSONObject("from").getString("id"));
                        }
                        comment = object.getJSONObject("summary").getInt("total_count");
                        try {
                            post = graphResponse.getJSONObject().getString("message");
                        } catch (Exception e){
                            post = graphResponse.getJSONObject().getString("story");
                        }
                        poster = graphResponse.getJSONObject().getJSONObject("from").getString("name");
                        posterID = graphResponse.getJSONObject().getJSONObject("from").getString("id");
                        likes = graphResponse.getJSONObject().getJSONObject("likes").getJSONObject("summary").getInt("total_count");
                        hasLiked = graphResponse.getJSONObject().getJSONObject("likes").getJSONObject("summary").getBoolean("has_liked");
                        progressBar.setVisibility(View.GONE);
                        fillViews();
                }
                } catch (Exception ignored) {
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
