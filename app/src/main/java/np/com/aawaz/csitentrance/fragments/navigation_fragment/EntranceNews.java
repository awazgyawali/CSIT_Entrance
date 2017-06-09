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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.adapters.NewsAdapter;
import np.com.aawaz.csitentrance.objects.News;


public class EntranceNews extends Fragment implements ValueEventListener {

    RecyclerView recy;
    NewsAdapter newsAdapter;
    private LinearLayout errorLayout;
    ProgressBar progress;
    DatabaseReference reference;


    private String mUrl;
    private String mTitle;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Setting the data
        readyAdapter();
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

    public void readyAdapter() {
        newsAdapter = new NewsAdapter(getContext());
        recy.setLayoutManager(new LinearLayoutManager(getContext()));
        recy.setAdapter(newsAdapter);
        reference = FirebaseDatabase.getInstance().getReference().child("news");
    }

    private void addOneTimeListener() {
        errorLayout.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        reference.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot child : dataSnapshot.getChildren())
            newsAdapter.addToTop(child.getValue(News.class));
        progress.setVisibility(View.GONE);
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