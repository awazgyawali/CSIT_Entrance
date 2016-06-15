package np.com.aawaz.csitentrance.fragments.other_fragments;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.devspark.robototextview.widget.RobotoTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.CommentAdapter;
import np.com.aawaz.csitentrance.objects.Comment;

public class CommentsFragment extends BottomSheetDialogFragment {

    RecyclerView commentsRecyclerView;
    ProgressBar progressBar;

    AppCompatEditText commentEditText;
    ImageButton commentButton;
    CircleImageView profileImage;
    RobotoTextView commentNumber, errorMessage;
    LinearLayout commentAdder;
    CommentAdapter adapter;
    ArrayList<String> key = new ArrayList<>();

    DatabaseReference reference;

    public CommentsFragment() {
        // Required empty public constructor
    }

    private void readyCommentButton() {
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
                sendPostRequestThroughGraph(commentEditText.getText().toString());
            }
        });
    }

    private void sendPostRequestThroughGraph(final String message) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Comment comment = new Comment(currentUser.getUid(), currentUser.getDisplayName(), System.currentTimeMillis(), message, currentUser.getPhotoUrl().toString());
        Map<String, Object> postValues = comment.toMap();

        reference.push().setValue(postValues);
        commentEditText.setText("");
    }

    public void onViewCreated(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressbarSingleFeed);
        commentsRecyclerView = (RecyclerView) view.findViewById(R.id.commentsRecyOfFullPost);
        commentEditText = (AppCompatEditText) view.findViewById(R.id.addCommentText);
        commentButton = (ImageButton) view.findViewById(R.id.commentButton);
        profileImage = (CircleImageView) view.findViewById(R.id.profileImage);
        commentNumber = (RobotoTextView) view.findViewById(R.id.numberComments);
        errorMessage = (RobotoTextView) view.findViewById(R.id.errorComment);
        commentAdder = (LinearLayout) view.findViewById(R.id.commentAdder);
        errorMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        errorMessage.setVisibility(View.GONE);
    }

    private void fillViews() {
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CommentAdapter(getActivity());
        commentsRecyclerView.setAdapter(adapter);
        readyCommentButton();
        addListener();
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

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_comments, null);
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();

        reference = rootReference.child("forum").child(getArguments().getString("key")).child("comments");

        dialog.setContentView(contentView);
        onViewCreated(contentView);
        fillViews();

        if (getArguments().getInt("comment_count") == 0) {
            progressBar.setVisibility(View.GONE);
            errorMessage.setText("No Comments");
            errorMessage.setVisibility(View.VISIBLE);
        }


        commentNumber.setText(getArguments().getInt("comment_count") + " comments");

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        Picasso.with(getContext())
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .into(profileImage);
    }
}
