package np.com.aawaz.csitentrance.fragments.navigation_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.NewsAdapter;
import np.com.aawaz.csitentrance.objects.News;


public class EntranceNews extends Fragment implements ChildEventListener {

    RecyclerView recy;
    NewsAdapter newsAdapter;
    ProgressBar progress;
    DatabaseReference reference;
    ArrayList<String> key = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private LinearLayout errorLayout;
    private String mUrl;
    private String mTitle;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Setting the data
        addOneTimeListener();
        appIndexing();
    }

    private void appIndexing() {
        mUrl = "http://csitentrance.brainants.com/news";
        mTitle = "Latest news about the BSc CSIT entrance exam.";
    }

    public com.google.firebase.appindexing.Action getAction() {
        return Actions.newView(mTitle, mUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    public void onStop() {
        FirebaseUserActions.getInstance().end(getAction());
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recy = (RecyclerView) view.findViewById(R.id.newsFeedRecy);
        errorLayout = (LinearLayout) view.findViewById(R.id.errorNews);
        progress = (ProgressBar) view.findViewById(R.id.progressbarNews);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOneTimeListener();
            }
        });
    }


    private void addOneTimeListener() {
        reference = FirebaseDatabase.getInstance().getReference().child("news");
        errorLayout.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        newsAdapter = new NewsAdapter(getContext());
        linearLayoutManager = new LinearLayoutManager(getContext());
        reference.addChildEventListener(this);
        recy.setLayoutManager(linearLayoutManager);
        recy.setAdapter(newsAdapter);
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        newsAdapter.addToTop(dataSnapshot.getValue(News.class));
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
        key.add(0, dataSnapshot.getKey());
        progress.setVisibility(View.GONE);
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
        News post = dataSnapshot.getValue(News.class);
        newsAdapter.editItemAtPosition(position, post);
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
        newsAdapter.removeItemAtPosition(position);
        key.remove(position);
    }


    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        progress.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }
}