package np.com.aawaz.csitentrance.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.CommentAdapter;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.objects.Comment;
import np.com.aawaz.csitentrance.objects.EventSender;

public class CommentsActivity extends AppCompatActivity {

    RecyclerView commentsRecyclerView;
    ProgressBar progressBar;

    AppCompatEditText commentEditText;
    ImageButton commentButton;
    CircleImageView profileImage;
    TextView commentNumber, errorMessage;
    LinearLayout commentAdder;
    CommentAdapter adapter;
    ArrayList<String> key = new ArrayList<>();

    DatabaseReference reference;

    private String mUrl;
    private String mTitle;

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
        super.onStop();
    }

    private void sendPostRequestThroughGraph(final String message) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Comment comment = new Comment(currentUser.getUid(), currentUser.getDisplayName(), System.currentTimeMillis(), message, currentUser.getPhotoUrl().toString());
        Map<String, Object> postValues = comment.toMap();

        reference.push().setValue(postValues);
        commentEditText.setText("");
    }

    public void readyViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressbarSingleFeed);
        commentsRecyclerView = (RecyclerView) findViewById(R.id.commentsRecyOfFullPost);
        commentEditText = (AppCompatEditText) findViewById(R.id.addCommentText);
        commentButton = (ImageButton) findViewById(R.id.commentButton);
        profileImage = (CircleImageView) findViewById(R.id.profileImage);
        commentNumber = (TextView) findViewById(R.id.numberComments);
        errorMessage = (TextView) findViewById(R.id.errorComment);
        commentAdder = (LinearLayout) findViewById(R.id.commentAdder);
        errorMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        errorMessage.setVisibility(View.GONE);
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

                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(adapter.getUidAt(position))) {
                    new MaterialDialog.Builder(CommentsActivity.this)
                            .title("Select any option")
                            .items("Edit", "Delete")
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                    if (which == 0)
                                        showDialogToEdit(adapter.getMessageAt(position), position);
                                    else if (which == 1)
                                        reference.child(key.get(position)).removeValue();
                                }
                            })
                            .build()
                            .show();
                }
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
        dialog.getInputEditText().setMaxLines(7);
        dialog.show();
    }


    private void addListener() {
        progressBar.setVisibility(View.VISIBLE);
        reference.addChildEventListener(new ChildEventListener() {
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
                int position = -1;
                for (int i = 0; i < key.size(); i++) {
                    if (key.get(i).equals(dataSnapshot.getKey()))
                        position = i;
                }
                if (position == -1)
                    return;
                adapter.editItemAtPosition(position, dataSnapshot.getValue(Comment.class));
                maintainCommentNo();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int position = -1;
                for (int i = 0; i < key.size(); i++) {
                    if (key.get(i).equals(dataSnapshot.getKey()))
                        position = i;
                }
                if (position == -1)
                    return;
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
        });
    }

    private void maintainCommentNo() {
        commentNumber.setText(adapter.getItemCount() + " comments");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        reference = rootReference.child("forum").child(getIntent().getStringExtra("key")).child("comments");

        readyViews();

        fillViews();

        if (getIntent().getIntExtra("comment_count", 0) == 0) {
            progressBar.setVisibility(View.GONE);
            errorMessage.setText("No Comments");
            errorMessage.setVisibility(View.VISIBLE);
        }


        commentNumber.setText(getIntent().getIntExtra("comment_count", 0) + " comments");

        Picasso.with(this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .into(profileImage);
        appIndexing();
    }
}
