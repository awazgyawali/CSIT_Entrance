package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.CommentsActivity;
import np.com.aawaz.csitentrance.adapters.ForumAdapter;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.objects.Post;
import np.com.aawaz.csitentrance.objects.SPHandler;


public class EntranceForum extends Fragment implements ChildEventListener {

    RecyclerView recyclerView;

    AppCompatEditText questionEditText;
    ProgressBar progressBar;
    LinearLayout errorPart;
    ImageView fab;

    ForumAdapter adapter;

    private InputMethodManager imm;

    ArrayList<String> key = new ArrayList<>();

    DatabaseReference reference;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        errorPart = (LinearLayout) view.findViewById(R.id.errorPart);
        progressBar = (ProgressBar) view.findViewById(R.id.progressCircleFullFeed);
        recyclerView = (RecyclerView) view.findViewById(R.id.fullFeedRecycler);
        fab = (ImageView) view.findViewById(R.id.fabAddForum);
        questionEditText = (AppCompatEditText) view.findViewById(R.id.questionEditText);

        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0)
                    fab.setVisibility(View.GONE);
                else
                    fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        errorPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addListener();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(questionEditText.getWindowToken(), 0);
                sendPostRequestThroughGraph(questionEditText.getText().toString());
            }
        });
        fab.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("forum");
        super.onActivityCreated(savedInstanceState);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        fillRecyclerView();
        addListener();
    }

    private void addListener() {
        errorPart.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        reference.addChildEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Post newPost = dataSnapshot.getValue(Post.class);
        newPost.comment_count = (int) dataSnapshot.child("comments").getChildrenCount();
        adapter.addToTop(newPost);
        key.add(0, dataSnapshot.getKey());
        progressBar.setVisibility(View.GONE);
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
        Post post = dataSnapshot.getValue(Post.class);
        post.comment_count = (int) dataSnapshot.child("comments").getChildrenCount();
        adapter.editItemAtPosition(position, post);
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
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(getContext(), "Failed to load comments.",
                Toast.LENGTH_SHORT).show();
        errorPart.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void fillRecyclerView() {
        progressBar.setVisibility(View.GONE);
        adapter = new ForumAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                startActivity(new Intent(getContext(), CommentsActivity.class)
                        .putExtra("key", key.get(position))
                        .putExtra("comment_count", adapter.getCommentCount(position)));
            }

            @Override
            public void itemLongClicked(View view, final int position) {
                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(adapter.getUidAt(position))) {
                    new MaterialDialog.Builder(getContext())
                            .title("Select any option")
                            .items("Edit", "Delete")
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                    if (which == 0)
                                        showDialogToEdit(adapter.getMessageAt(position), position);
                                    else if (which == 1)
                                        FirebaseDatabase.getInstance().getReference().child("forum").child(key.get(position)).removeValue();
                                }
                            })
                            .build()
                            .show();
                }
            }
        });
    }

    private void showDialogToEdit(String message, final int position) {
        new MaterialDialog.Builder(getContext())
                .title("Edit post")
                .input("Your message", message, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("message", input.toString());
                        FirebaseDatabase.getInstance().getReference().child("forum").child(key.get(position)).updateChildren(map);
                    }
                })
                .positiveText("Save")
                .show();
    }


    private void sendPostRequestThroughGraph(final String message) {
        if (canPost()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            Post post = new Post(currentUser.getUid(), currentUser.getDisplayName(), new Date().getTime(), message, FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            Map<String, Object> postValues = post.toMap();

            reference.push().setValue(postValues);
            SPHandler.getInstance().setLastPostedTime(System.currentTimeMillis());
            sendNotificationToAllTheSubscribers(message);
            questionEditText.setText("");
        } else {
            new MaterialDialog.Builder(getContext())
                    .title("Opps...")
                    .content("You can ask one question per hour, please come back later. And don't worry your message will be stored in the editor.")
                    .show();
        }

    }

    private void sendNotificationToAllTheSubscribers(String string) {
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(FirebaseOptions.fromResource(getContext()).getGcmSenderId() + "@gcm.googleapis.com")
                .addData("fragment", "Result")
                .addData("title", "Entrance Forum")
                .addData("body", string)
                .addData("to","forum")
                .build());
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

    private boolean canPost() {
        long preTime = SPHandler.getInstance().getLastPostedTime();
        long postTime = System.currentTimeMillis();

        long difference = (postTime - preTime) / (60 * 60 * 1000);

        return difference >= 1;
    }
}