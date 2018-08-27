package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.CommentAdapter;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.MyApplication;
import np.com.aawaz.csitentrance.misc.Singleton;
import np.com.aawaz.csitentrance.objects.Comment;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.Post;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class CommentsActivity extends AppCompatActivity implements ChildEventListener {

    RecyclerView commentsRecyclerView;
    ProgressBar progressBar;

    AppCompatEditText commentEditText;
    ImageButton commentButton;
    CircleImageView forumPic;
    TextView commentNumber, errorMessage, postedBy, forumTime, realPost, upVoteButton;
    LinearLayout commentAdder;
    CommentAdapter adapter;
    ArrayList<String> key = new ArrayList<>();

    DatabaseReference reference;

    private String mUrl;
    private String mTitle;
    private ConstraintLayout forumContainer;

    public CommentsActivity() {
        // Required empty public constructor
    }

    private void readyCommentButton() {
        new EventSender().logEvent("opened_comments");

        commentEditText.addTextChangedListener(new TextWatcher() {
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
                if (commentEditText.getText().toString().length() > 0)
                    sendPostRequestThroughGraph(commentEditText.getText().toString());
                else
                    Toast.makeText(CommentsActivity.this, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void appIndexing() {
        mUrl = "http://csitentrance.brainants.com/forum";
        mTitle = getIntent().getStringExtra("message");
    }

    public Action getAction() {
        return Actions.newView(mTitle, mUrl);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    protected void onStop() {
        FirebaseUserActions.getInstance().end(getAction());
        reference.removeEventListener(this);
        super.onStop();
    }

    private void sendPostRequestThroughGraph(final String message) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Comment comment = new Comment(currentUser.getUid(), currentUser.getDisplayName(), System.currentTimeMillis(), message, currentUser.getPhotoUrl().toString());
        Map<String, Object> postValues = comment.toMap();

        reference.push().setValue(postValues);
        increaseCommentCount();
        commentEditText.setText("");
        commentsRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void increaseCommentCount() {
        FirebaseDatabase.getInstance().getReference().child("forum_data/posts/" + getIntent().getStringExtra("key") + "/comment_count").setValue(adapter.getItemCount() + 1);
    }

    private void decreaseCommentCount() {
        FirebaseDatabase.getInstance().getReference().child("forum_data/posts/" + getIntent().getStringExtra("key") + "/comment_count").setValue(adapter.getItemCount() - 1);
    }

    public void readyViews() {
        forumContainer = findViewById(R.id.postContainer);
        forumPic = findViewById(R.id.forumPic);
        postedBy = findViewById(R.id.postedBy);
        forumTime = findViewById(R.id.forumTime);
        realPost = findViewById(R.id.realPost);
        upVoteButton = findViewById(R.id.upvoteButton);

        progressBar = findViewById(R.id.progressbarSingleFeed);
        commentsRecyclerView = findViewById(R.id.commentsRecyOfFullPost);
        commentEditText = findViewById(R.id.addCommentText);
        commentButton = findViewById(R.id.commentButton);
        commentNumber = findViewById(R.id.numberComments);
        commentNumber.setPaintFlags(commentNumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        errorMessage = findViewById(R.id.errorComment);
        commentAdder = findViewById(R.id.commentAdder);
        errorMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        errorMessage.setVisibility(View.GONE);
        upVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void fillViews() {
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(this);
        commentsRecyclerView.setAdapter(adapter);
        adapter.setClickEventListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {

            }

            @Override
            public void itemLongClicked(View view, final int position) {

                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(adapter.getUidAt(position)) || SPHandler.containsDevUID(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    new MaterialDialog.Builder(CommentsActivity.this)
                            .title("Select any option")
                            .items("Edit", "Delete")
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                    if (which == 0)
                                        showDialogToEdit(adapter.getMessageAt(position), position);
                                    else if (which == 1) {
                                        reference.child(key.get(position)).removeValue();
                                        decreaseCommentCount();
                                    }
                                }
                            })
                            .build()
                            .show();
                }
            }

            @Override
            public void upVoteClicked(View view, final int position) {
                Singleton.getInstance().upvoteComment(getIntent().getStringExtra("key"), key.get(position), FirebaseAuth.getInstance().getCurrentUser().getUid(), adapter.getUidAt(position), adapter.haveIVoted(position));
                adapter.upVoteAtPosition(position);
            }
        });

        readyCommentButton();

        addListener();

    }

    private void showDialogToEdit(String message, final int position) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Edit post")
                .input("Your message", message, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("message", input.toString());
                        reference.child(key.get(position)).updateChildren(map);
                    }
                })
                .positiveText("Save")
                .build();

        dialog.getInputEditText().setLines(5);
        dialog.getInputEditText().setSingleLine(false);
        dialog.getInputEditText().setMaxLines(7);
        dialog.show();
    }


    private void addListener() {
        progressBar.setVisibility(View.VISIBLE);
        reference.addChildEventListener(this);
    }

    private void maintainCommentNo() {
        commentNumber.setText(adapter.getItemCount() + getCommentMessage());
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Comment newPost = dataSnapshot.getValue(Comment.class);
        adapter.addToTop(newPost);
        key.add(dataSnapshot.getKey());
        progressBar.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        maintainCommentNo();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        int position = key.indexOf(dataSnapshot.getKey());
        adapter.editItemAtPosition(position, dataSnapshot.getValue(Comment.class));
        maintainCommentNo();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        int position = key.indexOf(dataSnapshot.getKey());
        adapter.removeItemAtPosition(position);
        key.remove(position);
        maintainCommentNo();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        progressBar.setVisibility(View.GONE);
        errorMessage.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        reference = rootReference.child("forum_data/comments").child(getIntent().getStringExtra("key"));

        readyViews();

        fillPost();

        fillViews();

        if (getIntent().getIntExtra("comment_count", 0) == 0) {
            progressBar.setVisibility(View.GONE);
            errorMessage.setText("No Comments");
            errorMessage.setVisibility(View.VISIBLE);
        }


        commentNumber.setText(getIntent().getIntExtra("comment_count", 0) + " comments");

        appIndexing();
    }

    private void fillPost() {
        FirebaseDatabase.getInstance().getReference().child("forum_data/posts").child(getIntent().getStringExtra("key")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                forumContainer.setVisibility(View.VISIBLE);

                final Post post = dataSnapshot.getValue(Post.class);
                if (post!= null && post.author == null) {
                    Toast.makeText(CommentsActivity.this, "The post has been deleted.", Toast.LENGTH_SHORT).show();
                    return;
                }
                postedBy.setText(post.author + " ");
                realPost.setText(post.message);
                forumTime.setText(getRelativeTimeSpanString(post.time_stamp));
                forumPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(CommentsActivity.this, ProfileActivity.class).putExtra("uid", post.uid));
                    }
                });

                if (post.likes != null) {
                    upVoteButton.setText(String.valueOf(post.likes.size()));
                    if (post.likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        upVoteButton.setTextColor(ContextCompat.getColor(CommentsActivity.this, R.color.colorAccent));
                        upVoteButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.upvote_accent, 0);
                    } else {
                        upVoteButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.upvote_grey, 0);
                        upVoteButton.setTextColor(ContextCompat.getColor(CommentsActivity.this, R.color.grey));
                    }
                } else {
                    post.likes = new ArrayList<>();
                    upVoteButton.setText("0");
                    upVoteButton.setTextColor(ContextCompat.getColor(CommentsActivity.this, R.color.grey));
                    upVoteButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.upvote_grey, 0);
                }

                if (SPHandler.containsDevUID(post.uid))
                    postedBy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.admin, 0);
                else
                    postedBy.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


                try {
                    Picasso.with(MyApplication.getAppContext())
                            .load(post.image_url)
                            .error(TextDrawable.builder().buildRound(String.valueOf(post.author.charAt(0)).toUpperCase(), Color.BLUE))
                            .placeholder(TextDrawable.builder().buildRound(String.valueOf(post.author.charAt(0)).toUpperCase(), Color.BLUE))
                            .into(forumPic);
                } catch (Exception e) {
                    forumPic.setImageDrawable(TextDrawable.builder().buildRound(String.valueOf(post.author.charAt(0)).toUpperCase(), Color.BLUE));
                }
                upVoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Singleton.getInstance().upvoteAPost(dataSnapshot.getKey(), FirebaseAuth.getInstance().getCurrentUser().getUid(), post.uid, post.likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()));
//todo lcally chage garera jhukkaune
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private CharSequence getRelativeTimeSpanString(long time_stamp) {
        if (time_stamp > System.currentTimeMillis())
            return "PINNED";
        return DateUtils.getRelativeTimeSpanString(time_stamp, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
    }

    public String getCommentMessage() {
        return adapter.getItemCount() == 1 ? " comment" : " comments";
    }
}
