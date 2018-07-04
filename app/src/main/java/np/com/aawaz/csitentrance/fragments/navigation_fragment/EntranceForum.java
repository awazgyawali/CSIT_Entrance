package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import np.com.aawaz.csitentrance.objects.PopupAd;
import np.com.aawaz.csitentrance.objects.Post;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class EntranceForum extends Fragment implements
        // ValueEventListener,
        ClickListener, ChildEventListener {

    RecyclerView recyclerView;

    ProgressBar progressBar;

    ForumAdapter adapter;
    ArrayList<String> key = new ArrayList<>();
    DatabaseReference reference;
    FloatingActionButton floatingActionButton;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean running = true;

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
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progressCircleFullFeed);
        recyclerView = view.findViewById(R.id.fullFeedRecycler);
        floatingActionButton = view.findViewById(R.id.fabForumPost);
        final SwipeRefreshLayout forumSwipeRefresh = view.findViewById(R.id.forumSwipeRefresh);
        forumSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Snackbar.make(view, "Posts will updated in real time.", Snackbar.LENGTH_SHORT);
                forumSwipeRefresh.setRefreshing(false);
            }
        });

        new EventSender().logEvent("orchid_forum");
        view.findViewById(R.id.forum_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupAd adToShow = new PopupAd();
                adToShow.banner_image = "orchid";
                adToShow.address = ("Bijaya chowk, Behind Maitinepal, Kathmandu");
                adToShow.phone = ("01-4479744");
                adToShow.phone = ("Orchid International College");
                adToShow.website = ("http://www.oic.edu.np/");
                adToShow.detail = ("College aims to provide value based quality education that will enable the students to embrace the challenges of the modern world and establish the foundation for a successful future. Well equipped infrastructure, competent and committed faculty members, effective management and well disciplined students are the major four pillars of Orchid International College.");

                ACHSDialog dialog = new ACHSDialog();
                dialog.setContext(getContext());
                dialog.setAd(adToShow);
                dialog.show(getChildFragmentManager(), "orchid");
            }
        });
        view.findViewById(R.id.call_orchid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "01-4479744", null)));
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), PostForumActivity.class), 201);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("forum_data/posts");
        super.onActivityCreated(savedInstanceState);

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        reference.keepSynced(true);
        reference.orderByChild("time_stamp").limitToLast(50).addChildEventListener(this);
        fillRecyclerView();
        handleIntent();
    }

    private void handleIntent() {
        String post_id = getArguments().getString("post_id");
        if (post_id != null && !post_id.equals("new_post") && !MainActivity.openedIntent) {
            startActivity(new Intent(getContext(), CommentsActivity.class)
                    .putExtra("key", post_id)
                    .putExtra("message", "Entrance Forum"));
            MainActivity.openedIntent = true;
        }
    }

    private void fillRecyclerView() {
        adapter = new ForumAdapter(getActivity(), true);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;
        SPHandler.getInstance().clearUnreadCount();
        NotificationManager managerCompat = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        managerCompat.cancel("posted".hashCode());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201 && resultCode == 200) {
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        running = false;
        reference.removeEventListener(this);
    }

    @Override
    public void itemClicked(View view, int position) {
        startActivity(new Intent(getContext(), CommentsActivity.class)
                .putExtra("key", key.get(position))
                .putExtra("message", adapter.getMessageAt(position))
                .putExtra("comment_count", adapter.getCommentCount(position)));
    }

    @Override
    public void itemLongClicked(View v, final int position) {
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
                                FirebaseDatabase.getInstance().getReference().child("forum_data/posts").child(key.get(position)).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("forum_data/comments").child(key.get(position)).removeValue();
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
                        FirebaseDatabase.getInstance().getReference().child("forum_data/posts").child(key.get(position)).updateChildren(map);
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
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        progressBar.setVisibility(View.GONE);
        Post newPost = dataSnapshot.getValue(Post.class);
        String currentKey = dataSnapshot.getKey();
        if (newPost.author != null) {
            if (mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0 || newPost.uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                recyclerView.scrollToPosition(0);
            adapter.addToTop(newPost, dataSnapshot.getKey());
            key.add(0, currentKey);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Post editedPost = dataSnapshot.getValue(Post.class);
        int index = key.indexOf(dataSnapshot.getKey());
        if (editedPost.author != null) {
            adapter.editItemAtPosition(editedPost, index);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        int index = key.indexOf(dataSnapshot.getKey());
        adapter.removeItemAtPosition(index);
        key.remove(index);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}