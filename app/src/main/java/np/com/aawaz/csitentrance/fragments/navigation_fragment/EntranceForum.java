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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.CommentsActivity;
import np.com.aawaz.csitentrance.activities.MainActivity;
import np.com.aawaz.csitentrance.activities.PostForumActivity;
import np.com.aawaz.csitentrance.adapters.ForumAdapter;
import np.com.aawaz.csitentrance.fragments.other_fragments.ACHSDialog;
import np.com.aawaz.csitentrance.interfaces.ClickListener;
import np.com.aawaz.csitentrance.interfaces.ResponseListener;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.Post;
import np.com.aawaz.csitentrance.objects.SPHandler;
import np.com.aawaz.csitentrance.services.NetworkRequester;

//todo here are some todos, firebase bhanda different use garera
public class EntranceForum extends Fragment implements
        // ValueEventListener,
        ClickListener {

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
                startActivityForResult(new Intent(getActivity(), PostForumActivity.class), 200);
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
        reference = database.getReference().child("forum_data/posts");
        super.onActivityCreated(savedInstanceState);
        addListener();
        handleIntent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == 200) {
            addListener();//post gare pachi refresh
        }
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

    private void addListener() {
        recyclerView.setVisibility(View.GONE);
        errorPart.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        reference.keepSynced(true);

        //reference.orderByChild("time_stamp").limitToLast(50).addListenerForSingleValueEvent(this); testing right now

        new NetworkRequester("https://csit-entrance-7d58.firebaseio.com/forum_data/posts.json?orderBy=%22time_stamp%22&limitToLast=50", new ResponseListener() {
            @Override
            public void onSuccess(String response) {
                SPHandler.getInstance().addForumData(response);
                if (!running)
                    return;
                NotificationManagerCompat.from(getContext()).cancel("posted".hashCode());
                SPHandler.getInstance().clearUnreadCount();
                parseResponse(response);
            }

            @Override
            public void onFailure() {
                if (SPHandler.getInstance().getForumData() == null) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    errorPart.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load posts.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    parseResponse(SPHandler.getInstance().getForumData());
                    Toast.makeText(getContext(), "Check your internet connection.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void parseResponse(String response) {
        fillRecyclerView();
        adapter.notifyDataSetChanged();
        key.clear();
        progressBar.setVisibility(View.GONE);
        try {
            JSONObject object = new JSONObject(response);
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String currentKey = keys.next();
                Post newPost = new Gson().fromJson(object.getJSONObject(currentKey).toString(), Post.class);
                if (newPost.author != null) {
                    adapter.addToTop(newPost);
                    key.add(0, currentKey);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onDataChange(DataSnapshot dataSnap) {
//        fillRecyclerView();
//        adapter.notifyDataSetChanged();
//        key.clear();
//        progressBar.setVisibility(View.GONE);
//        for (DataSnapshot dataSnapshot : dataSnap.getChildren()) {
//            Post newPost = dataSnapshot.getValue(Post.class);
//            if (newPost.author == null)
//                return;
//            newPost.comment_count = (int) dataSnapshot.child("comments").getChildrenCount();
//            adapter.addToTop(newPost);
//            key.add(0, dataSnapshot.getKey());
//        }
//    }
//
//    @Override
//    public void onCancelled(DatabaseError databaseError) {
//        Toast.makeText(getContext(), "Failed to load posts.",
//                Toast.LENGTH_SHORT).show();
//        swipeRefreshLayout.setVisibility(View.GONE);
//        errorPart.setVisibility(View.VISIBLE);
//        progressBar.setVisibility(View.GONE);
//    }

    private void fillRecyclerView() {
        progressBar.setVisibility(View.GONE);
        adapter = new ForumAdapter(getContext());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        running = false;
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
}