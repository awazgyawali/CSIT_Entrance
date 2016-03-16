package np.com.aawaz.csitentrance.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.activities.AddPost;
import np.com.aawaz.csitentrance.adapters.ForumAdapter;
import np.com.aawaz.csitentrance.misc.Singleton;


public class EntranceForum extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout errorPart;
    FloatingActionButton fab;
    ArrayList<String> poster = new ArrayList<>(),
            messages = new ArrayList<>(),
            postId = new ArrayList<>(),
            comments = new ArrayList<>();

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
        errorPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchFromServer();
                errorPart.setVisibility(View.GONE);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), AddPost.class), 100);
            }
        });
        fab.hide();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchFromServer();
    }

    private void fillRecyclerView() {
        fab.show();
        progressBar.setVisibility(View.GONE);
        ForumAdapter adapter = new ForumAdapter(getContext(), poster, messages, comments, postId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setClickListener(new ForumAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(new RecyclerView(getContext()));
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {

                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });
    }

    private void fetchFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        errorPart.setVisibility(View.GONE);
        JsonArrayRequest request = new JsonArrayRequest(getString(R.string.getForum), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                poster.clear();
                messages.clear();
                postId.clear();
                comments.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);

                        poster.add(object.getString("poster"));
                        messages.add(object.getString("message"));
                        postId.add(object.getString("id"));
                        comments.add(object.getString("comments"));
                    } catch (Exception ignored) {
                    }
                }
                fillRecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorPart.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
        Singleton.getInstance().getRequestQueue().add(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 100)
            fetchFromServer();
    }
}