package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import np.com.aawaz.csitentrance.objects.Post;
import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.ForumAdapter;
import np.com.aawaz.csitentrance.fragments.other_fragments.CommentsFragment;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.misc.SPHandler;


public class EntranceForum extends Fragment {

    private static final String TAG = "DEbug";
    RecyclerView recyclerView;

    AppCompatEditText questionEditText;
    ProgressBar progressBar;
    LinearLayout errorPart;
    FloatingActionButton fab;

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
        fab = (FloatingActionButton) view.findViewById(R.id.fabAddForum);
        questionEditText = (AppCompatEditText) view.findViewById(R.id.questionEditText);

        fab.hide();

        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0)
                    fab.hide();
                else
                    fab.show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        errorPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorPart.setVisibility(View.GONE);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(questionEditText.getWindowToken(), 0);
                sendPostRequestThroughGraph(questionEditText.getText().toString());
            }
        });
        fab.hide();
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
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Post newPost = dataSnapshot.getValue(Post.class);
                adapter.addToTop(newPost);
                key.add(0,dataSnapshot.getKey());
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
                adapter.editItemAtPosition(position, dataSnapshot.getValue(Post.class));
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
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getContext(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillRecyclerView() {
        progressBar.setVisibility(View.GONE);
        adapter = new ForumAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setClickListener(new ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new CommentsFragment();
                //pathaune ho comments haru
                bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
    }


    private void sendPostRequestThroughGraph(final String message) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Post post = new Post(currentUser.getUid(), currentUser.getDisplayName(), new Date().getTime(), message);
        Map<String, Object> postValues = post.toMap();

        reference.push().setValue(postValues);
    }
}