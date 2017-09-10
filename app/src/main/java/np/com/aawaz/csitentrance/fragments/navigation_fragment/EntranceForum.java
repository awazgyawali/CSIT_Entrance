package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.CommentsActivity;
import np.com.aawaz.csitentrance.activities.MainActivity;
import np.com.aawaz.csitentrance.activities.PostForumActivity;
import np.com.aawaz.csitentrance.adapters.ForumAdapter;
import np.com.aawaz.csitentrance.fragments.other_fragments.ACHSDialog;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.Post;
import np.com.aawaz.csitentrance.objects.SPHandler;


public class EntranceForum extends Fragment implements ValueEventListener, ClickListener {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    ProgressBar progressBar;
    LinearLayout errorPart;

    ForumAdapter adapter;
    ArrayList<String> key = new ArrayList<>();
    DatabaseReference reference;
    FloatingActionButton floatingActionButton;
    ImageView callAchs;
    ConstraintLayout achsAd;
    private LinearLayoutManager mLinearLayoutManager;

    public static Fragment newInstance(String post_id) {
        EntranceForum forum = new EntranceForum();
        Bundle args = new Bundle();
        args.putString("post_id", post_id);
        forum.setArguments(args);
        return forum;
    }

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
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.forumSwipeRefresh);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fabForumPost);
        callAchs = (ImageView) view.findViewById(R.id.callACHS);
        achsAd = (ConstraintLayout) view.findViewById(R.id.forum_ad);
        new EventSender().logEvent("achs_ad");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                addListener();
            }
        });

        achsAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ACHSDialog dialog = new ACHSDialog();
                dialog.show(getChildFragmentManager(), "achs");
                new EventSender().logEvent("achs_full_ad");
            }
        });

        callAchs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "01-4436383", null)));
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PostForumActivity.class));
            }
        });
        errorPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addListener();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("forum");
        super.onActivityCreated(savedInstanceState);
        addListener();
        handleIntent();
    }

    private void handleIntent() {
        String post_id = getArguments().getString("post_id");
        if (post_id != null && !MainActivity.openedIntent) {
            startActivity(new Intent(getContext(), CommentsActivity.class)
                    .putExtra("key", post_id)
                    .putExtra("message", "Entrance Forum"));
            MainActivity.openedIntent = true;
        }
    }

    private void addListener() {
        recyclerView.setVisibility(View.GONE);
        errorPart.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        reference.limitToLast(50).addListenerForSingleValueEvent(this);
    }


    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Post newPost = dataSnapshot.getValue(Post.class);
        if (newPost.author == null)
            return;
        newPost.comment_count = (int) dataSnapshot.child("comments").getChildrenCount();
        adapter.addToTop(newPost);
        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
        key.add(0, dataSnapshot.getKey());
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnap) {
        fillRecyclerView();
        adapter.notifyDataSetChanged();
        key.clear();
        progressBar.setVisibility(View.GONE);
        for (DataSnapshot dataSnapshot : dataSnap.getChildren()) {
            Post newPost = dataSnapshot.getValue(Post.class);
            if (newPost.author == null)
                return;
            newPost.comment_count = (int) dataSnapshot.child("comments").getChildrenCount();
            adapter.addToTop(newPost);
            key.add(0, dataSnapshot.getKey());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(getContext(), "Failed to load posts.",
                Toast.LENGTH_SHORT).show();
        errorPart.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void fillRecyclerView() {
        progressBar.setVisibility(View.GONE);
        adapter = new ForumAdapter(getContext());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void itemClicked(View view, int position) {
        startActivity(new Intent(getContext(), CommentsActivity.class)
                .putExtra("key", key.get(position))
                .putExtra("message", adapter.getMessageAt(position))
                .putExtra("comment_count", adapter.getCommentCount(position)));
    }

    @Override
    public void itemLongClicked(View view, final int position) {
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(adapter.getUidAt(position)) || SPHandler.containsDevUID(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            new MaterialDialog.Builder(getContext())
                    .title("Select any option")
                    .items("Edit", "Delete")
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            if (which == 0)
                                showDialogToEdit(adapter.getMessageAt(position), position);
                            else if (which == 1) {
                                FirebaseDatabase.getInstance().getReference().child("forum").child(key.get(position)).removeValue();
                                key.remove(position);
                                adapter.removeItemAtPosition(position);
                            }
                        }
                    })
                    .build()
                    .show();
        }
    }

    private void showDialogToEdit(String message, final int position) {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title("Edit post")
                .input("Your message", message, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("message", input.toString());
                        adapter.getItemAtPosition(position).message = input.toString();
                        adapter.editItemAtPosition(position);
                        FirebaseDatabase.getInstance().getReference().child("forum").child(key.get(position)).updateChildren(map);
                    }
                })
                .positiveText("Save")
                .build();
        dialog.getInputEditText().setLines(5);
        dialog.getInputEditText().setSingleLine(false);
        dialog.getInputEditText().setMaxLines(7);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        SPHandler.getInstance().clearUnreadCount();
        NotificationManagerCompat.from(getContext()).cancel("posted".hashCode());
    }
}